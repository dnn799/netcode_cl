import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;

import javax.swing.*;

public class Client extends JFrame {
	
	private boolean connected = false;
	
	// 2 panela, 1. za komunikacioni prozor, 2. za refresh prozor
	private JPanel communicationPanel;
	private JPanel refreshPanel;
	
	
	private ClientThread clientThread;
	private Label prikazivac;
	private TextField editor;
	
	// ovi paneli se nalaze unutar refresh prozora
	// firstPanel - sadrzi refreshButton
	// secondPanel - sadrzi sve servere koji se nalaze u mrezi
	private JPanel firstPanel;
	private JPanel secondPanel;
	
	// ne znam zasto :D
	private Client client;
	
	// disconnect - OBAVEZNO PRVO PRITISNUTI DISCONNECT PA POTOM UGASITI SERVER
	//				inace ce prijaviti SocketException i pucace program
	// refresh - salje broadcast na mrezu, nalazi servere
	private JButton disconnect;
	private JButton refresh;

	// komunikacija
	private DatagramSocket socketCommunication;
	private int serverPort;
	private InetAddress serverAddress;
	private int serverListeningPort;
	
	
	
	public Client() throws SocketException {
		super("ClientSide");
		client = this;
		setSize(500,500);
		refreshWindow();
		communicationWindow();
		socketCommunication = new DatagramSocket();
		setVisible(true);
		addWindowListener(new ProzorDogadjaji());
		refresh.addActionListener(new RefreshDogadjaji());
	}
	
	// gasi prozor, gasi sve niti u klijentu, zatvara socket-e
	private class ProzorDogadjaji extends WindowAdapter {
		public void windowClosing(WindowEvent d) {
			obustavi();
			dispose();
		}
	}
	
	// gasi sve niti, salje serveru disconnected poruku ako je povezan uopste na server
	// zato postoji flag CONNECTED, jer bi bacao exception ako nije povezan na server jer barata
	// sa null pokazivacem
	private void obustavi() {
		if (isConnected()) {
			clientThread.getSender().zaustavi();
			clientThread.getReciever().zaustavi();
			if (clientThread.isConnected()) sendMessageToServer();
			clientThread.zavrsi();
		}
	}
	
	// disconnect button koristi ovu klasu, gasi niti, i menja panel na refresh mode
	private class DisconnectEvent implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			obustavi();
			notConnected();
			changePanel(refreshPanel);
		}
	}
	
	
	public boolean isConnected() {
		return connected;
	}
	
	public void connected() {
		connected = true;
	}
	
	public void notConnected() {
		connected = false;
	}
	
	// refresh button - pravi novu nit koja 3 sekunde trazi servere
	// i zatim se gasi
	private class RefreshDogadjaji implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			secondPanel.removeAll();
			refresh.setEnabled(false);
			GetServers gServ = new GetServers(client);
			gServ.start();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			gServ.zaustavi();
			refresh.setEnabled(true);
			
		}
	}
	
	private class TastaturaDogadjaji extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode()==KeyEvent.VK_ENTER) {
				clientThread.getSender().obavesti(); // obavestava nit da je pritisnut enter
										 // i unblock-uje klijentsku nit
				
			}
		}
	}
	
	
	public JPanel communicationPanel() {
		return communicationPanel;
	}
	
	public DatagramSocket getClientSocket() {
		return socketCommunication;
	}
	
	public Label getPrikazivac() {
		return prikazivac;
	}
	
	public TextField getEditor() {
		return editor;
	}
	
	public JPanel getSecondPanel() {
		return secondPanel;
	}
	
	
	public DatagramSocket getClientThreadSocket() {
		return clientThread.getSocket();
	}
	
	// kreira ClientThread na pritisak ServerButton-a
	public void createThread(InetAddress servAddr) {
		try {
			clientThread = new ClientThread(this,servAddr);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		clientThread.start();
	}
	
	public ClientThread getThread() {
		return clientThread;
	}
	
	// deleteme poruka namenjena serveru
	public void sendMessageToServer() {
		System.out.println(serverAddress.toString());
		byte[] delete = "deleteme".getBytes();
		DatagramPacket deleteme = new DatagramPacket(delete,delete.length,serverAddress,serverListeningPort);
		try {
			socketCommunication.send(deleteme);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Parametri servera
	 * serverPort - komunkacija sa serverskom niti
	 * serverAddress
	 * serverListeningPort - komunikacija sa serverom (zbog gasenja niti)
	 */
	public void setServer(int listenPort, int port, InetAddress address) {
		serverPort = port;
		serverListeningPort = listenPort;
		serverAddress = address;
		clientThread.setServer(serverPort);
	}
	
	/*
	 * pravi panel za komunikaciju sa serverom
	 */
	private void communicationWindow() {
		communicationPanel = new JPanel();
		communicationPanel.setLayout(new GridLayout(3,1));
		
		JPanel jPan = new JPanel();
		jPan.setLayout(new GridLayout(2,1));
		jPan.add(new Label("Kreirana klijentska strana",Label.CENTER),"East");
		disconnect = new JButton("Disconnect");
		disconnect.addActionListener(new DisconnectEvent());
		JPanel j = new JPanel();
		j.setSize(new Dimension(100,30));
		j.add(disconnect,Component.CENTER_ALIGNMENT);
		jPan.add(j, "Center");
		communicationPanel.add(jPan,"North");
		
		prikazivac = new Label("",Label.CENTER);
		communicationPanel.add(prikazivac,"Center");
		editor = new TextField();
		editor.addKeyListener(new TastaturaDogadjaji());
		communicationPanel.add(editor,"South");
		communicationPanel.revalidate();
		communicationPanel.repaint();
		
	}
	
	/*
	 * pravi panel za trazenje servera
	 */
	private void refreshWindow() {
		refreshPanel = new JPanel();
		refreshPanel.setLayout(new BorderLayout(2,1));
		refreshPanel.add(firstPanel = new JPanel());
		refreshPanel.add(refresh = new JButton("Refresh"));
		refresh.setPreferredSize(new Dimension(100,50));
		firstPanel.setSize(new Dimension(100,50));
		firstPanel.add(refresh);
		refreshPanel.add(firstPanel,"North");
		secondPanel = new JPanel();
		refreshPanel.add(secondPanel,"Center");
		add(refreshPanel);
		
	}
	
	/*
	 * menja ova gore 2 panela
	 */
	public void changePanel(JPanel p) {
		getContentPane().removeAll();
		getContentPane().add(p);
		revalidate();
		repaint();
	}
	
	public static void main(String [] vargs) throws SocketException {
		new Client();
	}
}
