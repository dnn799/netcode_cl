
import java.io.IOException;
import java.net.*;

public class Reciever extends Thread {
	
	private ClientThread cliThread;
	
	public Reciever(ClientThread t) {
		cliThread = t;
	}
	
	public void run()  {
		try {
			while(!interrupted()) {
					byte[] recieveMessage = new byte [500];
					DatagramPacket recievePacket = new DatagramPacket(recieveMessage,recieveMessage.length);
					cliThread.getSocket().receive(recievePacket);
					Data data = Data.read(recievePacket.getData());
					System.out.println(data);
					cliThread.getClient().getPrikazivac().setText(data.toString());
					for (int i=0; i<recieveMessage.length; i++) {
						recieveMessage[i]=0;
				}
			}
		} catch (IOException e) {}
	}
	
	public synchronized void zaustavi() {
		System.out.println("reciever - zaustavi");
		cliThread.getSocket().close();
		interrupt();
	}
}
