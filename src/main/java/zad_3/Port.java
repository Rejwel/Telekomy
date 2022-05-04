package zad_3;

import com.fazecast.jSerialComm.SerialPort;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class Port implements AutoCloseable {
    private final SerialPort port;
    private byte[] lastMessage = {0};
    private byte[] messageToSend;
    private static List<Byte> deliveredMessage = new ArrayList<>();
    private static boolean nineNine = false;
    private byte[] deliveredBytes = new byte[0];
    private int numerOfBlockToSend = -1;
    private int counter = 0;
    private byte[] lastSendMessage = new byte[2];
    private boolean shouldContinue = true;
    private String result = "";
    private Map<String, Character> dictionary;

    public Port(SerialPort port, Map<String, Character> map) throws IOException, InterruptedException {
        this.port = port;
        port.openPort();
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        port.addDataListener(new SerialPortListenerReceiver(this));
        dictionary = map;
        whatToDoReceiverSwitch();
    }

    public Port(SerialPort port, List<Byte> messageToSend) throws InterruptedException, IOException {
        this.port = port;
        port.openPort();
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        port.addDataListener(new SerialPortListenerSender(this));
        this.messageToSend = changeToArray(messageToSend);
        whatToDoSenderSwitch();
    }

    private static byte[] changeToArray(List<Byte> list) {
        byte[] array = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static boolean isNineNine() {
        return nineNine;
    }

    public static void setNineNine(boolean nineNine) {
        Port.nineNine = nineNine;
    }

    public SerialPort getPort() {
        return port;
    }

    public static List<Byte> getDeliveredMessage() {
        return deliveredMessage;
    }

    public static void setToNewDeliveredMessage() {
        deliveredMessage = new ArrayList<>();
    }

    public void close() {
        port.closePort();
    }

    public void send(String message) {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        port.writeBytes(messageBytes, messageBytes.length);
    }

    public void setLastMessage(byte[] lastMessage) {
        this.lastMessage = lastMessage;
    }

    public byte[] getLastMessage() {
        return lastMessage;
    }

    private void whatToDoSenderSwitch() throws InterruptedException, IOException {
        while (true) {
            if (nineNine) {
                port.writeBytes(messageToSend, messageToSend.length); //wyslji wiadomosc
                Thread.sleep(1000);
                port.writeBytes(new byte[]{0x16}, 1);
                break;
            }
            Thread.sleep(1);
        }
    }

    private void whatToDoReceiverSwitch() throws IOException, InterruptedException {
        port.writeBytes(new byte[]{99}, 1); //wyslij wiadomosc inicjujaca;

        while (shouldContinue) {

        }
    }

    public void send11Message() {
        port.writeBytes(new byte[]{11}, 1);
    }

    public static void saveMessage(Map<String, Character> map) throws IOException { //zapisz wiadomosc do pliku
        FileWriter fw = new FileWriter("src/main/resources/wynik.txt");
        String result = Data.changeToString(map, deliveredMessage);
        fw.write(result);
        fw.close();
        System.out.println("Zapisane");
    }

    public Map<String, Character> getDictionary() {
        return dictionary;
    }
}
