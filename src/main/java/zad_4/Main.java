package zad_4;

import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SocketException {
        Enumeration<NetworkInterface> tempNetInterface = null;
        try {
            tempNetInterface = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        ArrayList<NetworkInterface> interfaces = new ArrayList<>(Collections.list(tempNetInterface));
        interfaces.removeIf(anInterface -> !anInterface.getName().contains("eth"));
        interfaces.removeIf(i -> !i.getInetAddresses().hasMoreElements());
        interfaces.removeIf(i -> !i.getInetAddresses().nextElement().getHostAddress().equals("169.254.89.194"));
        interfaces.removeIf(i -> {
            try {
                return !i.isUp();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return false;
        });
        System.out.println(interfaces);
        Socket socket = new Socket();
        System.out.println(interfaces.get(0).getInetAddresses());
        System.out.println(interfaces.get(0).getInetAddresses().nextElement().getHostAddress());

    }



}
