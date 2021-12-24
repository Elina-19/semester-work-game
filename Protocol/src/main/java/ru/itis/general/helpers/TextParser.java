package ru.itis.general.helpers;

public class TextParser implements MessageParser<String>{
    @Override
    public byte[] serializeMessage(String message) {
        char[] mess = message.toCharArray();
        byte[] messByte = new byte[mess.length*2];

        for (int i = 0; i < mess.length; i++){
            messByte[i*2] = (byte)(mess[i]>>8);
            messByte[i*2+1] = (byte)mess[i];
        }

        return messByte;
    }

    @Override
    public String deserializeMessage(byte[] message) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < message.length; i = i + 2){
            char a = (char) (message[i]<<8 | message[i+1]);
            sb.append(a);
        }

        return sb.toString();
    }
}

