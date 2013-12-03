import java.io.IOException;
import java.net.*;


public class Sender extends Thread {
	private ClientThread cliThread;
	
	public Sender(ClientThread t) {
		cliThread = t;
	}
	public void run() {
		try {
			while (!interrupted()){
					synchronized (this) {
						System.out.println("usao u block");
						wait();
					}
					System.out.println("prosao block");
					if (cliThread.getKraj()) break;
					byte[] sendMessage = cliThread.getClient().getEditor().getText().getBytes();
					cliThread.getClient().getEditor().setText("");
					
					DatagramPacket sendPacket = new DatagramPacket(sendMessage,sendMessage.length,cliThread.getServerAddress(),cliThread.getServerPort());
					
					cliThread.getSocket().send(sendPacket);
					
					for (int i=0; i<sendMessage.length;i++) {
						sendMessage[i]=0;
				}
			}
		} catch (Exception e) {}
	}
	
	public synchronized void obavesti() {
		notify();
	}
	public synchronized void zaustavi() {
		interrupt();
	}

}
