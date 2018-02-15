package org.upperlevel.corrida.phase.fetch;

import android.app.Activity;
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
    @Getter
    private FetchPhase parent;

    @Getter
    private Activity activity;

    @Getter
    private EditText hostnameText;

    @Getter
    private TextView hostnameError;

    @Getter
    private EditText portText;

    @Getter
    private TextView portError;

    public JoinNaoPhase(FetchPhase parent) {
        this.parent = parent;
        this.activity = parent.getActivity();
    }

    @Override
    public void onStart() {
        activity.setContentView(R.layout.activity_join_nao);

        hostnameText = activity.findViewById(R.id.hostname_input);
        hostnameError = activity.findViewById(R.id.hostname_error);
        portText = activity.findViewById(R.id.port_input);
        portError = activity.findViewById(R.id.port_error);

        activity
                .findViewById(R.id.connect)
                .setOnClickListener(new OnNaoJoin());
    }

    @Override
    public void onStop() {
    }

    public class OnNaoJoin implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // hostname
            String hostname = hostnameText.getText().toString();
            hostnameError.setText("");
            if (hostname.isEmpty()) {
                hostnameError.setText("Indirizzo invalido");
            }
            // port
            int port;
            portError.setText("");
            try {
                port = Integer.parseInt(portText.getText().toString());
            } catch (NumberFormatException e) {
                portError.setText("Dovresti inserire un numero intero");
                return;
            }
            if (port < 0 || port >= 65535) {
                portError.setText("Dovresti inserire un numero compreso tra 0 e 65535");
                return;
            }
            // try connect
            Socket socket;
            try {
                socket = new Socket(hostname, port);
            } catch (IOException e) {
                Toast.makeText(activity, "Impossibile connettersi", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }
            parent.getParent().setPhase(new GamePhase(parent.getParent(), socket));
        }
    }
}
