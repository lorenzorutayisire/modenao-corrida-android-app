package org.upperlevel.corrida.phase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.upperlevel.corrida.MainActivity;
import org.upperlevel.corrida.phase.fetch.JoinNaoPhase;
import org.upperlevel.corrida.phase.fetch.SearchNaoPhase;

import lombok.Getter;

public class GameActivity extends AppCompatActivity {
    @Getter
    private PhaseManager<Phase> root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("GameActivity", "Started");
        super.onCreate(savedInstanceState);
        Log.i("GameActivity", "Started super called");

        root = new PhaseManager<>();

        Intent intent = getIntent();
        boolean useUdp = intent.getBooleanExtra(MainActivity.EXTRA_MESSAGE, true);

        Log.i("GameConnector", "Should I search Nao via udp? " + useUdp);
        if (useUdp) {
            root.setPhase(new SearchNaoPhase(this));
        } else {
            root.setPhase(new JoinNaoPhase(this));
        }
    }

    @Override
    public void onBackPressed() {
        Log.i("GameActivity", "On back pressed");
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        Log.i("GameActivity", "On pause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("GameActivity", "On stop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i("GameActivity", "On destroy");
        super.onDestroy();

        Log.i("GameActivity", "Near to stop phase: " + root.getPhase().getClass().getSimpleName());
        root.setPhase(null);
    }
}
