package BoostrapRat;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map.Entry;

import com.rabbitmq.client.Channel;

public class ManageAckList implements Runnable{

	HashMap<Long, Long> ackService; 
	int timeCheckInterval; 
	int timeoutPackage;
	Channel enterChannel;
	
	public ManageAckList(Channel enterChannel, HashMap<Long, Long> ackService, int timeCheckInterval, int timeoutPackage) {
		this.ackService = ackService;
		this.timeCheckInterval = timeCheckInterval;
		this.timeoutPackage = timeoutPackage;
		this.enterChannel = enterChannel;
	}
	@Override
	public void run() {
		// ONCE ACTIVATED TEST IF Some package is alive after time expected
		long idForAck;
		long timestamp;
		long currentTimestamp;  
		while (true) {
			// STEP 1 - Check for each msg;
			synchronized (this.ackService) {
				currentTimestamp = (new Timestamp(System.currentTimeMillis())).getTime();
				for (Entry<Long, Long> AckMap : this.ackService.entrySet()) {
				   idForAck = AckMap.getKey();
				   timestamp = AckMap.getValue();
				   if ((currentTimestamp - timestamp)> this.timeoutPackage) {
					   // IF difference is huge, non ACK to re deliver
					   try {
						this.enterChannel.basicNack(idForAck, false, true);
						System.out.println(" DELETED ID: "+idForAck);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				   }
				}
			}
			
			try {
				Thread.sleep(this.timeCheckInterval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

}
