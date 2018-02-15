package org.upperlevel.corrida;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.upperlevel.corrida.phase.Corrida;

import lombok.Getter;

public class CorridaActivity extends AppCompatActivity {
    @Getter
    private Corrida corrida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        corrida = new Corrida(this);
        corrida.start();
    }
}
