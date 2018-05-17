package shared;

import java.io.Serializable;

public class Response implements Serializable {
    private ResponseType type;
    private String result;

    public Response(ResponseType type, String result) {
        this.type = type;
        this.result = result;
    }

    public ResponseType getType() {
        return type;
    }

    public String getResult() {
        return result;
    }
}
