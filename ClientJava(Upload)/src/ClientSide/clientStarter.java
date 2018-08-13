package ClientSide;

import java.io.IOException;
import java.net.UnknownHostException;

public class clientStarter {

	public static void main(String[] args) throws UnknownHostException, IOException {
		
		Client cw = new Client("127.0.0.1", 9000);
		Thread threadCw = new Thread (cw);
		threadCw.start();
		/*
		Client cw2 = new Client();
		Thread threadCw2 = new Thread (cw2);
		threadCw2.start();
		
		Client cw3 = new Client();
		Thread threadCw3 = new Thread (cw3);
		threadCw3.start();
		*/
	}
	
	

}
