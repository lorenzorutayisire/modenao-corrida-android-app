package org.upperlevel.corrida.phase.fetch;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.phase.Phase;
import org.upperlevel.corrida.phase.game.GamePhase;

import java.io.IOException;
import java.net.Socket;

import lombok.Getter;

public class JoinNaoPhase implements Phase {
    private static final String TAG = JoinNaoPhase.class.getSimpleName();

    @Getter
    private FetchPhase parent;

    @Getter
    private Activity activity;

    @Getter
    private EditText hostnameTextField;

    @Getter
    private TextView hostnameErrorField;

    @Getter
    private EditText portTextField;

    @Getter
    private TextView portErrorField;

    public JoinNaoPhase(FetchPhase parent) {
        this.parent = parent;
        this.activity = parent.getActivity();
    }

    @Override
    public void onStart() {
        activity.setContentView(R.layout.activity_join_nao);

        hostnameTextField = activity.findViewById(R.id.hostname_input);
        hostnameErrorField = activity.findViewById(R.id.hostname_error);
        portTextField = activity.findViewById(R.id.port_input);
        portErrorField = activity.findViewById(R.id.port_error);

        activity.findViewById(R.id.connect)
                .setOnClickListener(new OnNaoJoin());
    }

    @Override
    public void onStop() {
    }

    public class OnNaoJoin implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // Hostname
            String hostname = hostnameTextField.getText().toString();
            hostnameErrorField.setText("");
            if (hostname.isEmpty()) {
                hostnameErrorField.setText("Indirizzo invalido");
                Log.w(TAG, "Found invalid hostname");
            }
            Log.i(TAG, "Hostname is correct");

            // Port
            int port;
            portErrorField.setText("");
            try {
                port = Integer.parseInt(portTextField.getText().toString());
            } catch (NumberFormatException e) {
                portErrorField.setText("Dovresti inserire un numero intero");
                Log.w(TAG, "Invalid port number format");
                return;
            }
            if (port < 0 || port >= 65535) {
                portErrorField.setText("Dovresti inserire un numero compreso tra 0 e 65535");
                Log.w(TAG, "Invalid port number");
                return;
            }
            Log.i(TAG, "Port number is correct");

            // Connection
            Socket socket;
            Log.i(TAG, "Establishing connection...");
            try {
                socket = new Socket(hostname, port);
            } catch (IOException e) {
                Toast.makeText(activity, "Impossibile connettersi", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }
            Log.i(TAG, "Connection established to endpoint");
            parent.getParent().setPhase(new GamePhase(parent.getParent(), socket));
        }
    }
}
