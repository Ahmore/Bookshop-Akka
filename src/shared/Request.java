package shared;

import java.io.Serializable;

public class Request implements Serializable {
    private String title;

    public Request(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
