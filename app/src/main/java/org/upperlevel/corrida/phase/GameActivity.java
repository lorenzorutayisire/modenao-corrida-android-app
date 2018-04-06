package org.upperlevel.corrida.phase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.upperlevel.corrida.MainActivity;
import org.upperlevel.corrida.phase.Phase;
import org.upperlevel.corrida.phase.PhaseManager;
import org.upperlevel.corrida.phase.fetch.JoinNaoPhase;
import org.upperlevel.corrida.phase.fetch.SearchNaoPhase;

import lombok.Getter;

public class GameActivity extends AppCompatActivity {
    @Getter
    private PhaseManager<Phase> root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    protected void onStop() {
        super.onStop();
        root.setPhase(null);
        Log.i("GameConnector", "Game activity stopped");
    }
}
