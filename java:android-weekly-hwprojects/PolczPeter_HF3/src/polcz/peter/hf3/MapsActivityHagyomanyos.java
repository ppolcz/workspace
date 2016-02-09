package polcz.peter.hf3;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivityHagyomanyos extends FragmentActivity {
    static final LatLng PERTH = new LatLng(-31.90, 115.86);

    private GoogleMap map;
    private LatLng mLocation;
    private Marker mLocationMarker;
    private boolean mLocationFound = true;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        map = mapFragment.getMap();
        map.setMyLocationEnabled(true);

        map.setInfoWindowAdapter(new InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow (Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents (Marker arg0) {

                // Getting view from the layout file info_window_layout
                View view = getLayoutInflater().inflate(R.layout.list_item, null);

                // Returning the view containing InfoWindow contents
                return view;
            }
        });

        getLocation();

    }

    private void makeUseOfNewLocation (Location location) {
        mLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mLocationMarker.setPosition(mLocation);
        if (!mLocationFound) {
            animateThere();
            mLocationFound = true;
        }
    }

    private void getLocation () {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged (Location location) {
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged (String provider, int status, Bundle extras) {}

            public void onProviderEnabled (String provider) {}

            public void onProviderDisabled (String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);

        try {
            Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            mLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
            animateThere();
        } catch (NullPointerException e) {
            mLocationFound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapnormal:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return false;
            case R.id.mapsatellite:
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return false;
            case R.id.mapterrain:
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return false;
            case R.id.maphybrid:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void animateThere() {
        mLocationMarker = map.addMarker(new MarkerOptions().position(mLocation));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLocation, 17);
        map.animateCamera(cameraUpdate);
    }
}
