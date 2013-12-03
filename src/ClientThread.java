


import java.io.IOException;
import java.net.*;

public class ClientThread extends Thread {
	
	private Client client;
	private Sender sender;
	private Reciever reciever;
	
	private InetAddress serverAddress;
	private int serverPort;
	
	private boolean connected = false;
	
	/*
	 * ClientThread koristi poseban socket za komunikaciju
	 */
	private DatagramSocket socketCommunication;
	

	public ClientThread(Client c, InetAddress servAddr) throws SocketException {
		client = c;
		serverAddress = servAddr;
		socketCommunication = new DatagramSocket();
		sender = new Sender(this);
		reciever = new Reciever(this);
	}
	
	
	public void setServer(int port) {
		serverPort =port;
	}
	
	public InetAddress getServerAddress() {
		return serverAddress;
	}
	
	public int getServerPort() {
		return serverPort;
	}
	
	public Sender getSender() {
		return sender;
	}
	
	public Reciever getReciever() {
		return reciever;
	}
	
	public DatagramSocket getSocket() {
		return socketCommunication;
	}
	
	public Client getClient() {
		return client;
	}
	
	public void connect() {
		connected = true;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void run() {
		try {
			reciever.start();
			sender.start();
			
			while(!interrupted()) {}
			
		} catch (Exception e) {}
	}

	public synchronized void obavesti() {
		notify();
	}
	
	public void zavrsi() {
		socketCommunication.close();
		interrupt();
	}
	
	
	protected void finalize() throws Throwable { 
		super.finalize(); 
		socketCommunication.close();
	}

}
