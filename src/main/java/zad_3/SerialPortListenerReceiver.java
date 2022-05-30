package zad_3;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.IOException;
import java.util.Arrays;


public class SerialPortListenerReceiver implements SerialPortDataListener {
    private final Port port;
    private long time;

    public SerialPortListenerReceiver(Port port) {
        this.port = port;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        byte[] receivedData = serialPortEvent.getReceivedData(); //czytanie danych odebranych
        if (Arrays.equals(receivedData, new byte[]{0x16}) && System.currentTimeMillis() - time > 500) {
            try {
                System.out.println("koniec");
                Port.saveMessage(port.getDictionary()); // zakoncz program
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (byte receivedDatum : receivedData) {
            time = System.currentTimeMillis();
            Port.getDeliveredMessage().add(receivedDatum);
        }
    }
}
