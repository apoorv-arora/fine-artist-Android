package in.fine.artist.home.utils.autocomplete;

import android.content.Intent;

import com.google.android.gms.location.places.Place;

/**
 * Created by apoorvarora on 22/05/17.
 */
public interface GoogleAutocompleteCallbacks {
    void onAutocompleteClicked(int reqCode, Intent data);

    void onPlaceSelected(int reqCode, Place place);

    void onPlaceCancelled(int reqCode, Intent data);

    void onAutocompleteEmptyResults(int requestCode, Intent data);

    void onNoNetworkCallInProgress(int requestCode, Intent data);

    void onPlacesFetchFailed(int requestCode, Intent data);
}
