package ch.donkeycode.backendui;

import ch.donkeycode.backendui.common.Store;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
@Service
public class SimpleWebSocketServer {

    private final ExecutorService executorService;

    private final Store<String, String> wsClientsStore = new Store<>(stringStringStoreParamsBuilder -> stringStringStoreParamsBuilder.keyExtractor(s -> s).onSizeChanged(integer -> System.out.println("Size changed")));

    @PostConstruct
    public void start() {
        executorService.execute(this::startSocket);
    }

    public void startSocket() {
        int port = 8081; // Port für den ServerSocket
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("WebSocket Server gestartet. Warte auf Verbindung...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client verbunden: " + clientSocket);

                // WebSocket-Handshake durchführen
                performHandshake(clientSocket.getInputStream(), clientSocket.getOutputStream());

                // Nachricht an den Client senden
                sendWebSocketMessage(clientSocket.getOutputStream(), "Hello Client");

                // Verbindung schließen
                clientSocket.close();
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static void performHandshake(InputStream input, OutputStream output) throws IOException, NoSuchAlgorithmException {
        // Lesen des HTTP-Handshakes
        StringBuilder request = new StringBuilder();
        int data;
        while ((data = input.read()) != -1) {
            request.append((char) data);
            if (request.toString().contains("\r\n\r\n")) {
                break;
            }
        }

        // Generieren des WebSocket-Antwort-Headers
        String key = request.toString().split("Sec-WebSocket-Key: ")[1].split("\r\n")[0].trim();
        String acceptKey = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes()));

        String response = "HTTP/1.1 101 Switching Protocols\r\n" +
                "Upgrade: websocket\r\n" +
                "Connection: Upgrade\r\n" +
                "Sec-WebSocket-Accept: " + acceptKey + "\r\n" +
                "\r\n";

        output.write(response.getBytes());
        output.flush();
    }

    private static void sendWebSocketMessageMasked(OutputStream output, String message) throws IOException {
        // Nachricht kodieren und senden
        byte[] rawData = message.getBytes();
        byte[] frame = new byte[10 + rawData.length];
        frame[0] = (byte) 0x81; // FIN-Bit gesetzt, Opcode: Text
        frame[1] = (byte) (0x80 | rawData.length); // Maskierung aktivieren und Länge der Payload

        // Mask-Schlüssel generieren und in den Frame einfügen
        byte[] mask = new byte[4];
        for (int i = 0; i < 4; i++) {
            mask[i] = (byte) (Math.random() * 256);
            frame[i + 2] = mask[i];
        }

        // Payload maskieren und in den Frame einfügen
        for (int i = 0; i < rawData.length; i++) {
            frame[i + 6] = (byte) (rawData[i] ^ mask[i % 4]);
        }

        // Frame senden
        output.write(frame);
        output.flush();
    }

    private static void sendWebSocketMessage(OutputStream output, String message) throws IOException {
        byte[] rawData = message.getBytes("UTF-8");
        int rawDataLength = rawData.length;

        output.write(0x81); // FIN-Bit gesetzt, Opcode: Text
        if (rawDataLength <= 125) {
            output.write(rawDataLength);
        } else if (rawDataLength <= 65535) {
            output.write(126);
            output.write((rawDataLength >>> 8) & 0xFF);
            output.write(rawDataLength & 0xFF);
        } else {
            output.write(127);
            output.write((rawDataLength >>> 56) & 0xFF);
            output.write((rawDataLength >>> 48) & 0xFF);
            output.write((rawDataLength >>> 40) & 0xFF);
            output.write((rawDataLength >>> 32) & 0xFF);
            output.write((rawDataLength >>> 24) & 0xFF);
            output.write((rawDataLength >>> 16) & 0xFF);
            output.write((rawDataLength >>> 8) & 0xFF);
            output.write(rawDataLength & 0xFF);
        }
        output.write(rawData);
        output.flush();
    }
}
