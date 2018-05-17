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
                    Writer output;
                    output = new BufferedWriter(new FileWriter("bookshop/orders.txt", true));
                    output.append(action.getTitle() + "\n");
                    output.close();

                    getSender().tell(new Result(RequestType.ORDER, "Successfully ordered", action.getSender()), getSelf());
                })
                .matchAny(o -> log.info("Received unknown message"))
                .build();
    }
}

