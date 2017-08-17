package in.fine.artist.home.data.course;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by apoorvarora on 15/07/17.
 */
public class Schedule implements Serializable {
    private String timeline;
    private String title;
    private String description;
    private ArrayList<Label> labels;

    public Schedule(){}

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
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

    public ArrayList<Label> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<Label> labels) {
        this.labels = labels;
    }
}
