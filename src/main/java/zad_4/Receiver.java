package zad_4;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

public class Receiver {
    private Socket socket;

    public Receiver(String address) {
        try {
            socket = new Socket(address, 5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receivedData() throws IOException {
        InputStream socketIn = socket.getInputStream();
        while(true){
            byte[] bytes = socketIn.readAllBytes();
            if(bytes.length != 0) {
                System.out.println(Arrays.toString(bytes));
            }
        }
    }
}
