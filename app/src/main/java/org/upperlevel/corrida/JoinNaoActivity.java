package org.upperlevel.corrida;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class JoinNaoActivity extends AppCompatActivity {
    public static final String TAG = "JoinNaoActivity";

    private JoinNaoTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_nao);

        ((CorridaApplication) getApplication()).reset();

        findViewById(R.id.connect).setOnClickListener(view -> {
            String address = ((EditText) findViewById(R.id.hostname_input)).getText().toString();
            TextView portError = findViewById(R.id.port_error);
            portError.setText("");
            int port;
            try {
                port = Integer.parseInt(((EditText) findViewById(R.id.port_input)).getText().toString());
            } catch (NumberFormatException e) {
                portError.setText(R.string.join_nao_invalid_port);
                return;
            }
            Log.i(TAG, "Tcp targeted connection started to " + address + " " + port);
            task = new JoinNaoTask(this, address, port);
            task.start();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (task != null && task.isAlive()) {
            task.interrupt(); // tries interrupt the task if was running
            Log.i(TAG, "Tcp connection task suddenly interrupted by activity");
        }
    }
}
