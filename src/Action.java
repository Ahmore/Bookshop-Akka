import akka.actor.ActorRef;

public interface Action {
    public ActorRef getSender();
}
