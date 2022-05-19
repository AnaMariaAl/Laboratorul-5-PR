package com.company;

import java.io.*;
import java.net.*;
import  javax.sound.sampled.*;

public class StackClient {

    static AudioInputStream ais;
    static AudioFormat format;
    static int port = 50005;
    static int sampleRate = 44100;

    static DataLine.Info dataLineInfo;
    static SourceDataLine sourceDataLine;

    public static void main(String args[]) throws Exception {
        System.out.println("Server started at port:" + port);
        System.setProperty("java.net.preferIPv4Stack", "true");

        try{
            InetAddress group = InetAddress.getByName("225.6.7.8");
            MulticastSocket mSocket = new MulticastSocket(port);
            mSocket.setReuseAddress(true);
            mSocket.joinGroup(group);

            byte[] receiveData = new byte[4096]; //datele care urmeaza sa le primim de la packet

            format = new AudioFormat(sampleRate, 16, 2, true, false);
            dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(format);
            sourceDataLine.start();

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            ByteArrayInputStream baiss = new ByteArrayInputStream(receivePacket.getData());

            while (true)
            {
                mSocket.receive(receivePacket);
                ais = new AudioInputStream(baiss, format, receivePacket.getLength());
                toSpeaker(receivePacket.getData());
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void toSpeaker(byte soundbytes[]) {
        try
        {
            System.out.println("At the speaker");
            sourceDataLine.write(soundbytes, 0, soundbytes.length);
        } catch (Exception e) {
            System.out.println("Not working in speakers...");
            e.printStackTrace();
        }
    }
}