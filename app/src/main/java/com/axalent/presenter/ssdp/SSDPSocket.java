package com.axalent.presenter.ssdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;

public class SSDPSocket {
	
	SocketAddress mSSDPMulticastGroup;
	private MulticastSocket mSSDPSocket;
	private MulticastSocket mSSDPSocketAck;
	private InetAddress broadcastAddress;
	
	public SSDPSocket() throws IOException {
		mSSDPSocket = new MulticastSocket(58000); // Bind some random port for receiving datagram
		broadcastAddress = InetAddress.getByName(SSDPConstants.ADDRESS);
		mSSDPSocket.joinGroup(broadcastAddress);
	}

	public SSDPSocket(String ack) throws IOException {
		mSSDPSocketAck = new MulticastSocket(9960); // Bind some random port for receiving datagram
		InetAddress broadcastAddress = InetAddress.getByName(SSDPConstants.ADDRESS_ACK);
		mSSDPSocketAck.joinGroup(broadcastAddress);
	}
	
	/* Used to send SSDP packet */
	public void send(String data) throws IOException {
		DatagramPacket dp = new DatagramPacket(data.getBytes(), data.length(), broadcastAddress, SSDPConstants.PORT);
		mSSDPSocket.send(dp);
	}
	
	/* Used to receive SSDP packet */
	public DatagramPacket receive() throws IOException {
		byte[] buf = new byte[1024];
		DatagramPacket dp = new DatagramPacket(buf, buf.length);
		mSSDPSocket.receive(dp);
		return dp;
	}

	/* Used to receive SSDP packet */
	public DatagramPacket receiveAck() throws IOException {
		byte[] buf = new byte[1024];
		DatagramPacket dp = new DatagramPacket(buf, buf.length);
		mSSDPSocketAck.receive(dp);
		return dp;
	}

	public void close() {
		if (mSSDPSocket != null) {
			mSSDPSocket.close();
		}
	}
}
