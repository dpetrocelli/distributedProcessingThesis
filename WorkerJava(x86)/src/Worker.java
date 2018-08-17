import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;



public class Worker {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
				/*
					CloseableHttpClient httpclient = HttpClients.createDefault();
					String url = "http://localhost:8080/getJob";
					HttpResponse response = httpclient.execute(new HttpGet(url));
					System.out.println(response.getStatusLine());
					String msg = (response.getEntity().getContent()).toString();
					System.out.println("MSG; "+msg);
					/*JsonUtility jsonUt = new JsonUtility();
					jsonUt.setType("Message");
					Message msgRearmed = (Message) jsonUt.fromJson(msg);
					System.err.println("RESP: "+msgRearmed.name);*/
			String url = "http://localhost:8080/getJob";
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
		   //print in String
		   System.out.println(response.toString());
		   JsonObject myresponse = new JsonObject();
		   System.out.println(myresponse.get(response.toString()));
		   
			
			}
	}
