package zad_2;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class SerialPortListenerReceiver implements SerialPortDataListener {
    private final Port port;

    public SerialPortListenerReceiver(Port port) {
        this.port = port;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        byte[] receivedData = serialPortEvent.getReceivedData();
        port.setLastMessage(receivedData);
        port.setLastMessageReaded(false);
    }
}
