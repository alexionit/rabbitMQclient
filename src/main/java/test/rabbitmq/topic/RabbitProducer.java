package test.rabbitmq.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.typesafe.config.*;
import java.io.*;


public class RabbitProducer {

    public static String QUEUE_NAME = "topic-message";
    public static String ROUTING_PATTERN = "test.message.*";

    public static void main(String[] argv) throws Exception {

        Config config = ConfigFactory.load("conf/application.conf").getConfig("rabbitMq");
        String filename = config.getString("messsage_path");
        String hostname = config.getString("hostname");
        String ports = config.getString("port");
        String EXCHANGE_NAME = config.getString("exchange_name");
        String exchange_type = config.getString("routing_type");


        int port = Integer.parseInt(ports);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostname);
        factory.setPort(port);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, exchange_type);

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_PATTERN);

            String routingKey = getRouting(filename);
            String message = getMessage(filename);

            int random = (int)(Math.random() * 50 + 1);

            JSONObject obj = new JSONObject();
            obj.put("content", message);
            String jsonString = obj.toString();

            channel.basicPublish(EXCHANGE_NAME, routingKey, null, jsonString.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");

        }
    }

    private static String getRouting(String filename) throws Exception {

        JSONObject jsonObject = (JSONObject) readJsonSimpleDemo(filename);
        return jsonObject.get("routing_key").toString();
    }

    private static String getMessage(String filename) throws Exception {

        JSONObject jsonObject = (JSONObject) readJsonSimpleDemo(filename);
        return jsonObject.get("message_body").toString();
    }

    public static Object readJsonSimpleDemo(String filename) throws Exception {

        Object obj = new JSONParser().parse(new FileReader(filename));
        JSONObject jo = (JSONObject) obj;
        return jo;
    }
}
