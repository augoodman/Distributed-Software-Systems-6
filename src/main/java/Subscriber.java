import javax.jms.*;
import javax.naming.InitialContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Subscriber implements javax.jms.MessageListener {
    private TopicSession pubSession;
    public final TopicConnection connection;
    public static Map<String, Integer> rankings = new HashMap<>();
    /* Establish JMS subscriber */
    public Subscriber(String topicName, String clientName, String username, String password)
	throws Exception {
	// Obtain a JNDI connection
	InitialContext jndi = new InitialContext();
	// Look up a JMS connection factory
	TopicConnectionFactory conFactory = (TopicConnectionFactory)jndi.lookup("topicConnectionFactry");
	// Create a JMS connection
	connection = conFactory.createTopicConnection();
	connection.setClientID(clientName);  // this is normally done by configuration not programmatically
	// Look up a JMS topic - see jndi.properties in the classes directory
	Topic chatTopic = (Topic) jndi.lookup(topicName);
	TopicSession subSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
	TopicSubscriber subscriber = subSession.createDurableSubscriber(chatTopic, "DemoSubscriberModel");

	subscriber.setMessageListener(this);  // so we will use onMessage
	
	// Start the JMS connection; allows messages to be delivered
	connection.start();
    }

   public void onMessage(Message message) {
        try {
            int count;
            if (message instanceof TextMessage) {
                TextMessage txtMessage = (TextMessage) message;
                String[] song = txtMessage.getText().split(":");
                if(!rankings.containsKey(song[1])){
                    rankings.put(song[1], 1);
                }
                else{
                    rankings.put(song[1], rankings.get(song[1]) + 1);
                }
                rankings = rankings.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue,
                                LinkedHashMap::new
                        ));
                System.out.println("Song Played: " + song[1]);
                System.out.println("User: " + song[0]);
                System.out.println("Rankings:");
                int i = 1;
                for(Map.Entry<String, Integer> e : rankings.entrySet()){
                    if(e.getValue() == 1)
                        System.out.println("  " + i + ". " + e.getKey() + ": " + e.getValue() + " play");
                    else
                        System.out.println("  " + i + ". " + e.getKey() + ": " + e.getValue() + " plays");
                    i++;
                }
                System.out.println("Enter 'exit' to close the program.");
            } else {
                System.out.println("Invalid message received.");
            }
        } catch (JMSException e1) {
	    e1.printStackTrace();
	}
   }
    
    public static void main(String[] args) {
	// uncomment this line for verbose logging to the screen
	// BasicConfigurator.configure();
    if (args.length != 4) {
        System.out.println("Expected arguments: <topic-name(String)> <client-name(String)> <username(String)> <password(String)>");
        System.exit(1);
      }
      String topicName = args[0];
      String clientName = args[1];
      String userName = args[2];
      String password = args[3];
	try {
	    Subscriber sub =
		new Subscriber("Chat1", clientName, userName, password);
	    BufferedReader commandLine = new BufferedReader(new InputStreamReader(System.in));
	    
	    // closes the connection and exit the system when 'exit' enters in
	    // the command line
        System.out.println("Enter 'exit' to close the program.");
	    while (true) {
		String s = commandLine.readLine();
        if (s.equalsIgnoreCase("exit")) {
		    sub.connection.close();
		    System.exit(0);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
