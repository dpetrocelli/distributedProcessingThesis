package thesis.KafkaWebServer;

import java.util.Collections;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class DistributedRestController {
	
	ConnectionFactory connectionFactory;
	Connection connection;
	Session session;
	Destination destination;
	MessageProducer producer;
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	// default broker URL is : tcp://localhost:61616"

	private static String enterQueue = "enterQueue";
	private static String intermediateQueue = "intermediateQueue";
	private static String endQueue = "endQueue";
	
	public DistributedRestController () {
		this.createActiveMQConnection();
	}
	
	@RequestMapping(value = "/getJob", method = RequestMethod.GET)
	  public void getJob () {
		// Obtain element from the list
		/*Properties props = new Properties();
	      props.put("bootstrap.servers", "localhost:9092");
	      props.put("group.id", "*");
	      props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	      props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	      
	      //create producer
	      Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);
	      consumer.subscribe(Collections.singletonList("enterTopic"));
	      while (true) {
	            ConsumerRecords<String, String> records = consumer.poll(1000);
	            for (ConsumerRecord<String, String> record : records) {
	                System.out.printf("%s [%d] offset=%d, key=%s, value=\"%s\"\n",
									  record.topic(), record.partition(),
									  record.offset(), record.key(), record.value());
				}
	        }*/
		//return "ok - Ready";
		 
	 }
  @RequestMapping(value = "/uploadChunk", method = RequestMethod.POST)
  public String persistPerson(@RequestBody String msg) {
	 
	  try {
		TextMessage message = this.session.createTextMessage(msg);
		producer.send(message);
		System.out.println("MSG has been sent" );
		
	} catch (JMSException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
	  	
		return "ok - Ready";
	  
  }
  
  private void createActiveMQConnection () {
	  try {
		  this.connectionFactory = new ActiveMQConnectionFactory(url);
		  this.connection = connectionFactory.createConnection();
		  this.connection.start();
		  this.session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
		  this.destination = session.createQueue(this.enterQueue);
		  this.producer = session.createProducer(this.destination);
      }
      catch (Exception e) {
          System.out.println("Caught: " + e);
          e.printStackTrace();
      }
  }
  
  
  
}
