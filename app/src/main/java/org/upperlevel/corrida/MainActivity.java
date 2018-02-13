package org.upperlevel.corrida;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((CorridaApplication) getApplication()).reset();

        findViewById(R.id.search_nao_button).setOnClickListener(view -> {
            Log.i(TAG, "Starting SearchNaoActivity");
            startActivity(new Intent(MainActivity.this, SearchNaoActivity.class));
        });
        findViewById(R.id.join_nao_button).setOnClickListener(view -> {
            Log.i(TAG, "Starting JoinNaoActivity");
            startActivity(new Intent(MainActivity.this, JoinNaoActivity.class));
        });
    }
}
