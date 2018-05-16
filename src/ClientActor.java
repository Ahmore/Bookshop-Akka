import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ClientActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    if (s.startsWith("msg")) {
                        getContext().actorSelection("akka.tcp://bookshop_system@127.0.0.1:3552/user/bookshop").tell(s, getSelf());
                    }
                    else if (s.startsWith("result")) {
                        System.out.println(s);              // result from child
                    }
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
