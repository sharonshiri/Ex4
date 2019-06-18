package com.example.ex4;

import android.os.AsyncTask;
import android.util.Log;

public class ConnectTask extends AsyncTask<String, String, TcpClient> {

    TcpClient mTcpClient;

    /**
     * Name: ConnectTask
     * @param tcp
     * constructor
     */
    public ConnectTask(TcpClient tcp) {
        mTcpClient = tcp;
    }

    /**
     * doInBackground
     * @param message
     * @return null
     * run the tcpClient (start the connection)
     */
   protected TcpClient doInBackground(String ... message) {
        mTcpClient.run();
        return null;
    }

    /**
     * onProgressUpdate
     * @param values
     */
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.d("test", "response " + values[0]);
    }
}