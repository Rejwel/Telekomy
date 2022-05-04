package zad_2;

import com.fazecast.jSerialComm.SerialPort;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class Port implements AutoCloseable {
    private final SerialPort port;
    private boolean lastMessageReaded = true;
    private byte[] lastMessage = {0};
    private byte[][] messageToSend;
    private static List<Byte> deliveredMessage = new ArrayList<>();
    private static List<Byte> finalDeliveredMessage = new ArrayList<>();
    private static boolean nineNine = false;
    private static boolean oneOne = false;
    private static boolean two = false;

    private byte[] deliveredBytes = new byte[0];
    private int numerOfBlockToSend = -1;
    private int counter = 0;
    private byte[] lastSendMessage = new byte[2];
    private boolean shouldContinue = true;

    public Port(SerialPort port) throws IOException, InterruptedException {
        this.port = port;
        port.openPort();
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        port.addDataListener(new SerialPortListenerReceiver(this));
        whatToDoReceiverSwitch();
    }

    public Port(SerialPort port, byte[][] messageToSend) throws InterruptedException, IOException {
        this.port = port;
        port.openPort();
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        port.addDataListener(new SerialPortListenerSender(this));
        this.messageToSend = messageToSend;
        whatToDoSenderSwitch();
    }

    public static boolean isNineNine() {
        return nineNine;
    }

    public static void setNineNine(boolean nineNine) {
        Port.nineNine = nineNine;
    }

    public static boolean isOneOne() {
        return oneOne;
    }

    public static void setOneOne(boolean oneOne) {
        Port.oneOne = oneOne;
    }

    public static boolean isTwo() {
        return two;
    }

    public static void setTwo(boolean two) {
        Port.two = two;
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

    public static List<Byte> getFinalDeliveredMessage() {
        return finalDeliveredMessage;
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
                while (true) {
                    if (two || oneOne || nineNine) {
                        lastMessageReaded = true;

                        if (counter == messageToSend.length && oneOne) {
                            port.writeBytes(new byte[]{(byte) 0xff}, 1); //wszystko wyslane, zakoncz program
                            break;
                        }

                        if (two) {
                            port.writeBytes(lastSendMessage, lastSendMessage.length); //ponownie wyslji wiadomosc
                            System.out.println("zle");
                        }

                        if (oneOne || nineNine) {
                            numerOfBlockToSend++;
                            System.out.println(numerOfBlockToSend);
                            byte[] combine = messagePlusCheckSum(messageToSend[numerOfBlockToSend]); //tworzenie wiadomosci i laczenie go z checksuma;
                            lastSendMessage = combine; //przypisanie do wartosci
                            port.writeBytes(combine, combine.length); //wyslji wiadomosc
                            counter++;
                        }
                    }
                    oneOne = false;
                    two = false;
                    nineNine = false;
                    Thread.sleep(1);
                }
                break;
            }
            Thread.sleep(1);
        }
    }

    private void whatToDoReceiverSwitch() throws IOException, InterruptedException {
        port.writeBytes(new byte[]{99}, 1); //wyslij wiadomosc inicjujaca;

        SerialPortListenerReceiver.setCanContinue(false);
        while (shouldContinue) {

        }
    }

    public void send11Message() {
        port.writeBytes(new byte[]{11}, 1);
    }

    public static void saveMessage() throws IOException { //zapisz wiadomosc do pliku
        FileOutputStream fo = new FileOutputStream("src/main/resources/wynik.bmp");
        byte[] temp = new byte[finalDeliveredMessage.size()];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = finalDeliveredMessage.get(i);
        }
        byte[] result = Base64.getDecoder().decode(temp);
        fo.write(result);
        fo.close();
        System.out.println("Wyslane");
    }

    public boolean isLastMessageReaded() {
        return lastMessageReaded;
    }

    public void setLastMessageReaded(boolean lastMessageReaded) {
        this.lastMessageReaded = lastMessageReaded;
    }

    private void addByteToArray(byte[] message) {
        byte[] newTab = new byte[deliveredBytes.length + message.length];
        for (int i = 0; i < deliveredBytes.length; i++) {
            newTab[i] = deliveredBytes[i];
        }
        int counter = 0;
        for (int i = deliveredBytes.length; i < newTab.length; i++) {
            newTab[i] = message[counter];
            counter++;
        }
        deliveredBytes = newTab;
    }

    private byte[] messageWithoutCheckSum(byte[] combine) {
        byte[] message = new byte[combine.length - 2];
        for (int i = 0; i < message.length; i++) {
            message[i] = combine[i];
        }
        return message;
    }

    private byte[] fromListToArray(List<Byte> list) {
        byte[] result = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    private byte[] messagePlusCheckSum(byte[] message) {
        byte[] messagePlusSum = new byte[message.length + 2];
        for (int i = 0; i < message.length; i++) {
            messagePlusSum[i] = message[i];
        }
        byte[] checkSum = Data.countCheckSum(message);
        messagePlusSum[messagePlusSum.length - 2] = checkSum[0];
        messagePlusSum[messagePlusSum.length - 1] = checkSum[1];
        return messagePlusSum;
    }

    private void removesZeros() {
        int zeros = deliveredBytes[deliveredBytes.length - 1];
        byte[] result = new byte[deliveredBytes.length - zeros - 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = deliveredBytes[i];
        }
        deliveredBytes = result;
    }

    public static void removesZerosFromList(List<Byte> list) {
        int zeros = list.get(list.size() - 1);
        List<Byte> result = new ArrayList<>(list);
        int amountOfSubList = list.size() - zeros - 1;
        finalDeliveredMessage = result.subList(0, amountOfSubList);
    }

    public static List<Byte> removeCheckSum(List<Byte> list) {
        List<Byte> result = new ArrayList<>(list);
        return result.subList(0, list.size() - 2);
    }

    public static void addToFinalList(List<Byte> list) {
        finalDeliveredMessage.addAll(list);
    }


}
