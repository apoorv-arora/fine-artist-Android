package in.fine.artist.home.data.course;

import java.io.Serializable;

/**
 * Created by apoorvarora on 15/07/17.
 */
public class CourseObjectives implements Serializable {

    private String iconUrl;
    private String title;
    private String description;

    public CourseObjectives(){}

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
