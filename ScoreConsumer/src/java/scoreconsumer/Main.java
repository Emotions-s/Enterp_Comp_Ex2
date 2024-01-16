package scoreconsumer;

import java.util.Scanner;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
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
        Session session = null;
        Destination dest = null;
        MessageConsumer consumer = null;
        TextMessage message = null;

        String text;

        dest = topic;

        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(dest);
            connection.start();

            Thread inputThread = new Thread(() -> waitForInputAndExit());
            inputThread.start();
            while (true) {
                Message m = consumer.receive();
                if (m != null) {
                    if (m instanceof TextMessage) {
                        message = (TextMessage) m;
                        System.out.println("Updated!: " + message.getText());
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
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
        System.exit(0);
    }

    private static void waitForInputAndExit() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("To end the program, enter \"q\" or \"Q\"");

        while (true) {
            String input = scanner.nextLine();
            if (input.equals("Q") || input.equals("q")) {
                break;
            }
        }

        System.exit(0);
    }
}
