package org.upperlevel.corrida.phase.game;

import android.os.AsyncTask;

import org.upperlevel.corrida.command.Command;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AsyncCommandSend extends AsyncTask<Command, Void, IOException> {
    public static final Charset CHARSET = Charset.forName("UTF-8");
    @Getter
    private final OutputStream out;

    @Override
    protected IOException doInBackground(Command... commands) {
        for (Command command : commands) {
            try {
                out.write(command.encode().getBytes(CHARSET));
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
