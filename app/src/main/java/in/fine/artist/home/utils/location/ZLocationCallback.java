package in.fine.artist.home.utils.location;

import android.location.Location;

/**
 * Created by apoorvarora on 28/04/17.
 */
public interface ZLocationCallback {
    public void onCoordinatesIdentified(Location loc);
    public void onLocationIdentified();
    public void onLocationNotIdentified();
    public void onDifferentCityIdentified();
    public void locationNotEnabled();
    public void onLocationTimedOut();
    public void onNetworkError();
}