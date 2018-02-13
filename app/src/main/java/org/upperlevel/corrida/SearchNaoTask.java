package org.upperlevel.corrida;

import android.content.Intent;

import org.upperlevel.corrida.command.CommandTunnel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Searches NAO server on the local network.
 */
public class SearchNaoTask extends Thread {
    private static final int DEFAULT_PORT = 25565;

    private static final String MAGIC_BROADCAST_REQUEST = "corrida_discovery";
    private static final String MAGIC_BROADCAST_RESPONSE = "corrida_response";
    private static final String ENCODING = "utf-8";

    private CorridaApplication application;
    private SearchNaoActivity activity;

    public SearchNaoTask(SearchNaoActivity activity) {
        this.application = (CorridaApplication) activity.getApplication();
        this.activity = activity;
    }

    @Override
    public void run() {
        System.out.println("Instancing UDP socket...");
        // creates broadcast udp socket
        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create socket: " + e);
        }
        System.out.println("Creating broadcast address...");
        try {
            socket.setBroadcast(true);
        } catch (SocketException e) {
            throw new IllegalStateException("Cannot create a broadcast socket: " + e);
        }
        // sends broadcast packet
        System.out.println("Sending broadcast packet...");
        try {
            InetAddress broadcast = InetAddress.getByName("255.255.255.255"); // creates broadcast address
            byte[] request = MAGIC_BROADCAST_REQUEST.getBytes(ENCODING);
            socket.send(new DatagramPacket(request, request.length, broadcast, DEFAULT_PORT)); // broadcasts udp packet to the lan
        } catch (IOException e) {
            throw new IllegalStateException("Cannot send broadcast packet: " + e);
        }
        // listens for broadcast response
        System.out.println("Listening for response...");
        DatagramPacket response;
        try {
            String message;
            do {
                byte[] buffer = new byte[MAGIC_BROADCAST_RESPONSE.length()];
                response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);
                message = new String(response.getData(), ENCODING);
            } while (!message.equals(MAGIC_BROADCAST_RESPONSE));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot receive address packet: " + e);
        }
        // closes socket
        System.out.println("Closing UDP socket...");
        socket.close();
        // now we have the nao host
        InetAddress address = response.getAddress();

        Socket tcpSocket;
        try {
            tcpSocket = new Socket(address, DEFAULT_PORT);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot connect to remote: " + address + ":" + DEFAULT_PORT);
        }
        application.setTunnel(new CommandTunnel(tcpSocket));
        activity.startActivity(new Intent(activity, InsertNameActivity.class));
    }
}
