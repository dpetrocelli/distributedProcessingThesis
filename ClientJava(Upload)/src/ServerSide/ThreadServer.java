package ServerSide;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Objects.Task;


public class ThreadServer implements Runnable{
	Socket clientSocket;
	
	public ThreadServer (Socket clientSocket){
		this.clientSocket = clientSocket;
	}
	@Override
	public void run() {
		System.out.println(" Thread Server has Started");
		try {
			ObjectOutputStream out = new ObjectOutputStream (this.clientSocket.getOutputStream());
			ObjectInputStream  in = new ObjectInputStream (this.clientSocket.getInputStream());
			Task receivedTask = (Task) in.readObject();
			
			// Obtain data object
			String videoPath = (receivedTask.videoPart.getVideoPath().split("/"))[1];
			videoPath = "src/trans-"+videoPath;
			System.out.println("video path is : "+videoPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
