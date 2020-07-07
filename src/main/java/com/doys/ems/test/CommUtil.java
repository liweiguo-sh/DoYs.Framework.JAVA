package com.doys.ems.test;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
public class CommUtil implements SerialPortEventListener {
    private static final String PORT_NAME = "COM4";                 // -- 端口 --
    private static final int BIT_RATE = 2400;                       // -- 波特率 --
    public static final int DATA_BITS = SerialPort.DATABITS_8;
    public static final int STOP_BIT = SerialPort.STOPBITS_1;
    public static final int PARITY_BIT = SerialPort.PARITY_EVEN;    // -- 偶校验 --

    private static SerialPort serialPort;
    private static InputStream in;
    private static OutputStream out;
    private static CommUtil commUtil;
    // ------------------------------------------------------------------------
    private CommUtil() {
    }
    public void init() {
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(PORT_NAME);
            if (portIdentifier.isCurrentlyOwned()) {
                System.out.println("Error: Port is currently in use");
            }
            else if (portIdentifier.getPortType() == 1) {
                serialPort = (SerialPort) portIdentifier.open(PORT_NAME, 1000);
                serialPort.setSerialPortParams(BIT_RATE, DATA_BITS, STOP_BIT, PARITY_BIT);

                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();

                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);
            }
            else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static CommUtil getInstance() {
        if (commUtil == null) {
            commUtil = new CommUtil();
            commUtil.init();
        }
        return commUtil;
    }
    public void close() {
        try {
            in.close();
            out.close();
            serialPort.notifyOnDataAvailable(false);
            serialPort.removeEventListener();
            serialPort.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        try {
            byte[] bytes = hexStrToByteArray(message);
            out.write(bytes);
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            case SerialPortEvent.DATA_AVAILABLE:
                String str = receive();
                System.out.println(str);
                break;
        }
    }
    public String receive() {
        byte[] buffer = new byte[128];
        int data;
        String result = null;
        try {
            int len = 0;
            while ((data = in.read()) > -1) {
                buffer[len++] = (byte) data;
            }
            byte[] copyValue = new byte[len];
            System.arraycopy(buffer, 0, copyValue, 0, len);
            result = ByteArrayToString(copyValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // -- 16进制转byte数组 --
    public byte[] hexStrToByteArray(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new byte[0];
        }
        byte[] byteArray = new byte[str.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {
            String subStr = str.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte) Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }
    public String ByteArrayToString(byte[] by) {
        String str = "";
        for (int i = 0; i < by.length; i++) {
            String hex = Integer.toHexString(by[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            str += hex.toUpperCase();
        }
        return str;
    }

    public static void main(String[] args) {
        String cmd = "";

        CommUtil commUtil = null;
        // ------------------------------------------------
        try {
            cmd = "FE FE FE 68 10 AA AA AA AA AA AA AA 03 03 0A 81 00 AF 16";
            cmd = cmd.replaceAll(" ", "");

            commUtil = CommUtil.getInstance();

            commUtil.send(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            commUtil.close();
        }
    }
}