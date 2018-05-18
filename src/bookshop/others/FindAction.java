package bookshop.others;

import akka.actor.ActorRef;

public class FindAction {
    private String title;
    private String db;
    private ActorRef sender;

    public FindAction(String title, String db, ActorRef sender) {
        this.title = title;
        this.db = db;
        this.sender = sender;
    }

    public String getTitle() {
        return title;
    }

    public String getDb() {
        return db;
    }

    public ActorRef getSender() {
        return sender;
    }
}
