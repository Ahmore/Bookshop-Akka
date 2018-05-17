package bookshop.actors;

import akka.actor.AbstractActor;
import akka.actor.AllForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;
import shared.Request;
import shared.ResponseType;
import shared.Response;

import java.io.*;

import static akka.actor.SupervisorStrategy.restart;

public class OrderActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, request -> {
                    Writer output;
                    output = new BufferedWriter(new FileWriter("bookshop/orders.txt", true));
                    output.append(request.getTitle() + "\n");
                    output.close();

                    getSender().tell(new Response(ResponseType.ORDER, "Successfully ordered"), getSelf());
                })
                .matchAny(o -> log.info("Received unknown message"))
                .build();
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

