import akka.actor.ActorRef;

public class Result {
    private RequestType type;
    private String result;
    private ActorRef sender;

    public Result(RequestType type, String result, ActorRef sender) {
        this.type = type;
        this.result = result;
        this.sender = sender;
    }

    public RequestType getType() {
        return type;
    }

    public String getResult() {
        return result;
    }

    public ActorRef getSender() {
        return sender;
    }
}
