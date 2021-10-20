import java.net.*;
import java.util.*;
import java.io.*;

/**
 * This Chat Server has 3 main functions:
 * -- 1) Keep listening to port 5000 for client connection request. Once get one, this Server will do 2 things: 
 * 	1, Create a new thread with a inner class to continue listen to client; 
 * 	2, Put client's connection(PrintWriter) into a ArrayList. Yes, this is a long-connection version only for small amount clients;
 * 
 * -- 2) Runnable thread for each client will be listening the input, and trigger the boardcast medthod(TellEveryone), passing the received text on.
 * 
 * -- 3) TellEveryone with the received text to every PrintWriter in the ArrayList.
 */

public class SimpleChatServer {
	ArrayList<PrintWriter> arlClients = new ArrayList<PrintWriter>();

	public static void main(String[] args) {
		new SimpleChatServer().go();
	}

	/**
	 * start()
	 * Main control of Server, fullfilling the function --1)
	 * -- 1) Keep listening to port 5000 for client connection request. Once get one, this Server will do 2 things: 
 	* 	1, Create a new thread with a inner class to continue listen to client; 
 	* 	2, Put client's connection(PrintWriter) into a ArrayList. Yes, this is a long-connection version only for small amount clients;
	 */
	private void go() {
		//fullfil the function --1)
		ServerSocket serverSocket;
		try{
			serverSocket = new ServerSocket(5000);
			System.out.println("ChatServer is listening at port:" + serverSocket);
			while(true){
				//keep listening till client request.
				Socket newClientSocket = serverSocket.accept();	
				System.out.println("New Connection request coming in.");
				
				//Creat a new threads to handle the new client.
				new Thread(new InnerClientHandle(newClientSocket)).start();

				//Creat a PrintWriter for this new client and put it into ArrayList of clients.
				PrintWriter pWriter = new PrintWriter(newClientSocket.getOutputStream());
				arlClients.add(pWriter);

				//close server
				//serverSocket.close();
			}
		}catch(Exception e){
			e.getStackTrace();
		}finally{
		}
		
	}

	/**
	 * InnerClientHandle
	 * -- 2) Runnable thread for each client will be listening the input, and trigger the boardcast medthod(TellEveryone), passing the received text on.
	 */
	public class InnerClientHandle implements Runnable {
		//fullfil the function --2)
		Socket clientSocket;
		BufferedReader bReader;

		//Constructor to build a long-connection with client
		public InnerClientHandle(Socket newSocket) {
			System.out.println("Building a New Handler.");
			try{
				clientSocket = newSocket;

				//Here is some trick to review.
				//BufferReader's constructor do not take InputStreamReader any more.
				//So I use Reader(abstract class) instead, see how is it going
				InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
				bReader = new BufferedReader(isr);
				System.out.println("Handler is ready on port:" + clientSocket);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		//Listen to the socket for client input, and trigger TellEveryone()
		public void run() {
			String clientTxt;
			System.out.println("Running...");
			try{
				while((clientTxt = bReader.readLine())!=null){
					
					System.out.println(clientSocket.getPort() +" sent a Msg:" + clientTxt);
					
					//Simply and risky just call boardcast in the handler. 
					//Better way is to handle the exception separately.
					TellEveryone(clientTxt);
					
				}
			}catch(Exception e){
				e.printStackTrace();
			}

			try {
				clientSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void TellEveryone(String txtToSend) {
		//fullfil the function --3)
		System.out.println("Telling Everyone of " + arlClients.size());
		try{
			//Loop the clientArrayList and send the msg
			for(int x=0; x<arlClients.size();x++){
				PrintWriter pWriter =(PrintWriter)arlClients.get(x);
				pWriter.println(txtToSend);
				pWriter.flush();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}



}
