package client.actors;

import akka.actor.AbstractActor;
import akka.actor.AllForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;
import shared.Request;
import shared.RequestType;
import shared.Response;

import java.util.Arrays;

import static akka.actor.SupervisorStrategy.restart;

public class ClientActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    if (s.startsWith("find")) {
                        Request request = new Request(RequestType.FIND, getTitle(s));

                        getContext().actorSelection("akka.tcp://bookshop_system@127.0.0.1:3552/user/bookshop").tell(request, getSelf());
                    }
                    else if (s.startsWith("order")) {
                        Request request = new Request(RequestType.ORDER, getTitle(s));

                        getContext().actorSelection("akka.tcp://bookshop_system@127.0.0.1:3552/user/bookshop").tell(request, getSelf());
                    }
                    else if (s.startsWith("read")) {
                        Request request = new Request(RequestType.READ, getTitle(s));

                        getContext().actorSelection("akka.tcp://bookshop_system@127.0.0.1:3552/user/bookshop").tell(request, getSelf());
                    }
                    else {
                        System.out.println("Unknown command");
                    }
                })
                .match(Response.class, response -> {
                    if (response.getType() == RequestType.FIND) {
                        if (response.getResult().equals("")) {
                            System.out.println("Book not found");
                        }
                        else {
                            System.out.println("Price: " + response.getResult());
                        }
                    }
                    else if (response.getType() == RequestType.ORDER) {
                        System.out.println("Result: " + response.getResult());
                    }
                    else if (response.getType() == RequestType.READ) {
                        System.out.println(response.getResult());
                    }
                })
                .matchAny(o -> log.info("Received unknown message"))
                .build();
    }

    public static String getTitle(String line) {
        String[] splited = line.split(" ");

        return String.join(" ", Arrays.copyOfRange(splited, 1, splited.length));
    }

    private static SupervisorStrategy strategy
            = new AllForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
            matchAny(o -> restart()).
            build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}
