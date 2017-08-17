package in.fine.artist.home.data.course;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by apoorvarora on 15/07/17.
 */
public class SubCourse implements Serializable {

    private String title;
    private long startDate;
    private String commitment;
    private String aboutTheCourse;
    private ArrayList<Schedule> schedules;

    public SubCourse(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public String getCommitment() {
        return commitment;
    }

    public void setCommitment(String commitment) {
        this.commitment = commitment;
    }

    public String getAboutTheCourse() {
        return aboutTheCourse;
    }

    public void setAboutTheCourse(String aboutTheCourse) {
        this.aboutTheCourse = aboutTheCourse;
    }

    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
    }
}
