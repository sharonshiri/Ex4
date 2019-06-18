package com.example.ex4;

import android.util.Log;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClient implements Runnable {

    public static final String TAG = TcpClient.class.getSimpleName();
    // ip and port
    private String serverIP;
    private int serverPort;
    // message to send to the server
    private String mServerMessage;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    public PrintWriter mBufferOut;

    // singleton
    private static TcpClient tcpClientSingletone = null;
    public static TcpClient getInstance()
    {
        if (tcpClientSingletone == null) {
            tcpClientSingletone = new TcpClient();
        }
        return tcpClientSingletone;
    }

    /**
     * setServerIP
     * @param ip
     * set value of ip server
     */
    public void setServerIP(String ip){

        serverIP = ip;
    }

    /**
     * setServerPort
     * @param port
     * set value of port server
     */
    public void setServerPort(int port){

        serverPort = port;
    }

    /**
     * sendMessage
     * @param message
     * send message to the server
     */
    public void sendMessage(final String message) {
        // thread
        Runnable runnable = new Runnable() {
            @Override
            // do the run in the thread, send the message
            public void run() {
                if (mBufferOut != null) {
                    Log.d(TAG, "Sending: " + message);
                    mBufferOut.println(message);
                    mBufferOut.flush();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    /**
     * stopClient
     * stop the client/server communication
     */
    public void stopClient() {
        mRun = false;
        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }
        mBufferOut = null;
        mServerMessage = null;
    }

    /**
     * run
     * connect to the server and send the message
     */
    public void run() {
        mRun = true;
        try {
            InetAddress serverAddr = InetAddress.getByName(serverIP);
            Log.d("TCP Client", "C: Connecting...");
            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, serverPort);
            try {
                //sends the message to the server
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                while (mRun) {
                }
                Log.d("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");
            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            }
            finally {
                socket.close();
            }
        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }

    }
}