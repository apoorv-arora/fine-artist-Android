package in.fine.artist.home.data.course;

import java.io.Serializable;

/**
 * Created by apoorvarora on 15/07/17.
 */
public class Label implements Serializable {
    private String iconUrl;
    private String description;

    public Label(){}

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
