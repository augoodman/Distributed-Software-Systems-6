import javax.jms.*;
import javax.naming.*;
import org.apache.log4j.BasicConfigurator;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Publisher {
    private TopicSession pubSession;
    private TopicPublisher publisher;
    public TopicConnection connection;

    /* Establish JMS publisher */
    public Publisher(String topicName, String userName, String password) throws Exception {
	// Obtain a JNDI connection - see jndi.properties
	InitialContext jndi = new InitialContext();
	// Look up a JMS connection factory
	TopicConnectionFactory conFactory = (TopicConnectionFactory)jndi.lookup("topicConnectionFactry");
	// Create a JMS connection
	connection = conFactory.createTopicConnection(userName, password);
	// Create JMS session objects for publisher
	pubSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
	// Look up a JMS topic
	Topic chatTopic = (Topic) jndi.lookup(topicName);
	// Create a JMS publisher
	publisher = pubSession.createPublisher(chatTopic);
	publisher.setDeliveryMode(DeliveryMode.PERSISTENT);
    }

    boolean goPublish(String s) {
	boolean rval = true;
	try {
	    // Create and send message using topic publisher
	    TextMessage message = pubSession.createTextMessage();
	    message.setText(s);
	    publisher.publish(message);
	} catch (Throwable thw1) {
	    thw1.printStackTrace();
	    rval = false;
	}
	return rval;
    }
}
