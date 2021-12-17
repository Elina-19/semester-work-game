package ru.itis.protocol;

import java.io.IOException;
import java.io.OutputStream;

public class MessageOutputStream extends OutputStream {
    private OutputStream outputStream;
    public byte firstByte;
    public byte secondByte;

    public MessageOutputStream(OutputStream outputStream) {
        this.firstByte = (byte) Constants.VERSION;
        this.secondByte = (byte) (Constants.VERSION * 10 % 10);
        this.outputStream = outputStream;
    }

    public void writeMessage(Message message) throws IOException {
        outputStream.write(firstByte);
        outputStream.write(secondByte);

        byte type = message.getType();
        byte[] data = message.getData();

        outputStream.write(type);

        int length = data.length;
        outputStream.write((byte)(length>>8));
        outputStream.write((byte)length);

    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        outputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
