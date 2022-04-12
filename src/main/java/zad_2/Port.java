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
    private int numerOfBlockToSend = 0;
    private boolean start = true;
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

    private void whatToDoSenderSwitch() throws InterruptedException {
        while (true) {
            while (true) {
                System.out.println(lastMessage.toString());
                if (Arrays.equals(lastMessage, new byte[]{'9', '9'})) {
                    break;
                }
            }

            lastMessageReaded = true;
            while (true) {
                if (!Arrays.equals(lastMessage, new byte[]{'0'}) && Arrays.equals(new byte[]{lastMessage[lastMessage.length - 2], lastMessage[lastMessage.length - 1]}, new byte[]{'8', '8'}) && !lastMessageReaded) {
                    lastMessageReaded = false;
                    lastMessage = new byte[]{'1', '1'};
                    break;
                }
                port.writeBytes(new byte[]{'9', '9'}, 2);
                TimeUnit.SECONDS.sleep(3);
            }

            while (true) {
                System.out.println("dupa");
                if (!lastMessageReaded && (Arrays.equals(lastMessage, new byte[]{'0', '2'}) || Arrays.equals(lastMessage, new byte[]{'1', '1'}))) {
                    lastMessageReaded = true;
                    byte[] combine = messagePlusCheckSum(messageToSend[numerOfBlockToSend]);
                    if (messageToSend.length == 1 && shouldContinue) {
                        port.writeBytes(combine, combine.length);
                        shouldContinue = false;
                        continue;
                    }

                    if (numerOfBlockToSend == messageToSend.length - 1 && Arrays.equals(lastMessage, new byte[]{'1', '1'})) {
                        port.writeBytes(new byte[]{'f', 'f'}, 2);
                        break;
                    }


                    if (Arrays.equals(lastMessage, new byte[]{'0', '2'}) || start) {
                        port.writeBytes(combine, combine.length);
                        start = false;
                    } else if (Arrays.equals(lastMessage, new byte[]{'1', '1'})) {
                        numerOfBlockToSend++;
                        port.writeBytes(combine, combine.length);
                    }

                }

            }
            break;
        }
    }

    private void whatToDoReceiverSwitch() throws IOException, InterruptedException {
        while (true) {
            if (Arrays.equals(lastMessage, new byte[]{'9', '9'})) {
                break;
            }
            port.writeBytes(new byte[]{'9', '9'}, 2);
            TimeUnit.SECONDS.sleep(3);
            System.out.println(lastMessage.toString());
        }

        TimeUnit.SECONDS.sleep(3);
        port.writeBytes(new byte[]{'8', '8'}, 2);
        lastSendMessage = new byte[]{'8', '8'};
        TimeUnit.SECONDS.sleep(2);

        long start = 0;


        while (true) {
            if (System.currentTimeMillis() - start > 150) {
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
        FileOutputStream fo = new FileOutputStream("src/main/resources/wynik.bin");
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
