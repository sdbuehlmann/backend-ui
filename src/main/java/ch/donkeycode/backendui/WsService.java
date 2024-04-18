package ch.donkeycode.backendui;

import ch.donkeycode.backendui.common.Store;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
//@Service
public class WsService {

    private final ExecutorService executorService;

    private final Store<String, String> wsClientsStore = new Store<>(stringStringStoreParamsBuilder -> stringStringStoreParamsBuilder.keyExtractor(s -> s).onSizeChanged(integer -> System.out.println("Size changed")));

    @PostConstruct
    public void start() {
        executorService.execute(this::startSocket);
    }

    @SneakyThrows
    public void startSocket() {
        ServerSocket server = new ServerSocket(8081);
        System.out.println("Server has started on 127.0.0.1:80.\r\nWaiting for a connection…");
        Socket client = server.accept();
        System.out.println("A client connected.");

        InputStream in = client.getInputStream();
        OutputStream out = client.getOutputStream();
        Scanner s = new Scanner(in, "UTF-8");

        String data = s.useDelimiter("\\r\\n\\r\\n").next();
        Matcher get = Pattern.compile("^GET").matcher(data);

        if (get.find()) {
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
            match.find();
            byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                    + "Connection: Upgrade\r\n"
                    + "Upgrade: websocket\r\n"
                    + "Sec-WebSocket-Accept: "
                    + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
                    + "\r\n\r\n").getBytes("UTF-8");
            out.write(response, 0, response.length);
        }

        Thread.sleep(1000);


        while (true) {
            val nextData = s.next();
            System.out.println(nextData);

            Thread.sleep(100);
        }
    }

    private void decode() {
        byte[] decoded = new byte[6];
        byte[] encoded = new byte[]{(byte) 198, (byte) 131, (byte) 130, (byte) 182, (byte) 194, (byte) 135};
        byte[] key = new byte[]{(byte) 167, (byte) 225, (byte) 225, (byte) 210};
        for (int i = 0; i < encoded.length; i++) {
            decoded[i] = (byte) (encoded[i] ^ key[i & 0x3]);
        }
    }

    @Value
    @Builder
    private static class WsClient {

    }

    @Value(staticConstructor = "of")
    private static class WsClientId {
        String value;
    }
}
