package ch.donkeycode.examples.persons.services;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.springframework.stereotype.Service;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;


@Service
public class WebCamService {

    public List<CamInfo> getAvailableCams() {
        return Webcam.getWebcams().stream()
                .peek(webcam -> webcam.setCustomViewSizes(WebcamResolution.HD.getSize()))
                .flatMap(webcam -> Stream.concat(
                        Arrays.stream(webcam.getViewSizes())
                                .map(dimension -> CamInfo.builder()
                                        .name(webcam.getName())
                                        .resolution(dimension)
                                        .build()),
                        Stream.of(CamInfo.builder()
                                .name(webcam.getName())
                                .resolution(WebcamResolution.HD.getSize())
                                .build())))
                .toList();
    }

    public BufferedImage takeImage(CamInfo camInfo) {
        Webcam webcam = Webcam.getWebcamByName(camInfo.getName());
        webcam.setViewSize(camInfo.getResolution());
        webcam.open();

        val image = webcam.getImage();
        webcam.close();

        return image;
    }

    public CapturingHandle startCapturing(CamInfo camInfo, Consumer<BufferedImage> imageConsumer) {

        val running = new AtomicReference<>(true);
        val stopped = new CompletableFuture<Void>();

        val capturingThread = new Thread(() -> {
            Webcam webcam = Webcam.getWebcamByName(camInfo.getName());
            webcam.setViewSize(camInfo.getResolution());
            webcam.open();

            while (running.get()) {
                running.updateAndGet(isRunning -> {
                    val image = webcam.getImage();
                    if (isRunning) {
                        imageConsumer.accept(image);
                    }
                    return isRunning;
                });
            }

            webcam.close();
            stopped.complete(null);
        });
        capturingThread.start();

        return new CapturingHandle(running, stopped);
    }

    @RequiredArgsConstructor
    @Getter
    public static class CapturingHandle {

        private final AtomicReference<Boolean> running;
        private final CompletableFuture<Void> stopped;

        public void stop() {
            running.set(false);
        }

        public void waitUntilStopped() {
            stopped.join();
        }
    }

    @Value
    @Builder
    public static class CamInfo {
        String name;
        Dimension resolution;
    }
}
