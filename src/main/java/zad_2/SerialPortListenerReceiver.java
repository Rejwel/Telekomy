package zad_2;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SerialPortListenerReceiver implements SerialPortDataListener {
    private final Port port;
    private static boolean canContinue = true;

    public SerialPortListenerReceiver(Port port) {
        this.port = port;
    }

    public static boolean isCanContinue() {
        return canContinue;
    }

    public static void setCanContinue(boolean canContinue) {
        SerialPortListenerReceiver.canContinue = canContinue;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        byte[] receivedData = serialPortEvent.getReceivedData(); //czytanie danych odebranych
        if (Port.getDeliveredMessage().size() == 0) { //jezeli pusta tablica moze byc koniec programu
            if (Arrays.equals(receivedData, new byte[]{(byte) 0xff})) {
                System.out.println("koniec");
                Port.removesZerosFromList(Port.getFinalDeliveredMessage()); // usun zbedne zera
                try {
                    System.out.println("koniec2");
                    Port.saveMessage(); // zakoncz program
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for (byte receivedDatum : receivedData) {
            Port.getDeliveredMessage().add(receivedDatum);
        }
        if (Port.getDeliveredMessage().size() == 130) {
            System.out.println("cos");
            List<Byte> list = Port.removeCheckSum(Port.getDeliveredMessage()); //usun sprawdzenie z wiadomosci
            Port.addToFinalList(list); //dodaj wiadomosc do glownej listy
            Port.setToNewDeliveredMessage(); // wyzeruj tablice
            port.send11Message(); // wyslij potwierdzenie
        }
    }
}
