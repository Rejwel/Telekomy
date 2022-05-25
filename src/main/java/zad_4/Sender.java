package zad_4;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Sender {
    private Socket socket;
    private ServerSocket serverSocket;

    public Sender() {
        try {
            serverSocket = new ServerSocket(1234);
            socket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendBytes(byte[] bytes) throws IOException {
        OutputStream socketOut = socket.getOutputStream();
        socketOut.write(bytes);
    }
}
