import javax.swing.*;

import java.io.IOException;
import java.net.*;
import java.awt.*;
import java.awt.event.*;


public class ServerButton {
	
	private JButton button;
	private int serverListeningPort;
	private InetAddress serverAddress;
	private Client client;

	
	public ServerButton (int ServerList, InetAddress serverAddr, Client c) {
		serverListeningPort= ServerList;
		serverAddress = serverAddr;
		client = c;
		
		button = new JButton(serverAddress.getHostAddress());
		client.getSecondPanel().add(button);
		client.getSecondPanel().revalidate();
		client.getSecondPanel().repaint();
		button.addActionListener(new ConnectEvent());
	}
	
	/*
	 * Klijent pravi ClientThread pre uspostavljanja veze sa serverom
	 */
	private class ConnectEvent implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			client.createThread(serverAddress);
			connectWithServer(serverListeningPort,serverAddress);
			client.getThread().connect();
		}
	}
	

	private void connectWithServer(int port, InetAddress address) {
		try {
			byte[] sendMessage = "connect".getBytes();
			byte[] recieveMessage = new byte[20];
			DatagramPacket sendPacket = new DatagramPacket(sendMessage,sendMessage.length,serverAddress,55000);
			client.getThread().getSocket().send(sendPacket);
			System.out.println("proso send client");
			DatagramPacket recievePacket = new DatagramPacket(recieveMessage,recieveMessage.length);
			client.getThread().getSocket().receive(recievePacket);
			System.out.println("proso recieve");
			String port1 = new String(recievePacket.getData()).trim();
			int serverPort = Integer.parseInt(port1);
			client.setServer(serverListeningPort,serverPort,serverAddress);
			client.changePanel(client.communicationPanel());
			client.connected();
		} catch (IOException e) {}
	}

}
