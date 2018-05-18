package bookshop.actors;

import akka.actor.AbstractActor;
import akka.actor.AllForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import bookshop.others.FindAction;
import bookshop.others.FindResult;
import scala.concurrent.duration.Duration;
import shared.ResponseType;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;

import static akka.actor.SupervisorStrategy.stop;

public class FindActorChild extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(FindAction.class, action -> {
                    BufferedReader br = new BufferedReader(new FileReader(action.getDb()));
                    String line;

                    while ((line = br.readLine()) != null) {
                        String[] parsedLine = parseLine(line);

                        if (parsedLine[0].equals(action.getTitle())) {
                            getSender().tell(new FindResult(ResponseType.FIND, parsedLine[1], action.getSender()), getSelf());

                            return;
                        }
                    }

                    getSender().tell(new FindResult(ResponseType.FIND, "", action.getSender()), getSelf());
                })
                .matchAny(o -> log.info("Received unknown message"))
                .build();
    }

    public static String[] parseLine(String line) {
        String[] splited = line.split(" ");
        String title = String.join(" ", Arrays.copyOfRange(splited, 0, splited.length-1));
        String price = splited[splited.length-1];

        String[] result = {title, price};

        return result;
    }

    private static SupervisorStrategy strategy
            = new AllForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
            match(FileNotFoundException.class, e ->
                    stop()
            ).
            matchAny(o -> stop()).
            build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}

