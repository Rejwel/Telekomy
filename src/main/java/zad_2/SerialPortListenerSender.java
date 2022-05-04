package zad_2;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.util.Arrays;

public class SerialPortListenerSender implements SerialPortDataListener {

    private final Port port;

    public SerialPortListenerSender(Port port) {
        this.port = port;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        System.out.println("dostalem");
        byte[] receivedData = serialPortEvent.getReceivedData(); //czytanie danych odebranych
        if (Arrays.equals(receivedData, new byte[]{99})) {
            System.out.println("99");
            Port.setNineNine(true); //ustawienie zmiennej;
        }
        if (Arrays.equals(receivedData, new byte[]{11})) {
            System.out.println("11");
            Port.setOneOne(true); //ustawienie zmiennej;
        }
        if (Arrays.equals(receivedData, new byte[]{02})) {
            System.out.println("02");
            Port.setTwo(true); //ustawienie zmiennej;
        }
    }

}
