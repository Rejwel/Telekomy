package zad_3;

import com.fazecast.jSerialComm.SerialPort;

import java.util.List;
import java.util.Map;

public class PortManager {

    private static final SerialPort[] ports = SerialPort.getCommPorts();

    public static String[] getPortsNameList() {
        String[] list = new String[ports.length];
        for (int i = 0; i < ports.length; i++) {
            list[i] = i + 1 + ". " + ports[i].getSystemPortName();
        }
        return list;
    }

    public static Port getPort(int numberOnList, Map<String, Character> map) throws Exception {
        if (numberOnList < 1 || numberOnList > ports.length) {
            throw new Exception("Wybrano nieprawidłową opcję");
        }
        return new Port(ports[numberOnList - 1],map);
    }

    public static Port getPort(int numberOnList, List<Byte> messageToSend) throws Exception {
        if (numberOnList < 1 || numberOnList > ports.length) {
            throw new Exception("Wybrano nieprawidłową opcję");
        }
        return new Port(ports[numberOnList - 1], messageToSend);
    }
}
