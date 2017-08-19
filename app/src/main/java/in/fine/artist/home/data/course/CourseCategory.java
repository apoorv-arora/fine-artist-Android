package in.fine.artist.home.data.course;

import java.io.Serializable;

/**
 * Created by apoorvarora on 15/07/17.
 */
public class CourseCategory implements Serializable {
    private int categoryId;
    private String categoryTitle;
    private String categoryIconUrl;

    public CourseCategory(){}

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getCategoryIconUrl() {
        return categoryIconUrl;
    }

    public void setCategoryIconUrl(String categoryIconUrl) {
        this.categoryIconUrl = categoryIconUrl;
    }
}
