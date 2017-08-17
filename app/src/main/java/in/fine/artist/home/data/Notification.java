package in.fine.artist.home.data;

import java.io.Serializable;

/**
 * Created by apoorvarora on 21/04/17.
 */
public class Notification implements Serializable {
    private String title;
    private String body;
    private Object data;

    public Notification() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
