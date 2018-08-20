package newClient;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ClientDownloader implements Runnable{
	int totalParts; 
	String fileName;
	public ClientDownloader (String fileName, int totalParts) {
		this.fileName = fileName;
		this.totalParts = totalParts;
	}
	@Override
	public void run() {
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Client Downloader finished Jobs started");
		int i = 0;
		String ipSpringServer = "192.168.0.20";
		while (i<=this.totalParts) {
			try {
				
				String url = "http://"+ipSpringServer+":8080/getEndTasks?name="+this.fileName;
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				int responseCode = con.getResponseCode();
				System.out.println("\nSending 'GET' request to URL : " + url);
				System.out.println("Response Code : " + responseCode);
				BufferedReader in =new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				} in.close();
			   
				try{
					String msgNotEncoded = response.toString();
					JsonUtility jsonUt = new JsonUtility();
					jsonUt.setType("Message");
					Message msgRearmed = (Message) jsonUt.fromJson(msgNotEncoded);
					//System.err.println("RESP: "+msgRearmed.name);
					try (FileOutputStream fos = new FileOutputStream("c:\\mysalida"+msgRearmed.getPart()+".mp4")) {
					   fos.write(msgRearmed.data);
					}
					i+=1;
				}catch (Exception e) {
					System.err.println("NO INFO");
				}
				
				
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}

