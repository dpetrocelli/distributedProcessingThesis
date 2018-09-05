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
	int id;
	public ClientDownloader (String fileName, int totalParts) {
		this.fileName = fileName;
		this.totalParts = totalParts;
	}
	@Override
	public void run() {
		this.id = (int) Thread.currentThread().getId();
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// STEP 0 - Define variables
		//System.out.println("Client Downloader finished Jobs started");
		int i = 0;
		String ipSpringServer = "10.2.3.67";
		String outputBasePath = "C:\\DTP\\video\\returnedCompressed\\compressed_"+this.id+"_part_";
		
		// STEP 1 - Loop until parts = parameterParts 
		while (true) {
			try {
				
				// STEP 2 - URL
				
				String url = "http://"+ipSpringServer+":8080/getEndTasks?name="+this.fileName;
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				int responseCode = con.getResponseCode();
				//System.out.println("\nSending 'GET' request to URL : " + url);
				//System.out.println("Response Code : " + responseCode);
				BufferedReader in =new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				} in.close();
			   
				try{
					
					// STEP 3 - RearmMsg
					String msgNotEncoded = response.toString();
					JsonUtility jsonUt = new JsonUtility();
					jsonUt.setType("Message");
					
					Message msgRearmed = (Message) jsonUt.fromJson(msgNotEncoded);
					System.err.println("OBTAINED: "+msgRearmed.name);
					
					// STEP 4 - Save compressed video in file
					try (FileOutputStream fos = new FileOutputStream(outputBasePath+msgRearmed.getPart()+".mp4")) {
					   fos.write(msgRearmed.data);
					}
					i++;
					System.out.println("i="+i+" / TOTAL: "+this.totalParts);
					
					// STEP 5 - If parts completed, break 
					if (i==this.totalParts) break;
				}catch (Exception e) {
					//System.err.println("NO INFO");
					
				}
				
				
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// STEP 6 - once i've completed parts, i need to rearm the complete file compressed
		String concatenate = "ffmpeg.exe";
		for (int k=0; k<this.totalParts; k++) {
			concatenate+=" -i "+outputBasePath+k+".mp4";
		}
		concatenate+=" -filter_complex concat=n="+this.totalParts+":v=1:a=1 -y C:\\DTP\\video\\resultJoined\\"+this.fileName+"_CAJ.mp4";
		this.concatenateParts(concatenate);
		
		
	}
	
	private void concatenateParts (String command) {
			String baseFFMpeg = "C:\\DTP\\ffmpeg\\bin\\";
			String task = baseFFMpeg+command;
			System.err.println("TASK: "+task);
			Process powerShellProcess;
			try {
				powerShellProcess = Runtime.getRuntime().exec(task);
				BufferedReader stderr = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));
				String line2;
				while ((line2 = stderr.readLine()) != null) {
					//System.out.println(line2);
				}
				stderr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}

}

