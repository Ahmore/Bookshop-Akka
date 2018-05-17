package client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import client.actors.ClientActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Client {
    public static void main(String[] args) throws Exception {
        // Config
        File configFile = new File(args[0]);
        Config config = ConfigFactory.parseFile(configFile);
        
        // Create actor system & actors
        final ActorSystem system = ActorSystem.create("client_system", config);
        final ActorRef local = system.actorOf(Props.create(ClientActor.class), "client");

        // Interaction
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if (line.equals("q")) {
                break;
            }
            local.tell(line, null);
        }

        system.terminate();
    }
}