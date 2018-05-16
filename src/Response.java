public class Response {
    private RequestType type;
    private String result;

    public Response(RequestType type, String result) {
        this.type = type;
        this.result = result;
    }

    public RequestType getType() {
        return type;
    }

    public String getResult() {
        return result;
    }
}
