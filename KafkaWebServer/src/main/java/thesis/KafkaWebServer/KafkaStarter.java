package thesis.KafkaWebServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class KafkaStarter implements Runnable{
	String commands;
	
	public KafkaStarter (String commands) {
		this.commands = commands;
	}
	@Override
	public void run() {
		
		
			
			try {
				String[] commandList = {"powershell.exe", this.commands};
				Process powerShellProcess = Runtime.getRuntime().exec(commandList);
				powerShellProcess.getOutputStream();
				System.out.println("Standard Output:");
				BufferedReader stdout = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));
				String line;
				String resultLine="";
				while ((line = stdout.readLine()) != null) {
					System.out.println(line);
					if (line.endsWith("0.0.0.0/0.0.0.0:2181 (org.apache.zookeeper.server.NIOServerCnxnFactory)")) break;
					if (line.endsWith("INFO [KafkaServer id=0] started (kafka.server.KafkaServer)")) break;
					resultLine+=line+"\n";
				}
				stdout.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}


