package thesis.KafkaWebServer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class TopicSaveMessages {
	Properties properties;
	KafkaProducer<String, String> producer;
	public TopicSaveMessages () {
		Properties properties = new Properties();
		properties.put("bootstrap.servers", "localhost:9092");
		properties.put("acks"             , "0");
		properties.put("retries"          , "1");
		//properties.put("batch.size"       , "20971520");
		//properties.put("linger.ms"        , "33");
		//properties.put("max.request.size" , "2097152");
		properties.put("compression.type" , "gzip");
		properties.put("key.serializer"   , "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("value.serializer" , "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("kafka.topic"      , "enterTopic");
		producer = new KafkaProducer<String, String>(properties);
	}
    
	public void SaveMessage (String msg) {
		
	}
}
