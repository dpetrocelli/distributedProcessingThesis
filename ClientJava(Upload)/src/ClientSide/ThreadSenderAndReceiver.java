package ClientSide;

import java.net.Socket;
import java.util.ArrayList;

import Objects.Task;

public class ThreadSenderAndReceiver implements Runnable{
	int quantityOfParts, quantityFinished;
	ArrayList<Task> tasks;
	Socket s;
	
	public ThreadSenderAndReceiver (int quantityOfParts, int quantityFinished, ArrayList<Task> tasks, Socket s){
		this.quantityOfParts = quantityOfParts;
		this.quantityFinished = quantityFinished;
		this.s = s;
		this.tasks = tasks;
	}
	@Override
	public void run() {
		Task temporal;
		while (this.quantityOfParts < this.quantityFinished){
			
			for (int i=0; i<tasks.size();i++){
				// este if no iria
				if (this.quantityOfParts < this.quantityFinished){
					temporal = tasks.get(i);
					if (!(temporal.state.equals("finished"))){
						// take object and pass to server
						new Thread (new ChildSenderAndReceiver(this.quantityFinished, this.s, temporal)).start();
					}
				}
				break;
				
			}
		}
		
	}
	
	

}
