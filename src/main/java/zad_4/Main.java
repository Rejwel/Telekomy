package zad_4;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String word = "dupa";
//        Sender sender = new Sender();
//        sender.sendBytes(word.getBytes());
        Receiver receiver = new Receiver("169.254.59.39");


    }



}
