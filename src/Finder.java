import akka.actor.ActorRef;

public class Finder {
    String finder1;
    int state1 = 0;
    String finder2;
    int state2 = 0;
    ActorRef sender;

    public Finder(String finder1, String finder2, ActorRef sender) {
        this.finder1 = finder1;
        this.finder2 = finder2;
        this.sender = sender;
    }

    public String getFinder1() {
        return finder1;
    }

    public int getState1() {
        return state1;
    }

    public void setState1(int state1) {
        this.state1 = state1;
    }

    public String getFinder2() {
        return finder2;
    }

    public int getState2() {
        return state2;
    }

    public void setState2(int state2) {
        this.state2 = state2;
    }

    public ActorRef getSender() {
        return sender;
    }
}
