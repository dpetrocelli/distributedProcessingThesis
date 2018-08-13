package ClientSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import Objects.Task;




public class Client implements Runnable{
	Socket s;
	ObjectOutputStream out;
	ObjectInputStream in;
	String ffmpegPath;
	// Quant compressed
	int quantityOfParts;
	int quantityFinished;
	//   VIDEOPART, STATE
	ArrayList<Task> tasks;
	
	
	public Client (String host, int port) throws UnknownHostException, IOException {
		// Connect to Server (Master)
		this.s = new Socket (host, port);
		this.quantityOfParts = 0;
		this.quantityFinished=0;
		System.out.println("Client Connected to Main Server");
		
		
		String osName = System.getProperty("os.arch").toLowerCase();
		
		if (osName.startsWith("lin")){
			ffmpegPath = "ffmpeg";
	  	}
		else{
			ffmpegPath = "E:/ffmpeg/bin/";
		}
	  	
	  	
	}
	@Override
	public void run() {
		// Open thread to be prepared as a server (TRANSCORDER )
		// IN THIS CASE, RECEIVES FILES AND RETURNS COMPRESSED
		
		// Remember that is almost 1 thread is opened the main program continues running
		TrascoderThread ct = new TrascoderThread(this.s, this.ffmpegPath);
		Thread threadCt = new Thread (ct);
		threadCt.start();
		
		// Menu for transrate
		try {
			menu();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void menu() throws IOException, InterruptedException {
		String option;
		while (true){
			System.out.println("1 -> transrate video");
			System.out.println("2 -> Exit and close");
			option = readFromKeyboard();
			if (option.equals("1")){
				System.out.println(" please input videoFilePath");
				String path = "src/video.mp4";
				
				// Create the spliiter and charge data
				Splitter splitter = new Splitter(path, this.ffmpegPath, this.tasks, this.quantityOfParts);
				Thread threadSplitter = new Thread(splitter);
				threadSplitter.start();
				// And the splitter create the sender :)
				while (this.quantityOfParts==0){
					Thread.sleep(1000);
				}
				ThreadSenderAndReceiver tsar = new ThreadSenderAndReceiver(this.quantityOfParts, this.quantityFinished, this.tasks, this.s);
				Thread threadTsar = new Thread(tsar);
				threadTsar.start();
				
				
			}else{
				if (option.equals("2")){
					
					System.out.println("Client Closed");
					this.s.close();
					break;
					
				
					
				}else{
					System.err.println("option not available, please re input");
				}
			}
		}
		System.out.println("Salió");
		
	}


	private String readFromKeyboard () throws IOException{
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader (isr);
		return br.readLine();
	}
		
}