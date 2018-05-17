package bookshop.actors;

import akka.actor.AbstractActor;
import akka.actor.AllForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import bookshop.actions.FindAction;
import bookshop.others.FindResult;
import bookshop.others.Finder;
import scala.concurrent.duration.Duration;
import shared.Request;
import shared.Response;

import java.util.LinkedList;

import static akka.actor.SupervisorStrategy.restart;

public class FindActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private LinkedList<Finder> finders = new LinkedList<>();

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, request -> {
                    String finder1 = "find" + java.util.UUID.randomUUID().toString();
                    String finder2 = "find" + java.util.UUID.randomUUID().toString();

                    this.finders.push(new Finder(finder1, finder2));

                    context().actorOf(Props.create(FindActorChild.class), finder1);
                    context().actorOf(Props.create(FindActorChild.class), finder2);
                    context().child(finder1).get().tell(new FindAction(request.getTitle(), "bookshop/db1.txt", getSender()), getSelf());
                    context().child(finder2).get().tell(new FindAction(request.getTitle(), "bookshop/db2.txt", getSender()), getSelf());
                })
                .match(FindResult.class, result -> {
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
                })
                .matchAny(o -> log.info("Received unknown message"))
                .build();
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
