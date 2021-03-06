package br.unioeste.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import br.unioeste.client.SocketClientsManage;
import br.unioeste.client.SocketFileManage;
import br.unioeste.client.SocketMessageManager;
import br.unioeste.common.User;
import br.unioeste.messenger.ClientListener;
import br.unioeste.messenger.ClientsList;
import br.unioeste.messenger.FileListener;
import br.unioeste.messenger.ManageClients;
import br.unioeste.messenger.ManageFile;
import br.unioeste.messenger.ManageMessages;
import br.unioeste.messenger.MessagesListener;
import br.unioeste.util.Archive;

import javax.swing.border.EtchedBorder;
import javax.swing.JMenuBar;

import static br.unioeste.global.SocketConstants.*;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JButton;

public class MessengerClientsGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	private DefaultListModel<String> model;
	private DefaultListModel<String> modelFiles;

	private ManageMessages messageManager; // communicates with message server
	private ManageClients clientsManager; // communication with clients server
	private ManageFile fileManage;

	private ClientListener clientsListener; //
	private FileListener fileListener;
	private MessagesListener messageListener;

	private User user;

	private JMenuItem connectMenuItem;
	private JMenuItem disconetMenuItem;
	private JMenuItem refreshMenuItem;
	private JMenuItem ChatMenuItem;

	private JTextArea textAreaChat;
	private JTextArea textAreaStatusTransfer;

	private JLabel statusBar_Conection;

	private JRadioButton rdbtnChat;
	private JRadioButton rdbtnApenasUsuario;
	private JButton btnSendNewFile;

	private JFileChooser fileChooser = new JFileChooser();
	
	private JTabbedPane tabbedPane;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					ManageMessages messageManager; // declare MessageManager
					messageManager = new SocketMessageManager( SERVER_ADDRESS );

					ManageClients clientsManager;
					clientsManager = new SocketClientsManage();

					ManageFile manageFile = new SocketFileManage();

					MessengerClientsGUI frame = new MessengerClientsGUI(messageManager , clientsManager , manageFile);
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MessengerClientsGUI( ManageMessages managemessages , ManageClients clientsmanager , final ManageFile manageFile) {

		try {	/**Pegar variaveis de ambiente*/
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
		} catch (Exception e) {
			System.out.println("Erro ao obter variaveis de ambiente");
		}


		messageManager = managemessages;
		clientsManager = clientsmanager;
		fileManage = manageFile;

		clientsListener = new MyClientListener();
		messageListener = new MyMessageListener();
		fileListener = new MyFileListener();

		setTitle("Clients List");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 743);

		JMenu serverMenu = new JMenu ( "Server" );


		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add( serverMenu ); // add server menu to menu bar

		setJMenuBar( menuBar ); // add JMenuBar to application

		ConnectListener connectionListener = new ConnectListener();

		connectMenuItem = new JMenuItem( "Connect");
		connectMenuItem.addActionListener(connectionListener);

		DisconnectListener diconnectListener = new DisconnectListener();

		disconetMenuItem = new JMenuItem("Disconect");
		disconetMenuItem.addActionListener(diconnectListener);
		disconetMenuItem.setEnabled(false);

		refreshMenuItem = new JMenuItem("Refresh");
		refreshMenuItem.setEnabled(false);

		refreshMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientsManager.getClientsList(clientsListener);
			}
		});


		serverMenu.add(connectMenuItem);
		serverMenu.add(disconetMenuItem);
		serverMenu.add(refreshMenuItem);



		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(12, 12, 566, 593);
		contentPane.add(panel);

		panel.setLayout(null);

		model = new DefaultListModel<String>();

		JList list = new JList(model);
		list.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		list.setBounds(24, 12, 518, 569);
		panel.add(list);

		list.setSelectionBackground(Color.ORANGE);

		JScrollPane scrollPane_1 = new JScrollPane(list);
		scrollPane_1.setBounds(24, 12, 518, 569);
		panel.add(scrollPane_1);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(12, 607, 566, 81);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		JLabel statusBar = new JLabel("Status: ");
		statusBar.setBounds(12, 54, 66, 15);
		panel_1.add(statusBar);

		statusBar_Conection = new JLabel("Not connected");
		statusBar_Conection.setBounds(90, 54, 335, 15);
		panel_1.add(statusBar_Conection);

		rdbtnChat = new JRadioButton("Chat");
		rdbtnChat.setBounds(394, 24, 149, 23);
		panel_1.add(rdbtnChat);

		rdbtnApenasUsuario = new JRadioButton("Apenas usuario");
		rdbtnApenasUsuario.setBounds(394, 50, 149, 23);
		panel_1.add(rdbtnApenasUsuario);

		ButtonGroup grupo = new ButtonGroup();
		grupo.add(rdbtnChat);
		grupo.add(rdbtnApenasUsuario);
		rdbtnApenasUsuario.setSelected(true);

		JLabel lblOpes = new JLabel("Opções");
		lblOpes.setBounds(394, 0, 149, 16);
		panel_1.add(lblOpes);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(590, 12, 394, 670);
		contentPane.add(tabbedPane);

		JPanel pannelChat = new JPanel();
		tabbedPane.addTab("Chat", null, pannelChat, null);
		pannelChat.setLayout(null);

		pannelChat.setLayout(null);

		textAreaChat = new JTextArea();
		textAreaChat.setBounds(1, 1, 367, 630);
		pannelChat.add(textAreaChat);
		textAreaChat.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(textAreaChat);
		scrollPane.setBounds(12, 12, 370, 619);
		pannelChat.add(scrollPane);



		JPanel pannelFileTransfer = new JPanel();
		tabbedPane.addTab("Files Transfer", null, pannelFileTransfer, null);
		pannelFileTransfer.setLayout(null);

		modelFiles = new DefaultListModel<String>();

		JList listFileTransfer = new JList( modelFiles );
		listFileTransfer.addMouseListener(new DownloadArchive());

		listFileTransfer.setBounds(12, 46, 365, 533);
		pannelFileTransfer.add(listFileTransfer);

		listFileTransfer.setSelectionBackground(Color.ORANGE);

		JLabel lblAvaibleFiles = new JLabel("Avaible Files");
		lblAvaibleFiles.setBounds(12, 19, 129, 15);
		pannelFileTransfer.add(lblAvaibleFiles);

		btnSendNewFile = new JButton("Send File");
		btnSendNewFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int res = fileChooser.showOpenDialog(null);
				if (res == JFileChooser.APPROVE_OPTION) {
					File dir = fileChooser.getSelectedFile();
					String currentFile = dir.getPath();

					manageFile.sendFile( fileListener , currentFile);

				}

			}
		});
		btnSendNewFile.setBounds(12, 591, 129, 25);
		pannelFileTransfer.add(btnSendNewFile);

		JScrollPane scrollPane_2 =  new JScrollPane( listFileTransfer);
		scrollPane_2.setBounds(12, 46, 365, 533);
		pannelFileTransfer.add(scrollPane_2);

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try{
					fileManage.listAvaibleFiles(fileListener);
				}catch (Exception e3e) {
					// TODO: handle exception
				}

			}
		});
		btnRefresh.setBounds(286, 591, 91, 25);
		pannelFileTransfer.add(btnRefresh);

		JPanel panelStatusTransfer = new JPanel();
		tabbedPane.addTab("Transfer Status", null, panelStatusTransfer, null);
		panelStatusTransfer.setLayout(null);

		textAreaStatusTransfer = new JTextArea();
		textAreaStatusTransfer.setBounds(12, 12, 365, 619);
		panelStatusTransfer.add(textAreaStatusTransfer);
		textAreaStatusTransfer.setEditable(false);

		JScrollPane scrollPane_3 = new JScrollPane( textAreaStatusTransfer );
		scrollPane_3.setBounds(12, 12, 365, 619);
		panelStatusTransfer.add(scrollPane_3);



		ListDataListener listDataListener = new ListDataListener() {
			public void contentsChanged(ListDataEvent listDataEvent) {
				appendEvent(listDataEvent);
			}

			public void intervalAdded(ListDataEvent listDataEvent) {
				appendEvent(listDataEvent);
			}

			public void intervalRemoved(ListDataEvent listDataEvent) {
				appendEvent(listDataEvent);
			}

			private void appendEvent(ListDataEvent listDataEvent) {
				switch (listDataEvent.getType()) {
				case ListDataEvent.CONTENTS_CHANGED:
					System.out.println("Type: Contents Changed");
					break;
				case ListDataEvent.INTERVAL_ADDED:
					System.out.println("Novo cliente conectado");
					break;
				case ListDataEvent.INTERVAL_REMOVED:
					System.out.println("Type: Interval Removed");
					break;
				}
				DefaultListModel theModel = (DefaultListModel) listDataEvent.getSource();
				System.out.println(theModel);
			}
		};

		model.addListDataListener(listDataListener);

		newChat novaConversa = new newChat();

		list.addMouseListener(novaConversa);

	}

	private class MyClientListener implements ClientListener{

		public void clientreceived(User client) {
			// TODO Auto-generated method stub

		}

		public void clientListReceiver(ClientsList clientList) {
			SwingUtilities.invokeLater(new ClientsListUpdate(clientList.getClients()));

		}

	}

	private class ClientsListUpdate implements Runnable{

		private ArrayList<User> usersConnecteds;

		public ClientsListUpdate(ArrayList<User> users){
			usersConnecteds = users;
		}

		@Override
		public void run() {

			try{

				model.clear();
				if(!usersConnecteds.isEmpty()){


					for(User ur : usersConnecteds){
						model.addElement(ur.getUserName());
					}
				}else{
					model.addElement("Ninguem Conectado");
				}

			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}


		}

	}

	private class MyFileListener implements FileListener{

		@Override
		public void fileReceived(Archive archive) {
			// TODO Auto-generated method stub

		}

		@Override
		public void fileListAvaibleReceived(List<Archive> archives) {

			try{
				if(!archives.isEmpty()){
					modelFiles.clear();
					for(Archive arq : archives){
						modelFiles.addElement(arq.getName());
					}
				}else {
					modelFiles.addElement(NO_FILES_FOUND);
				}
			}catch (Exception e) {
				// TODO: handle exception
			}

		}

		@Override
		public void fileTransferStatus(String status) {
			textAreaStatusTransfer.append("\n" + status);

		}

	}

	private class MyMessageListener implements MessagesListener{

		public void messageReceived( String from,String to, String message ) 
		{

			String nMessage = "\n" + from + " To " + to + " says: " + message;
			textAreaChat.append(nMessage);
		}
	} 

	private class ConnectListener implements ActionListener 
	{
		// connect to server and enable/disable GUI components
		public void actionPerformed( ActionEvent event )
		{

			try{
				// prompt for userName
				String userName = JOptionPane.showInputDialog( 
						MessengerClientsGUI.this, "Enter user name:" );

				user = new User();
				user.setUserName(userName);;
				user.setUserTag(userName);

				// connect to server and route messages to messageListener
				messageManager.connect(messageListener, user);
			
				clientsManager.addClient(user);
				clientsManager.getClientsList(clientsListener);
				
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			connectMenuItem.setEnabled(false);
			disconetMenuItem.setEnabled(true);
			refreshMenuItem.setEnabled(true);
			ChatMenuItem.setEnabled(true);

			statusBar_Conection.setText(" Connected with " + user.getUserName());
			
			tabbedPane.setEnabled(true);

		} // end method actionPerformed      
	}

	private class DisconnectListener implements ActionListener 
	{
		// disconnect from server and enable/disable GUI components
		public void actionPerformed( ActionEvent event )
		{
			// disconnect from server and stop routing messages
			messageManager.disconnect( messageListener );

			connectMenuItem.setEnabled( true );
			disconetMenuItem.setEnabled(false);
			refreshMenuItem.setEnabled(false);
			ChatMenuItem.setEnabled(false);
			tabbedPane.setEnabled(false);
			modelFiles.clear();
			textAreaChat.setText("Desconected");
			textAreaStatusTransfer.setText("");

			model.clear();
		}   
	}



	private class newChat implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent mouseEvent) {
			JList theList = (JList) mouseEvent.getSource();
			if (mouseEvent.getClickCount() == 2) {
				int index = theList.locationToIndex(mouseEvent.getPoint());
				if (index >= 0) {
					Object o = theList.getModel().getElementAt(index);
					try{

						String message = JOptionPane.showInputDialog( 
								MessengerClientsGUI.this, "Enter message:" );			

						if(message != null){
							if(rdbtnApenasUsuario.isSelected()){
								messageManager.sendMessage(user.getUserName(), o.toString(), message);
							}else{
								messageManager.sendMessage(user.getUserName(), "all", message);
							}
						}

						String nMessage = "\nTo " + o.toString() +": " + message;
						textAreaChat.append(nMessage);

					}catch (Exception e) {
						// TODO: handle exception
					}
				}

			}
		}


		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	private class DownloadArchive implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent mouseEvent) {
			JList theList = (JList) mouseEvent.getSource();
			if (mouseEvent.getClickCount() == 2) {
				int index = theList.locationToIndex(mouseEvent.getPoint());
				if (index >= 0) {
					Object o = theList.getModel().getElementAt(index);
					try{

						int op = JOptionPane.showConfirmDialog(MessengerClientsGUI.this, " Download File ?");
						fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						if(op == JOptionPane.OK_OPTION){
							if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
								fileManage.receivedFile(fileListener , o.toString() , fileChooser.getSelectedFile().getPath());
							}
						}


					}catch (Exception e) {
						// TODO: handle exception
					}
				}

			}
		}


		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}
}



