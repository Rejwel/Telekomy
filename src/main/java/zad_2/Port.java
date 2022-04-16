package zad_2;

import com.fazecast.jSerialComm.SerialPort;


import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Port implements AutoCloseable {
    private final SerialPort port;
    private boolean lastMessageReaded = true;
    private byte[] lastMessage = {'0'};
    private byte[][] messageToSend;
    private List<Byte> deliveredMessage = new ArrayList<>();
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

    public Port(SerialPort port, byte[][] messageToSend) throws InterruptedException {
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

    private void whatToDoSenderSwitch() throws InterruptedException {

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
                        }

                        if (Arrays.equals(lastMessage, new byte[]{'1', '1'})) {
                            numerOfBlockToSend++;
                            byte[] combine = messagePlusCheckSum(messageToSend[numerOfBlockToSend]);
                            lastSendMessage = combine;
                            port.writeBytes(combine, combine.length);
                            counter++;
                        }
                    }
                    Thread.sleep(10);
                }
                break;
            }
            Thread.sleep(1000);
        }
    }

    private void whatToDoReceiverSwitch() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        while (!Arrays.equals(lastMessage, new byte[]{'9', '9'})) {
            if (System.currentTimeMillis() - start > 5000) {
                start = System.currentTimeMillis();
                port.writeBytes(new byte[]{'9', '9'}, 2);
                System.out.println(lastMessage);
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
                byte[] messageWithoutChecksum = messageWithoutCheckSum();
                if (Arrays.equals(Data.countCheckSum(messageWithoutChecksum), checkSumFromSender)) {
                    addByteToMessage(messageWithoutChecksum);
                    port.writeBytes(new byte[]{'1', '1'}, 2);
                    lastSendMessage = new byte[]{'1', '1'};
                } else {
                    port.writeBytes(new byte[]{'0', '2'}, 2);
                    lastSendMessage = new byte[]{'0', '2'};
                }
            }
        }
        FileOutputStream fo = new FileOutputStream("src/main/resources/cos.bmp");
        System.out.println(deliveredMessage.size());
        fo.write(fromListToArray(deliveredMessage));
        fo.close();
    }

    public boolean isLastMessageReaded() {
        return lastMessageReaded;
    }

    public void setLastMessageReaded(boolean lastMessageReaded) {
        this.lastMessageReaded = lastMessageReaded;
    }

    private void addByteToMessage(byte[] message) {
        for (byte b : message) {
            deliveredMessage.add(b);
        }
    }

    private byte[] messageWithoutCheckSum() {
        byte[] message = new byte[lastMessage.length - 2];
        for (int i = 0; i < message.length; i++) {
            message[i] = lastMessage[i];
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
}
