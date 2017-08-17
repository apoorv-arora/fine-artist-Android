package in.fine.artist.home.data.course;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by apoorvarora on 15/07/17.
 */
public class CourseDetail implements Serializable {

    private String headerBackgroundImageUrl;
    private String headerTitle;

    private String aboutTheCourse;
    private String createdByImageUrl;

    private ArrayList<CourseObjectives> courseObjectives;
    private ArrayList<SubCourse> subCourses;

    public CourseDetail(){}

    public String getHeaderBackgroundImageUrl() {
        return headerBackgroundImageUrl;
    }

    public void setHeaderBackgroundImageUrl(String headerBackgroundImageUrl) {
        this.headerBackgroundImageUrl = headerBackgroundImageUrl;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public String getAboutTheCourse() {
        return aboutTheCourse;
    }

    public void setAboutTheCourse(String aboutTheCourse) {
        this.aboutTheCourse = aboutTheCourse;
    }

    public String getCreatedByImageUrl() {
        return createdByImageUrl;
    }

    public void setCreatedByImageUrl(String createdByImageUrl) {
        this.createdByImageUrl = createdByImageUrl;
    }

    public ArrayList<CourseObjectives> getCourseObjectives() {
        return courseObjectives;
    }

    public void setCourseObjectives(ArrayList<CourseObjectives> courseObjectives) {
        this.courseObjectives = courseObjectives;
    }

    public ArrayList<SubCourse> getSubCourses() {
        return subCourses;
    }

    public void setSubCourses(ArrayList<SubCourse> subCourses) {
        this.subCourses = subCourses;
    }
}
