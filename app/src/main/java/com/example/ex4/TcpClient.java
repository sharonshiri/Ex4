package com.example.ex4;

import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
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
    // sends message received notifications
   // private OnMessageReceived mMessageListener = null;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    public PrintWriter mBufferOut;
    // used to read messages from the server
    //public BufferedReader mBufferIn;

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

    public void stopClient() {
        mRun = false;
        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

       // mMessageListener = null;
        //mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

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
                //receives the message which the server sends back
                //mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //in this while the client listens for the messages sent by the server
                while (mRun) {

                    //mServerMessage = mBufferIn.readLine();

//                    if (mServerMessage != null && mMessageListener != null) {
//                        //call the method messageReceived from MyActivity class
//                        mMessageListener.messageReceived(mServerMessage);
//                    }

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

    //Declare the interface. The method messageReceived(String message) will must be implemented in the Activity
    //class at on AsyncTask doInBackground
//    public interface OnMessageReceived {
//        public void messageReceived(String message);
//    }

}