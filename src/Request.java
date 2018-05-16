public class Request {
    private RequestType type;
    private String title;

    public Request(RequestType type, String title) {
        this.type = type;
        this.title = title;
    }

    public RequestType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }
}
