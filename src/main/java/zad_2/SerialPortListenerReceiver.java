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
        byte[] receivedData = serialPortEvent.getReceivedData();
        if (Port.getDeliveredMessage().size() == 0) {
            if (Arrays.equals(receivedData, new byte[]{(byte) 0xff})) {
                System.out.println("koniec");
                Port.removesZerosFromList(Port.getFinalDeliveredMessage());
                try {
                    System.out.println("koniec2");
                    Port.saveMessage();
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
            List<Byte> list = Port.removeCheckSum(Port.getDeliveredMessage());
            Port.addToFinalList(list);
            Port.setToNewDeliveredMessage();
            port.send11Message();
        }
    }
}
