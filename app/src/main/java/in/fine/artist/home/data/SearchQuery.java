package in.fine.artist.home.data;

import java.io.Serializable;

/**
 * Created by apoorvarora on 27/05/17.
 */
public class SearchQuery implements Serializable {
    private String sourceString;
    private String sourcePlaceId;
    private String destinationString;
    private String destinationPlaceId;

    public SearchQuery(){}

    public String getSourceString() {
        return sourceString;
    }

    public void setSourceString(String sourceString) {
        this.sourceString = sourceString;
    }

    public String getSourcePlaceId() {
        return sourcePlaceId;
    }

    public void setSourcePlaceId(String sourcePlaceId) {
        this.sourcePlaceId = sourcePlaceId;
    }

    public String getDestinationString() {
        return destinationString;
    }

    public void setDestinationString(String destinationString) {
        this.destinationString = destinationString;
    }

    public String getDestinationPlaceId() {
        return destinationPlaceId;
    }

    public void setDestinationPlaceId(String destinationPlaceId) {
        this.destinationPlaceId = destinationPlaceId;
    }
}
