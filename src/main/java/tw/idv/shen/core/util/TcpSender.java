package tw.idv.shen.core.util;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpSender {
    private static final String DEVICE_IP = "10.2.18.224";
    private static final int DEVICE_PORT = 8080;
    private static final int TIMEOUT_MS = 2000;

    public static void sendToDevice(String message) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(DEVICE_IP, DEVICE_PORT), TIMEOUT_MS);
            OutputStream out = socket.getOutputStream();
            out.write(message.getBytes());
            out.flush();
            System.out.println("TCP 已送出號碼：" + message);
        } catch (Exception e) {
            System.err.println("TCP 傳送失敗：" + e.getMessage());
        }
    }
}

