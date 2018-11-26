package DEV_CLIx86.Final;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientListTasks {

	String ipSpringServer;
	String idUser;
	ArrayList<String> getListTasks;
	public ClientListTasks(String ipSpringServer, String idUser) {
		this.ipSpringServer = ipSpringServer;
		this.idUser = idUser;
		this.getListTasks = new ArrayList<String>();
	}

	public ArrayList<String> getListTasks () {
		try {
			
			String url = "http://"+ipSpringServer+":8080/getListTasks?name="+this.idUser;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode+ "\n");
			BufferedReader in =new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			} in.close();
			//System.out.println(response);
			String resp = response.substring(1, (response.length()-1));
			this.getListTasks = new ArrayList<String>(Arrays.asList(resp.split(",")));
			
			
			}catch (Exception e) {
				System.err.println("NO INFO");
				
			}
		
		return this.getListTasks;
	}
}
