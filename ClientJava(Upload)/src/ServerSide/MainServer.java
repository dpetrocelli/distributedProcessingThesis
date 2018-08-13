package ServerSide;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import Objects.Task;



public class MainServer implements Runnable{
	ArrayList<Socket> clientSockets;
	ArrayList<Task> queueTasks;
	int port;
	
	public MainServer (int i){
		this.clientSockets = new ArrayList<Socket>();
		this.queueTasks = new ArrayList<Task>();
		this.port = i;
	}
	@Override
	public void run() {
		
		try {
			ServerSocket ss = new ServerSocket(this.port);
			System.out.println("Socket Server has started on port: "+this.port);
			
			// continuous verifying if client socket is active
			ThreadActivityList tal = new ThreadActivityList (clientSockets);
			Thread threadTal = new Thread(tal);
			threadTal.start();
			
			
			
			// Accept new connections 
			while (true){
				Socket clientSocket = ss.accept();
				clientSockets.add(clientSocket);
				System.out.println("Socket client accepted and added");
				ThreadServer ts = new ThreadServer(clientSocket);
				Thread threadTs = new Thread (ts);
				threadTs.start();
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}

