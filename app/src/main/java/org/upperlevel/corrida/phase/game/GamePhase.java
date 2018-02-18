package org.upperlevel.corrida.phase.game;

import android.app.Activity;

import org.upperlevel.corrida.command.Command;
import org.upperlevel.corrida.phase.Corrida;
import org.upperlevel.corrida.phase.Phase;
import org.upperlevel.corrida.phase.PhaseManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import lombok.Getter;

public class GamePhase extends PhaseManager implements Phase {
    @Getter
    private Corrida parent;

    @Getter
    private Activity activity;

    @Getter
    private Socket socket;

    public GamePhase(Corrida parent, Socket socket) {
        this.parent = parent;
        this.activity = parent.getActivity();

        this.socket = socket;
    }

    public void emit(Command... command) throws IOException {
        OutputStream out = socket.getOutputStream();
        new AsyncCommandSend(out).execute(command);
    }

    public String receive() throws IOException {
        InputStream in = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return reader.readLine();
    }

    @Override
    public void onStart() {
        setPhase(new InsertNamePhase(this));
    }

    @Override
    public void onStop() {
    }
}
