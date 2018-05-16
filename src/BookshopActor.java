import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class BookshopActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    if (s.startsWith("msg")) {
                        getSender().tell("result: " + s.toUpperCase(), getSelf());
                    }
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
