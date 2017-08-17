package in.fine.artist.home.data.course;

import java.io.Serializable;

/**
 * Created by apoorvarora on 15/07/17.
 */
public class CourseBrief implements Serializable {
    private String title;
    private String shortDescription;
    private String coverImageUrl;

    public CourseBrief(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
}
