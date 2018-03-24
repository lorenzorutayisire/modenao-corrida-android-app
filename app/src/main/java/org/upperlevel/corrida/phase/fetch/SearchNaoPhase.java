package org.upperlevel.corrida.phase.fetch;

import android.app.Activity;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.phase.Phase;
import org.upperlevel.corrida.phase.game.Game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import lombok.Getter;

public class SearchNaoPhase extends Thread implements Phase {
    private static final int DEFAULT_PORT = 25565;

    private static final byte[] MAGIC_BROADCAST_REQUEST = "corrida_discovery".getBytes(Charset.forName("utf-8"));
    private static final byte[] MAGIC_BROADCAST_RESPONSE = "corrida_response".getBytes(Charset.forName("utf-8"));

    @Getter
    private FetchPhase parent;

    @Getter
    private Activity activity;

    public SearchNaoPhase(FetchPhase parent) {
        this.parent = parent;
        this.activity = parent.getActivity();
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
    }

    @Override
    public void run() {
        // creates broadcast udp socket
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
        // sends broadcast packet
        InetAddress broadcast;
        try {
            broadcast = InetAddress.getByName("255.255.255.255"); // creates broadcast address
        } catch (IOException e) {
            throw new IllegalStateException("Cannot send broadcast packet: " + e);
        }
        // listens for broadcast response
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
        // closes socket
        socket.close();
        // now we have the nao host
        InetAddress address = response.getAddress();
        Socket tcpSocket;
        try {
            tcpSocket = new Socket(address, DEFAULT_PORT);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot connect to remote: " + address + ":" + DEFAULT_PORT);
        }
        activity.runOnUiThread(() -> parent.setPhase(new Game(parent.getParent(), tcpSocket)));
    }
}
