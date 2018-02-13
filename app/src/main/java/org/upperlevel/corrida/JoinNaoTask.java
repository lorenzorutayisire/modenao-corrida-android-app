package org.upperlevel.corrida;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.upperlevel.corrida.command.CommandTunnel;

import java.io.IOException;
import java.net.Socket;

public class JoinNaoTask extends Thread {
    public static final String TAG = "JoinNaoTask";

    private final CorridaApplication application;
    private final JoinNaoActivity activity;

    private String address;
    private int port;

    public JoinNaoTask(JoinNaoActivity activity, String address, int port) {
        this.application = (CorridaApplication) activity.getApplication();
        this.activity = activity;

        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        Socket socket;
        try {
            socket = new Socket(address, port);
        } catch (IOException e) {
            activity.runOnUiThread(() -> Toast.makeText(activity, "Indirizzo non valido", Toast.LENGTH_SHORT).show());
            return;
        }
        Log.i(TAG, "Tcp connection established");
        application.setTunnel(new CommandTunnel(socket));
        activity.runOnUiThread(() -> Toast.makeText(activity, "Connessione effettuata!", Toast.LENGTH_SHORT).show());

        Log.i(TAG, "Changing activity to InsertNameActivity");
        activity.startActivity(new Intent(activity, InsertNameActivity.class));
    }
}
