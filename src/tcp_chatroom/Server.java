package tcp_chatroom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{
	
	//listening for incoming connexions
	private ServerSocket server;
	public static ArrayList<ClientHandler> clientHandlers;//list of connected clients
	private boolean done;
	private ExecutorService pool;//to avoid creating new thread in every new connexion
	public Server() {
		clientHandlers= new ArrayList<>();
		done=false;
	}
	
	
	@Override
	public void run() {
		try {
		ServerSocket server= new ServerSocket(1234);
		System.out.println("server is running");

		//avoid creating new thread for handler
		pool=Executors.newCachedThreadPool();
		//accept connexions
		while(!done) {
	    //wait for client 
		Socket client =server.accept();
		System.out.println("a new client has connected to the server :");
		//open connexion handler foreach client
		ClientHandler handler = new ClientHandler(client);
		//run
		pool.execute(handler);
		clientHandlers.add(handler);
		}
        }catch(IOException e) {
			
			shutdown();
			
		}
		
	}
	
	//broadcast
	public void BroadcastMessage(String messageToSend) {
		
		//send msg to each client connected
		for(ClientHandler ch :clientHandlers) {
			if(ch!=null) {
				
				ch.sendMessage(messageToSend);
			}
		}
		
			
			
		}
	//close server
	public void shutdown(){
		done=true;	
		pool.shutdown();

		try {
				if(!server.isClosed()) {
					server.close();
				}
				for(ClientHandler ch :clientHandlers) {
					if(ch!=null) {
						//from client handler
					ch.shutdown();
					}
				}
				
			}catch(IOException e){
				e.printStackTrace();
			    
			    }
			
			
		}
	



//Handle each client connexions
public class ClientHandler implements Runnable{
	private Socket client;
	private BufferedReader in;//get stream from socket: client
	private PrintWriter out;//write to client 
	private String clientUsername;
	
	public ClientHandler(Socket client) {
	this.client=client;	
	}
	


	@Override
	public void run() {
		try {
			//client

			out=new PrintWriter(client.getOutputStream(),true);//flush =true
		    in =new BufferedReader(new InputStreamReader(client.getInputStream()));
		    
		    clientUsername=in.readLine();//user input
		    System.out.println(clientUsername + " connected");
		    //broadcast
		    BroadcastMessage( clientUsername + " joined the chat");
		    //ask for new msg
		    String messagefromClient;
		    while((messagefromClient=in.readLine())!= null){
		    	
		    	
		    	if(messagefromClient.startsWith("/quit")){
		    		 BroadcastMessage( clientUsername + " left the chat");
		    		 shutdown();
		    		
		    	}else {
		    		 BroadcastMessage( clientUsername +":"+messagefromClient);
		    	}
		   
		    }
		}catch(IOException e){
			shutdown();
	}
	}
	

   //send msg to client
	public void sendMessage(String message) {
		System.out.println(message);
		
	}
	
	//close serversocket
			public void shutdown() {
				
				try {
					in.close();
					out.close();

					if(!client.isClosed()) {
						client.close();
					}
				}catch(IOException e) {
					
					e.printStackTrace();
				}
			}
	
			}
      public static void main(String [] args)  {
	  Server server= new Server();
	  server.run();
      }
}


