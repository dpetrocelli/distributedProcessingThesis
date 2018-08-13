package ServerSide;

import java.net.Socket;
import java.util.ArrayList;

public class ThreadActivityList implements Runnable{
	
	ArrayList<Socket> clientSockets;
	
	public ThreadActivityList (ArrayList<Socket> clientSockets){
		this.clientSockets = clientSockets;
	}
	@Override
	public void run() {
		Socket object;
		while (true){
			try {
				Thread.sleep(1000);
				// if object is not connected, then remove of the available workers
				for (int i=0; i<this.clientSockets.size();i++){
					object = clientSockets.get(i);
					if (!object.isConnected()){
						clientSockets.remove(object);
						System.out.println("elements removed");
					}
				}
			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// TODO Auto-generated method stub
		
	}

}
