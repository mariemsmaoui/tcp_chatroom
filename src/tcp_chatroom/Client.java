package tcp_chatroom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{

	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private String username;
	private boolean done;
	@Override
	public void run() {

		try {
			client = new Socket("127.0.0.1",1234);
			System.out.println("please enter your name : ");

			out=new PrintWriter(client.getOutputStream(),true);
		    in =new BufferedReader(new InputStreamReader(client.getInputStream()));
		    Inputhandler inHandler = new Inputhandler();
		    Thread t = new Thread(inHandler);
		    //run dosesn't open seperate handlers
		    t.start();
		    String inMessage;
		    while((inMessage=in.readLine())!= null){
				
		    	System.out.println(inMessage);
			   
			    }
		}catch(IOException e) {
			shutdown();

		}
		
	}
	public void shutdown() {
		done=true;
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

	//ask for new input
	
	class Inputhandler implements Runnable{

		@Override
		public void run() {
			try {
				BufferedReader inReader=new BufferedReader(new InputStreamReader(System.in));
				while(!done) {
					String message = inReader.readLine();
				    if(message.equals("/quit"))	
				    {out.println(message);
				    	inReader.close();
				    	shutdown();
				    }else {
				    	//broadcast
				    	out.println(message);
				    }
				} 
				
				
			}catch(IOException e) {
				shutdown();

			}
			
		}
		
	}
	public static void main(String [] args)  {
		
		
		Client client = new Client();
		client.run();
		
		
	}
}
