package com.axalent.presenter;
/*
 * File Name                   : 
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2016/11/11
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SmartConfigThread extends Thread {

    private static final String TAG = "SmartConfigThread";
    private static final int PORT = 9957;
    private static final int LENGTH = 120;
    private boolean flag = true;
    private String ssid;
    private String password;
    private byte[] datas = new byte[LENGTH];
    private DatagramSocket dataSocket;
    private DatagramPacket dataPacket;

    public SmartConfigThread(String ssid, String password) {
        this.ssid = ssid;
        this.password = password;
    }

    @Override
    public void run() {

        try {
            Log.i(TAG, "socket open");
            dataSocket = new DatagramSocket(9957);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        dataPacket = new DatagramPacket(datas, datas.length);
        dataPacket.setLength(LENGTH);
        dataPacket.setPort(PORT);
        dataPacket.setData(new String("axalenttest").getBytes());

        datas[3] = 120;

        System.arraycopy(ssid.getBytes(), 0, datas, 4, ssid.length());
        System.arraycopy(password.getBytes(), 0, datas, 36, password.length());

        datas[101] = 1;

        InetAddress address1 = null;

        try {
            address1 = InetAddress.getByName("239.0.0.254");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        byte[] addr = address1.getAddress();

        int i = 0;
        while (flag) {

            Log.i(TAG, "index:" + i);

            if (i >= datas.length) {
                Log.i(TAG, "reset index");
                i = 0;
            }

            addr[1] = (byte) i;
            addr[2] = datas[i];

            InetAddress address2 = null;
            try {
                address2 = InetAddress.getByAddress(addr);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            Log.i(TAG, "address:" + address2);
            dataPacket.setAddress(address2);

            try {
                dataSocket.send(dataPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                currentThread().sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            i++;

        }

        dataSocket.close();

        Log.i(TAG, "socket closed");

    }

    public void stopSend() {
        flag = false;
    }

}