import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

public class FindActor extends AbstractActor {
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
                            getSender().tell(new Response(RequestType.FIND, parsedLine[1]), getSelf());

                            return;
                        }
                    }

                    getSender().tell(new Response(RequestType.FIND, ""), getSelf());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    public static String[] parseLine(String line) {
        String[] splited = line.split(" ");
        String title = String.join(" ", Arrays.copyOfRange(splited, 0, splited.length-1));
        String price = splited[splited.length-1];

        String[] result = {title, price};

        return result;
    }
}

