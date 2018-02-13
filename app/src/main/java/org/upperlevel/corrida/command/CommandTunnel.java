package org.upperlevel.corrida.command;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class CommandTunnel {
    public static final String TAG = "CommandTunnel";

    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;

    public CommandTunnel(Socket socket) {
        this.socket = socket;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot get socket out: " + e);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void send(Command command) {
        try {
            Log.i(TAG, "Sending command: " + command);
            out.write(command.format());
            out.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write on socket out: " + e);
        }
    }

    public Command read() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            Command command = Command.parse(reader.readLine());
            Log.i(TAG, "Received command: " + command);
            return command;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read socket in: " + e);
        }
    }
}
