package org.upperlevel.corrida;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.upperlevel.corrida.phase.GameActivity;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "org.upperlevel.corrida.USE_UDP";

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void onConnectClick(boolean useUdp) {
        if (!isOnline()) {
            Toast.makeText(this, "Non sei connesso ad internet", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(EXTRA_MESSAGE, useUdp);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.search_nao_button).setOnClickListener(view -> {
            Log.i("Main", "Connection via udp");
            onConnectClick(true);
        });
        findViewById(R.id.join_nao_button).setOnClickListener(view -> {
            Log.i("Main", "Connection via tcp");
            onConnectClick(false);
        });
    }
}
