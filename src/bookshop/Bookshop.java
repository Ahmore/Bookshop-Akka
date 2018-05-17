package bookshop;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import bookshop.actors.FindActor;
import bookshop.actors.OrderActor;
import bookshop.actors.ReadActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Bookshop {

    public static void main(String[] args) throws Exception {

        // Config
        File configFile = new File("bookshop.conf");
        Config config = ConfigFactory.parseFile(configFile);

        // Create actor system & actors
        final ActorSystem system = ActorSystem.create("bookshop_system", config);
        final ActorRef find = system.actorOf(Props.create(FindActor.class), "find");
        final ActorRef order = system.actorOf(Props.create(OrderActor.class), "order");
        final ActorRef read = system.actorOf(Props.create(ReadActor.class), "read");

        // Interaction
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if (line.equals("q")) {
                break;
            }
        }

        system.terminate();
    }
}