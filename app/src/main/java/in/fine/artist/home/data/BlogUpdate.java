package in.fine.artist.home.data;

import java.io.Serializable;

/**
 * Created by apoorvarora on 09/08/17.
 */

public class BlogUpdate implements Serializable {
    private int blogId;
    private String title;
    private String shortDescription;
    private String longDescription;
    private String refUrl;
    private String refImageUrl;
    private long created;
    private long modified;

    public BlogUpdate() {
    }

    public int getBlogId() {
        return blogId;
    }

    public void setBlogId(int blogId) {
        this.blogId = blogId;
    }

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

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getRefUrl() {
        return refUrl;
    }

    public void setRefUrl(String refUrl) {
        this.refUrl = refUrl;
    }

    public String getRefImageUrl() {
        return refImageUrl;
    }

    public void setRefImageUrl(String refImageUrl) {
        this.refImageUrl = refImageUrl;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }
}
