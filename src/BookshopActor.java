import akka.actor.AbstractActor;
import akka.actor.AllForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import scala.concurrent.duration.Duration;

import java.io.FileNotFoundException;
import java.util.LinkedList;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;
import static akka.actor.SupervisorStrategy.stop;

public class BookshopActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private LinkedList<Finder> finders = new LinkedList<>();

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, request -> {
                    if (request.getType().equals(RequestType.FIND)) {
                        String finder1 = "find" + java.util.UUID.randomUUID().toString();
                        String finder2 = "find" + java.util.UUID.randomUUID().toString();

                        this.finders.push(new Finder(finder1, finder2));

                        context().actorOf(Props.create(FindActor.class), finder1);
                        context().actorOf(Props.create(FindActor.class), finder2);
                        context().child(finder1).get().tell(new FindAction(request.getTitle(), "bookshop/db1.txt", getSender()), getSelf());
                        context().child(finder2).get().tell(new FindAction(request.getTitle(), "bookshop/db2.txt", getSender()), getSelf());
                    }
                    else if (request.getType().equals(RequestType.ORDER)) {
                        context().child("order").get().tell(new OrderAction(request.getTitle(), getSender()), getSelf());
                    }
                    else if (request.getType().equals(RequestType.READ)) {
                        context().child("read").get().tell(new ReadAction(request.getTitle(), getSender()), getSelf());
                    }
                })
                .match(Result.class, result -> {
                    if (result.getType() == RequestType.FIND) {
                        String finderName = getSender().path().name();
                        Finder finder = this.getFinder(finderName);

                        if (finder != null) {
                            // Found
                            if (!result.getResult().equals("")) {
                                if (finder.getFinder1().equals(finderName)) {
                                    finder.setState1(1);

                                    if (finder.getState2() != 1) {
                                        result.getSender().tell(new Response(result.getType(), result.getResult()), null);
                                    }
                                    context().stop(context().child(finder.getFinder1()).get());
                                }
                                else {
                                    finder.setState2(1);

                                    if (finder.getState1() != 1) {
                                        result.getSender().tell(new Response(result.getType(), result.getResult()), null);
                                    }
                                    context().stop(context().child(finder.getFinder2()).get());
                                }

                            }
                            // Not found
                            else {
                                if (finder.getFinder1().equals(finderName)) {
                                    finder.setState1(-1);

                                    if (finder.getState2() == -1) {
                                        result.getSender().tell(new Response(result.getType(), result.getResult()), null);
                                    }
                                    context().stop(context().child(finder.getFinder1()).get());
                                }
                                else {
                                    finder.setState2(-1);

                                    if (finder.getState1() == -1) {
                                        result.getSender().tell(new Response(result.getType(), result.getResult()), null);
                                    }
                                    context().stop(context().child(finder.getFinder2()).get());
                                }
                            }
                        }
                    }
                })
                .matchAny(o -> log.info("Received unknown message"))
                .build();
    }

    @Override
    public void preStart() throws Exception {
        context().actorOf(Props.create(OrderActor.class), "order");
        context().actorOf(Props.create(ReadActor.class), "read");
    }

    public Finder getFinder(String finder) {
        for (Finder obj : this.finders) {
            if (obj.getFinder1().equals(finder) || obj.getFinder2().equals(finder)) {
                return obj;
            }
        }

        return null;
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
