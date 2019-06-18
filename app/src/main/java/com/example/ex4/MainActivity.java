package com.example.ex4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {
    @Override
    /**
     * Name: onCreate
     * @param Bundle savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Name: onClick
     * @param v
     * Output: -
     * Operation: when the connect botton is on, goto joystick activation and connect
     */
    public void onClick(View v) {
        // get the values from the plain text
        EditText port = (EditText)findViewById(R.id.portText);
        EditText ip = (EditText)findViewById(R.id.ipText);
        int portValue = Integer.parseInt(port.getText().toString());
        String ipValue = ip.getText().toString();
        // create the tcp client and the connect task
        TcpClient mTcpClient = TcpClient.getInstance();
        mTcpClient.setServerIP(ipValue);
        mTcpClient.setServerPort(portValue);
        ConnectTask myTask = new ConnectTask(mTcpClient);
        // execute the task, goto run and start the connection
        myTask.execute();
        // goto joystick activity view
        startActivity(new Intent(MainActivity.this, JoystickActivity.class));
    }
}
