package org.upperlevel.corrida.phase.game;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;

import lombok.Getter;

public class AsyncCommandSend extends AsyncTask<Command, Void, IOException> {
    @Getter
    private final OutputStream out;

    public AsyncCommandSend(OutputStream out) {
        this.out = out;
    }

    @Override
    protected IOException doInBackground(Command... commands) {
        for (Command command : commands) {
            try {
                out.write(command.getPacket());
                out.flush();
            } catch (IOException e) {
                return e;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(IOException e) {
        if (e != null) {
            e.printStackTrace();
        }
    }
}
