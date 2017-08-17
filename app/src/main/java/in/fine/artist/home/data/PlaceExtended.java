package in.fine.artist.home.data;

import java.io.Serializable;

/**
 * Created by apoorvarora on 22/05/17.
 */
public class PlaceExtended implements Serializable {
    private SUGGESTION_TYPE suggestionType;
    private int placeType; // -1 for default, 0 for recent , 1 for frequent , 2 for results
    private String placeId;
    private String address;
    private String district;
    private String icon;

    public PlaceExtended() {
    }

    public enum SUGGESTION_TYPE {
        RECENT,
        SUGGESTION,
        DEFAULT
    }

    public SUGGESTION_TYPE getSuggestionType() {
        return suggestionType;
    }

    public void setSuggestionType(SUGGESTION_TYPE suggestionType) {
        this.suggestionType = suggestionType;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPlaceType() {
        return placeType;
    }

    public void setPlaceType(int placeType) {
        this.placeType = placeType;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
