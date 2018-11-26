package DEV_CLIx86.Final;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.text.StyleContext.SmallAttributeSet;

public class DownloadManager {
	int totalParts; 
	String fileName;
	String ipSpringServer;
	int id;
	
	public static void main(String[] args) {
		// maintain array of active threads
		ArrayList<Thread> threadList = new ArrayList<Thread>();
		
		boolean exit = false;
		String ipSpringServer = "";
		String idUser = "";
		Scanner keyboard = new Scanner(System.in);
	    
	    System.out.println("Ingrese ip del servidor");
		ipSpringServer = keyboard.nextLine();
		
		System.out.println("Ingrese su usuario ");
		idUser = keyboard.nextLine();
		
		String option;
		
		while (!(exit)) {
			System.out.println(" -------------------------- ");
			System.out.println(" Client Downloader Menu ");
			System.out.println("Choose : ");
			System.out.println("1 - List tasks: ");
			System.out.println("2 - Download task ");
			System.out.println("0 - Quit");
			System.out.println(" -------------------------- ");
			option = keyboard.nextLine();
			
			ClientDownloader cd;
			Thread cdThread;
			
			ClientListTasks clt;
			
			switch (option) {
			
			case "1":
				System.err.println("Searching data in REST WebServer");
				clt = new ClientListTasks (ipSpringServer, idUser);
				ArrayList<String> results = clt.getListTasks();
				int i = 0;
				for (String queue : results) {
					
					if (queue.startsWith("\""+idUser)) {
						System.err.println(i+":"+queue);
						i++;
					}
				}
				break;
			case "2":
				// download
				System.out.println("Please insert task name:");
				String task  = keyboard.nextLine();
				// Obtain QParts (2nd _ where i see david_19_xyz -> 19 are parts
				String[] parts = task.split(Pattern.quote("_"));
				
				cd = new ClientDownloader(ipSpringServer, task, Integer.valueOf(parts[1]));
				cdThread = new Thread(cd);
				cdThread.start();
				threadList.add(cdThread);
				
				break;
			case "0":
				exit=true;
				
				break;
			default:
				System.out.println(" Incorrect Option, please select another one");
				break;
			}
		}
		

		System.out.println(" Although you pressed exit...");
		System.out.println("We have to wait for the remain download process ");
		for (Thread t : threadList) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("All task have been finished!");
	}
}
