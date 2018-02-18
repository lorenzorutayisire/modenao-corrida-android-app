package org.upperlevel.corrida.phase.fetch;

import android.app.Activity;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.phase.Phase;
import org.upperlevel.corrida.phase.game.GamePhase;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import lombok.Getter;

public class SearchNaoPhase extends Thread implements Phase {
    private static final int DEFAULT_PORT = 25565;

    private static final String MAGIC_BROADCAST_REQUEST = "corrida_discovery";
    private static final String MAGIC_BROADCAST_RESPONSE = "corrida_response";

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
            throw new IllegalStateException("Cannot create socket: " + e);
        }
        try {
            socket.setBroadcast(true);
        } catch (SocketException e) {
            throw new IllegalStateException("Cannot create a broadcast socket: " + e);
        }
        // sends broadcast packet
        try {
            InetAddress broadcast = InetAddress.getByName("255.255.255.255"); // creates broadcast address
            byte[] request = MAGIC_BROADCAST_REQUEST.getBytes("UTF-8");
            socket.send(new DatagramPacket(request, request.length, broadcast, DEFAULT_PORT)); // broadcasts udp packet to the lan
        } catch (IOException e) {
            throw new IllegalStateException("Cannot send broadcast packet: " + e);
        }
        // listens for broadcast response
        DatagramPacket response;
        try {
            String message;
            do {
                byte[] buffer = new byte[MAGIC_BROADCAST_RESPONSE.length()];
                response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);
                message = new String(response.getData(), "UTF-8");
            } while (!message.equals(MAGIC_BROADCAST_RESPONSE));
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
        activity.runOnUiThread(() -> parent.setPhase(new GamePhase(parent.getParent(), tcpSocket)));
    }
}
