package in.fine.artist.home.data;

import java.io.Serializable;

/**
 * Created by apoorvarora on 13/05/17.
 */
public class AppConfig implements Serializable {
    private String tncUrl;
    private String aboutUsUrl;
    private String contactUsNumber;

    public AppConfig() {}

    public String getTncUrl() {
        return tncUrl;
    }

    public void setTncUrl(String tncUrl) {
        this.tncUrl = tncUrl;
    }

    public String getAboutUsUrl() {
        return aboutUsUrl;
    }

    public void setAboutUsUrl(String aboutUsUrl) {
        this.aboutUsUrl = aboutUsUrl;
    }

    public String getContactUsNumber() {
        return contactUsNumber;
    }

    public void setContactUsNumber(String contactUsNumber) {
        this.contactUsNumber = contactUsNumber;
    }
}
