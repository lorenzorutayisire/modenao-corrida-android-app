package org.upperlevel.corrida.phase.fetch;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.phase.Phase;
import org.upperlevel.corrida.phase.GameActivity;
import org.upperlevel.corrida.phase.game.GamePhase;

import java.io.IOException;
import java.net.Socket;

import lombok.Getter;

/**
 * In this phase the user is asked to insert hostname and the port of the remote nao.
 * After inserting them, connects via tcp to the game server.
 */
public class JoinNaoPhase implements Phase {
    @Getter
    private GameActivity activity;

    @Getter
    private EditText hostnameTextField;

    @Getter
    private TextView hostnameErrorField;

    @Getter
    private EditText portTextField;

    @Getter
    private TextView portErrorField;

    @Getter
    private Thread establishConnectionThread;

    public JoinNaoPhase(GameActivity activity) {
        this.activity = activity;
    }

    private void initFields() {
        hostnameTextField = activity.findViewById(R.id.hostname_input);
        hostnameErrorField = activity.findViewById(R.id.hostname_error);
        portTextField = activity.findViewById(R.id.port_input);
        portErrorField = activity.findViewById(R.id.port_error);
    }

    @Override
    public void onStart() {
        activity.setContentView(R.layout.activity_join_nao);
        initFields();
        activity.findViewById(R.id.connect).setOnClickListener(new OnNaoJoin());
    }

    @Override
    public void onStop() {
        if (establishConnectionThread != null && establishConnectionThread.isAlive()) {
            establishConnectionThread.interrupt();
        }
        Log.i("GameConnector", "Join nao stopped");
    }

    public class OnNaoJoin implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // Hostname
            String hostname = hostnameTextField.getText().toString();
            hostnameErrorField.setText("");
            if (hostname.isEmpty()) {
                hostnameErrorField.setText("Indirizzo invalido");
                Log.w("JoinNao", "Found invalid hostname");
            }
            Log.i("JoinNao", "Hostname is correct");

            // Port
            int port;
            portErrorField.setText("");
            try {
                port = Integer.parseInt(portTextField.getText().toString());
            } catch (NumberFormatException e) {
                portErrorField.setText("Dovresti inserire un numero intero");
                Log.w("JoinNao", "Invalid port number format");
                return;
            }
            if (port < 0 || port >= 65535) {
                portErrorField.setText("Dovresti inserire un numero compreso tra 0 e 65535");
                Log.w("JoinNao", "Invalid port number");
                return;
            }
            Log.i("JoinNao", "Port number is correct");

            // Connection
            establishConnectionThread = new EstablishConnection(hostname, port);
            establishConnectionThread.start();
        }
    }

    public class EstablishConnection extends Thread {
        @Getter
        private String hostname;

        @Getter
        private int port;

        public EstablishConnection(String hostname, int port) {
            this.hostname = hostname;
            this.port = port;
        }

        @Override
        public void run() {
            Socket socket;
            Log.i("JoinNao", "Establishing connection");
            try {
                socket = new Socket(hostname, port);
            } catch (IOException e) {
                activity.runOnUiThread(() -> Toast.makeText(activity, "Impossibile connettersi", Toast.LENGTH_SHORT).show());
                return;
            }
            Log.i("JoinNao", "Connection established to endpoint");
            activity.runOnUiThread(() -> {
                activity.getRoot().setPhase(new GamePhase(activity, socket));
                Log.i("JoinNao", "Changing to next phase.");
            });
        }
    }
}
