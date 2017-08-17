package in.fine.artist.home.utils.autocomplete;

import android.net.Uri;
import android.text.TextUtils;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;
import java.util.Locale;

import in.fine.artist.home.data.PlaceExtended;

/**
 * Created by apoorvarora on 22/05/17.
 */
public class GoogleAutoCompleteUtils {
    // Intent params key constants
    public static final String INTENT_DESTINATION_RECENT_LIST = "destinationRecentList";
    public static final String INTENT_SOURCE_RECENT_LIST = "sourceRecentList";
    public static final String INTENT_EXTERNAL_PLACES_LIST = "externalPlacesList";
    public static final String INTENT_ACTIVE_COMPONENTS = "activeComponents"; // 0 for source only, 1 for destination only and 2 for both
    public static final String INTENT_SOURCE_PREFILL_DATA = "sourcePrefillData";
    public static final String INTENT_DESTINATION_PREFILL_DATA = "destinationPrefillData";
    public static final String INTENT_API_KEY = "apiKey";
    public static final String INTENT_SOURCE_SELECTED = "sourceSelected";

    public static final int PLACE_AUTOCOMPLETE_START_LOCATION = 1;
    public static final int PLACE_AUTOCOMPLETE_DROP_LOCATION = 2;

    public static Place getPlace(final String id, final String name, final String address, final LatLng latLng) {
        return new Place() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public List<Integer> getPlaceTypes() {
                return null;
            }

            @Override
            public CharSequence getAddress() {
                return address;
            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public CharSequence getName() {
                return name;
            }

            @Override
            public LatLng getLatLng() {
                return latLng;
            }

            @Override
            public LatLngBounds getViewport() {
                return null;
            }

            @Override
            public Uri getWebsiteUri() {
                return null;
            }

            @Override
            public CharSequence getPhoneNumber() {
                return null;
            }

            @Override
            public float getRating() {
                return 0;
            }

            @Override
            public int getPriceLevel() {
                return 0;
            }

            @Override
            public CharSequence getAttributions() {
                return null;
            }

            @Override
            public Place freeze() {
                return null;
            }

            @Override
            public boolean isDataValid() {
                return false;
            }
        };
    }

    public static PlaceExtended getPlaceExtended(Place place) {
        PlaceExtended placeExtended = new PlaceExtended();
        if (!TextUtils.isEmpty(place.getAddress()))
            placeExtended.setAddress(place.getAddress().toString());
        if (!TextUtils.isEmpty(place.getName()))
            placeExtended.setDistrict(place.getName().toString());
        placeExtended.setPlaceId(place.getId());
        return placeExtended;
    }

    public static boolean isValidString(String string){
        if(TextUtils.isEmpty(string) || string.equalsIgnoreCase("null")){
            return false;
        }else{
            return true;
        }
    }

    public static boolean isValidPlace(Place place) {
        return (place != null && !TextUtils.isEmpty(place.getAddress()) && !TextUtils.isEmpty(place.getId()));
    }

    public static boolean isValidPlace(PlaceExtended place) {
        return (place != null && !TextUtils.isEmpty(place.getAddress()) && !TextUtils.isEmpty(place.getPlaceId()));
    }
}
