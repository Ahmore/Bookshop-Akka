import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.*;

public class OrderActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(OrderAction.class, action -> {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("bookshop/orders.txt", true)));
                    out.println(action.getTitle());

                    getSender().tell(new Response(RequestType.ORDER, "Success"), getSelf());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}

