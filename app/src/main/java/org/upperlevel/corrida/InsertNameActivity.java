package org.upperlevel.corrida;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.upperlevel.corrida.command.Command;
import org.upperlevel.corrida.command.CommandTunnel;
import org.upperlevel.corrida.game.StartGameActivity;

public class InsertNameActivity extends AppCompatActivity {
    public static final String TAG = "InsertNameActivity";

    private CorridaApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_name);

        application = (CorridaApplication) getApplication();
        Log.i(TAG, "Started InsertNameActivity");

        findViewById(R.id.name_submit).setOnClickListener(view -> {
            String name = ((TextView) findViewById(R.id.name_input)).getText().toString();
            CommandTunnel tunnel = application.getTunnel();
            if (tunnel != null) {
                tunnel.send(Command.parse("name \"" + name + "\""));
                Toast.makeText(this, "Nome inviato", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, StartGameActivity.class));
            } else {
                Toast.makeText(this, "Connessione persa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
