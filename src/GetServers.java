import java.awt.*;
import java.io.IOException;
import java.net.*;

import javax.swing.JPanel;

public class GetServers extends Thread {
	
	private DatagramSocket socketCommunication;
	private Client client;
	private boolean kraj = false;
	
	public GetServers(Client c) {
		try {
			socketCommunication = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		client = c;
	}
	
	
	public void run() {
		try {
			while(!interrupted()) {
				byte[] sendMessage = "broadcast".getBytes();
				byte[] recieveMessage = new byte[20];
				InetAddress broadcast;
	
				broadcast = InetAddress.getByName("255.255.255.255");
				DatagramPacket sendPacket = new DatagramPacket(sendMessage,sendMessage.length,broadcast,55000);
				socketCommunication.send(sendPacket);
				System.out.println("proso send client");
				while(!kraj) {
					DatagramPacket recievePacket = new DatagramPacket(recieveMessage,recieveMessage.length);
					socketCommunication.receive(recievePacket);
					int serverListeningPort = recievePacket.getPort();
					System.out.println("proso recieve");
					InetAddress serverAddress = recievePacket.getAddress();
					String message = new String(recievePacket.getData()).trim();
					if (message.equals("i_am_server")) {
						new ServerButton(serverListeningPort,serverAddress,client);
					}
					sleep(1);
				}
			}
		} catch (InterruptedException | IOException e){ 
			System.out.println("usao u catch");
		}
		
	}
	
	public void zaustavi() {
		System.out.println("zaustavi");
		kraj = true;
		socketCommunication.close();
		interrupt();
	}
}
