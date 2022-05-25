package zad_4;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

public class Receiver {
    private Socket socket;

    public Receiver(String address) {
        try {
            while (socket == null) {
                socket = new Socket(address, 5000);
            }
            receivedData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receivedData() throws IOException {
        InputStream socketIn = socket.getInputStream();
        while(true){
            byte[] bytes = new byte[4];
            for (int i =0; i < 4; i++) {
                bytes[i] = (byte) socketIn.read();
            }
            System.out.println(Arrays.toString(bytes));
        }
    }
}
