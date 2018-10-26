package io.pivotal.pas.c2c.udptest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MulticastingClient {

    private static Logger LOG = LoggerFactory.getLogger(MulticastingClient.class);
    
    private DatagramSocket socket;
    private InetAddress group;
    private byte[] buf;

    @Value("${udp.client.count:1}")
    private int expectedServerCount;

    public MulticastingClient() throws Exception {
        this.socket = new DatagramSocket();
        this.group = InetAddress.getByName("230.0.0.0");
    }

    public int discoverServers(String msg) throws IOException {
        copyMessageOnBuffer(msg);
        multicastPacket();

        return receivePackets();
    }

    public String discoverServers(String msg, int expectedServerCount) throws IOException {
        LOG.info("Changing expected server count to {}", expectedServerCount);
        this.expectedServerCount = expectedServerCount;
        int responses = discoverServers(msg);
        return String.format("Send %s, expected %d responses and received %d ", msg, expectedServerCount, responses);
    }

    private void copyMessageOnBuffer(String msg) {
        buf = msg.getBytes();
    }

    private void multicastPacket() throws IOException {
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
        socket.send(packet);
        socket.setSoTimeout(10000);
    }

    private int receivePackets() throws IOException {
        int serversDiscovered = 0;
        while (serversDiscovered != expectedServerCount) {
            try {
                receivePacket();
            } catch (SocketTimeoutException e) {
                LOG.info("Timeout while waiting for expected answers - {}", e.getMessage());
                return serversDiscovered;
            }
            serversDiscovered++;
        }
        return serversDiscovered;
    }

    private void receivePacket() throws IOException {
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        LOG.info("Received package from {}:{}", packet.getAddress(), packet.getPort());
    }

    public void close() {
        socket.close();
    }
}