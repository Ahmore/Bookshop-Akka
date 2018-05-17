import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.OverflowStrategy;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import scala.concurrent.duration.FiniteDuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;

public class ReadActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(ReadAction.class, action -> {
                    String filename = "bookshop/books/" + action.getTitle() + ".txt";

                    File file = new File(filename);

                    if (!file.exists()){
                        action.getSender().tell(new Response(RequestType.READ, "Book does not exist"), getSelf());
                    }
                    else {
                        ActorMaterializer materializer = ActorMaterializer.create(context());
                        ActorRef run = Source.actorRef(1000, OverflowStrategy.dropNew())
                                .throttle(1, FiniteDuration.create(1, TimeUnit.SECONDS), 1, ThrottleMode.shaping())
                                .to(Sink.actorRef(action.getSender(), NotUsed.getInstance()))
                                .run(materializer);

                        BufferedReader br = new BufferedReader(new FileReader(filename));
                        String line;

                        while ((line = br.readLine()) != null) {
                            run.tell(new Response(RequestType.READ, line), getSelf());
                        }
                    }
                })
                .matchAny(o -> log.info("Received unknown message"))
                .build();
    }
}