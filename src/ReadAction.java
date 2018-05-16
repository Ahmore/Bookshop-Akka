import akka.actor.ActorRef;

public class ReadAction {
    private String title;
    private ActorRef sender;

    public ReadAction(String title, ActorRef sender) {
        this.title = title;
        this.sender = sender;
    }

    public String getTitle() {
        return title;
    }

    public ActorRef getSender() {
        return sender;
    }
}
