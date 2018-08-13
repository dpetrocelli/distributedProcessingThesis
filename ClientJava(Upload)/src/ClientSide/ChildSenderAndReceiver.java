package ClientSide;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Objects.Task;

public class ChildSenderAndReceiver implements Runnable{

	int quantityFinished;
	Socket s;
	Task temporal;
	
	public ChildSenderAndReceiver(int quantityFinished, Socket s, Task temporal) {
		this.quantityFinished = quantityFinished;
		this.s = s;
		this.temporal = temporal;
	}

	@Override
	public void run() {
		// dedicated channel to send and receive
		try {
			ObjectOutputStream out = new ObjectOutputStream (this.s.getOutputStream());
			ObjectInputStream in = new ObjectInputStream (this.s.getInputStream());
			
			out.writeObject(temporal);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	

}
