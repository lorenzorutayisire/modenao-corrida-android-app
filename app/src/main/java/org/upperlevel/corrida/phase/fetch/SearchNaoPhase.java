package org.upperlevel.corrida.phase.fetch;

import android.util.Log;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.phase.Phase;
import org.upperlevel.corrida.phase.GameActivity;
import org.upperlevel.corrida.phase.game.GamePhase;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.Arrays;

import lombok.Getter;

/**
 * This phase searches the nao locally using udp broadcast.
 * When udp socket receives a specific response, connects to the sender of the packet (hopefully the nao) via tcp.
 */
public class SearchNaoPhase extends Thread implements Phase {
    private static final int DEFAULT_PORT = 25565;

    private static final byte[] MAGIC_BROADCAST_REQUEST = "corrida_discovery".getBytes(Charset.forName("utf-8"));
    private static final byte[] MAGIC_BROADCAST_RESPONSE = "corrida_response".getBytes(Charset.forName("utf-8"));

    @Getter
    private GameActivity activity;

    public SearchNaoPhase(GameActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onStart() {
        activity.setContentView(R.layout.activity_search_nao);
        start();
    }

    @Override
    public void onStop() {
        if (isAlive()) {
            interrupt();
        }
        Log.i("GameConnector", "Search nao stopped");
    }

    @Override
    public void run() {
        // ---------------------------------------------------------------- Connects via udp
        Log.i("GameConnector", "Creating udp broadcast");
        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create socket: ", e);
        }
        try {
            socket.setBroadcast(true);
        } catch (SocketException e) {
            throw new IllegalStateException("Cannot create a broadcast socket: ", e);
        }
        try {
            socket.setSoTimeout(200);
        } catch (SocketException e) {
            throw new IllegalStateException("Error while changing socket timeout", e);
        }

        Log.i("GameConnector", "Creating broadcast address");
        InetAddress broadcast;
        try {
            broadcast = InetAddress.getByName("255.255.255.255"); // creates broadcast address
        } catch (IOException e) {
            throw new IllegalStateException("Cannot send broadcast packet: " + e);
        }

        Log.i("GameConnector", "Sending broadcast address");
        DatagramPacket response;
        try {
            byte[] message;
            byte[] buffer = new byte[MAGIC_BROADCAST_RESPONSE.length];
            do {
                socket.send(new DatagramPacket(MAGIC_BROADCAST_REQUEST, MAGIC_BROADCAST_REQUEST.length, broadcast, DEFAULT_PORT)); // broadcasts udp packet to the lan
                response = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(response);
                    message = response.getData();
                } catch (SocketTimeoutException e) {
                    message = new byte[]{};
                }
            } while (!Arrays.equals(message, MAGIC_BROADCAST_RESPONSE));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot receive address packet: " + e);
        }

        Log.i("GameConnector", "Response received, closing udp socket");
        socket.close();

        // ---------------------------------------------------------------- Connects via tcp
        Log.i("GameConnector", "Opening tcp socket");
        InetAddress address = response.getAddress();
        Socket tcpSocket;
        try {
            tcpSocket = new Socket(address, DEFAULT_PORT);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot connect to remote: " + address + ":" + DEFAULT_PORT);
        }
        Log.i("GameConnector", "Connected");

        activity.runOnUiThread(() -> {
            Log.i("GameConnector", "Now the game can start");
            activity.getRoot().setPhase(new GamePhase(activity, tcpSocket));
        });
    }
}
