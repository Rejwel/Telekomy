package zad_2;

import com.fazecast.jSerialComm.SerialPort;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class Port implements AutoCloseable {
    private final SerialPort port;
    private boolean lastMessageReaded = true;
    private byte[] lastMessage = {'0'};
    private byte[][] messageToSend;

    private byte[] deliveredBytes = new byte[0];
    private int numerOfBlockToSend = -1;
    private int counter = 0;
    private boolean shouldContinue = true;
    private byte[] lastSendMessage = new byte[2];

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

    public void sendAConfirmConnectMessage() {
        long start = 0;
        while (true) {
            port.writeBytes(new byte[]{'9', '9'}, 2);
        }
    }

    private void whatToDoSenderSwitch() throws InterruptedException, IOException {
        while (true) {
            if (Arrays.equals(lastMessage, new byte[]{'9', '9'})) {
                System.out.println("dupa");
                long start = System.currentTimeMillis();
                while (!Arrays.equals(lastMessage, new byte[]{'1', '1'})) {
                    if (System.currentTimeMillis() - start > 2000) {
                        start = System.currentTimeMillis();
                        port.writeBytes(new byte[]{'9', '9'}, 2);
                    }
                }

                while (true) {
                    if (!lastMessageReaded && (Arrays.equals(lastMessage, new byte[]{'0', '2'}) || Arrays.equals(lastMessage, new byte[]{'1', '1'}))) {
                        lastMessageReaded = true;


                        if (counter == messageToSend.length && Arrays.equals(lastMessage, new byte[]{'1', '1'})) {
                            port.writeBytes(new byte[]{'f', 'f'}, 2);
                            break;
                        }

                        if (Arrays.equals(lastMessage, new byte[]{'0', '2'})) {
                            port.writeBytes(lastSendMessage, lastSendMessage.length);
                            System.out.println("zle");
                        }

                        if (Arrays.equals(lastMessage, new byte[]{'1', '1'})) {
                            numerOfBlockToSend++;
                            System.out.println(numerOfBlockToSend);
                            byte[] combine = messagePlusCheckSum(messageToSend[numerOfBlockToSend]);
                            lastSendMessage = combine;
                            port.writeBytes(combine, combine.length);
                            byte[] temp = messageWithoutCheckSum(combine);
                            addByteToArray(temp);
                            counter++;
                        }
                    }
                    Thread.sleep(100);
                }
                break;
            }
            Thread.sleep(1000);
        }
        removesZeros();
        FileOutputStream fo = new FileOutputStream("src/main/resources/wynikBezWysylania.bmp");
        byte[] result = Base64.getDecoder().decode(deliveredBytes);
        fo.write(result);
        fo.close();
    }

    private void whatToDoReceiverSwitch() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        while (!Arrays.equals(lastMessage, new byte[]{'9', '9'})) {
            if (System.currentTimeMillis() - start > 5000) {
                start = System.currentTimeMillis();
                port.writeBytes(new byte[]{'9', '9'}, 2);
            }
        }

        port.writeBytes(new byte[]{'1', '1'}, 2);
        lastSendMessage = new byte[]{'1', '1'};


        while (true) {
            if (System.currentTimeMillis() - start > 500) {
                port.writeBytes(lastSendMessage, 2);
                start = System.currentTimeMillis();
            }
            if (Arrays.equals(lastMessage, new byte[]{'f', 'f'})) {
                break;
            }
            if (!lastMessageReaded && lastMessage.length > 128) {
                lastMessageReaded = true;
                start = System.currentTimeMillis();
                byte[] checkSumFromSender = new byte[2];
                checkSumFromSender[0] = lastMessage[lastMessage.length - 2];
                checkSumFromSender[1] = lastMessage[lastMessage.length - 1];
                byte[] messageWithoutChecksum = messageWithoutCheckSum(lastMessage);
                if (Arrays.equals(Data.countCheckSum(messageWithoutChecksum), checkSumFromSender)) {
                    addByteToArray(messageWithoutChecksum);
                    port.writeBytes(new byte[]{'1', '1'}, 2);
                    lastSendMessage = new byte[]{'1', '1'};
                } else {
                    port.writeBytes(new byte[]{'0', '2'}, 2);
                    lastSendMessage = new byte[]{'0', '2'};
                }
            }
        }
        removesZeros();
        FileOutputStream fo = new FileOutputStream("src/main/resources/wynik.bmp");
        byte[] result = Base64.getDecoder().decode(deliveredBytes);
        fo.write(result);
        fo.close();
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
}
