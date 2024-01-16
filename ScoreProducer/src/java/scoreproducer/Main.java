package scoreproducer;

import java.util.Scanner;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class Main {
    @Resource(mappedName = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/SimpleJMSTopic")
    private static Topic topic;

    public static void main(String[] args) {
        Connection connection = null;
        Destination dest = null;
        Scanner in = new Scanner(System.in);
        
        String text;

        try {
            dest = topic;
            connection = connectionFactory.createConnection();

            Session session = connection.createSession(
                    false,
                    Session.AUTO_ACKNOWLEDGE);

            MessageProducer producer = session.createProducer(dest);
            TextMessage message = session.createTextMessage();
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            System.out.println("To end the program, enter \"q\" or \"Q\"");
            while (true) {
                System.out.print("Enter live score: ");
                text = in.nextLine();
                if (text.equals("q") || text.equals("Q")) {
                    break;
                }
                message.setText(text);
                producer.send(message);
            }
            producer.send(session.createMessage());
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }
}
