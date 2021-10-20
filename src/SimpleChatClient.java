import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class is the client side working together with SimpleChatServer.
 * It will do the following jobs:
 * 
 * --1) 
 * setup a simple GUI with only 3 widgets: Display TextArea, Input Area and a Send Button.
 * 
 * --2)
 * When Send button click, send the msg of Input to server. Inner class innerSendListener added to button.
 * 
 * --3) TODO next version
 * Next version will implement the Display Area, listening to the Server for any Updated msg.
 */
public class SimpleChatClient {
	Socket socketToServer;
	PrintWriter pWriter;
	JButton btnSend;
	JTextField txtField;
	JTextArea txtArea;

	public static void main(String[] args) {
		SimpleChatClient scc = new SimpleChatClient();
		scc.go();
	}

	/**
	 * Build up the GUI.
	 * Add Listenner to the Send button.
	 */
	private void go(){

		// ActionListner added to Send button
		btnSend = new JButton("Send");
		btnSend.addActionListener(new innerSendListener());

		//Setup GUI
		JPanel pnl = new JPanel();
		txtField = new JTextField(20);
		txtArea = new JTextArea();
		txtArea.setAutoscrolls(true);
		txtArea.setBackground(Color.GRAY);
		//txtArea.setBorder(border);
		txtArea.setEditable(false);
		pnl.add(txtField);
		pnl.add(btnSend);

		JFrame frmMain = new JFrame("Simple Chat Client");
		frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMain.getContentPane().add(BorderLayout.SOUTH, pnl);
		frmMain.getContentPane().add(BorderLayout.CENTER, txtArea);
		frmMain.setSize(600, 600);
		frmMain.setVisible(true);

		setupNetwork();

		listenToSocket();
	}

	private void setupNetwork(){
		//build up a long-connection to the server
		try{
			socketToServer = new Socket("127.0.0.1", 5000);
			pWriter = new PrintWriter(socketToServer.getOutputStream());
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	class innerSendListener implements ActionListener{
		//
		public void actionPerformed(ActionEvent e) {
			//send msg
			try{
				if(pWriter != null){
					pWriter.println(txtField.getText());
					pWriter.flush();
					System.out.println("Action:" + e.getActionCommand() + " Msg:" + txtField.getText() + " >>> " + socketToServer);
				}
				txtField.setText("");
				txtField.grabFocus();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}

	}

	public void listenToSocket() {
		try {
			String message;
			InputStreamReader isr = new InputStreamReader(socketToServer.getInputStream());
			BufferedReader bReader = new BufferedReader(isr);
			System.out.println("Client is listening now.");
			while((message = bReader.readLine())!=null){
				System.out.println("Client heard:" + message);
				//Update TextArea
				txtArea.append(message + "\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
