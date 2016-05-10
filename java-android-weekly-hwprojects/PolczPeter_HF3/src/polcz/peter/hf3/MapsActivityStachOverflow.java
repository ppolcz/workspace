package polcz.peter.hf3;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivityStachOverflow extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        OnMarkerClickListener {

    /* Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    /* Log tag */
    private final static String tag = "PolpeTag-MainActivity-StackOverflow";

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationClient mLocationClient;

    private Marker mLocationMarker;

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog (Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog (Bundle savedInstanceState) {
            return mDialog;
        }
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        Log.d(tag, "MainActivity#onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationClient = new LocationClient(this, this, this);

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        map = mapFragment.getMap();

        map.setMyLocationEnabled(true);
        map.setOnMarkerClickListener(this);

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
                View v = getLayoutInflater().inflate(R.layout.list_item, null);

                // // Getting the position from the marker
                // LatLng latLng = arg0.getPosition();
                //
                // // Getting reference to the TextView to set latitude
                // TextView mName = (TextView) v.findViewById(R.id.tw_name);
                //
                // // Getting reference to the TextView to set longitude
                // TextView mPoet = (TextView) v.findViewById(R.id.tw_poet);
                //
                // // Setting the latitude
                // mName.setText("Latitude:" + latLng.latitude);
                //
                // // Setting the longitude
                // mPoet.setText("Longitude:"+ latLng.longitude);

                // Returning the view containing InfoWindow contents
                return v;
            }
        });

    }

    /* Called when the Activity becomes visible. */
    @Override
    protected void onStart () {
        super.onStart();
        Log.d(tag, "MainActivity#onStart()");

        // Connect the client.
        if (isGooglePlayServicesAvailable()) {

            // emiatt hivodik majd meg egy masik szalon az onConnect()
            mLocationClient.connect();
        }
    }

    /* Called when the Activity is no longer visible. */
    @Override
    protected void onStop () {
        Log.d(tag, "MainActivity#onStop()");
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    /* Handle results returned to the FragmentActivity
     * by Google Play services */
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        Log.d(tag, "MainActivity#onActivityResult()");

        // Decide what to do based on the original request code
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                /* If the result code is Activity.RESULT_OK, try
                 * to connect again */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mLocationClient.connect();
                        break;
                }
        }
    }

    private boolean isGooglePlayServicesAvailable () {
        Log.d(tag, "MainActivity#isGooglePlayServiceAvailable()");

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(tag, "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }

    /* Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates */
    @Override
    public void onConnected (Bundle dataBundle) {
        Log.d(tag, "MainActivity#onConnected()");

        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        Location location = mLocationClient.getLastLocation();
        LatLng latLng;
        try {
            location.getLatitude();
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (NullPointerException e) {
            latLng = new LatLng(47, 19);
        }

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        map.animateCamera(cameraUpdate);

        mLocationMarker = map.addMarker(new MarkerOptions().position(latLng).title("Actual Location").snippet("Snippet"));
    }

    /* Called by Location Services if the connection to the
     * location client drops because of an error. */
    @Override
    public void onDisconnected () {
        Log.d(tag, "MainActivity#onDisconnected()");
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    /* Called by Location Services if the attempt to
     * Location Services fails. */
    @Override
    public void onConnectionFailed (ConnectionResult connectionResult) {
        Log.d(tag, "MainActivity#onConnectionFailed()");
        /* Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error. */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /* Thrown if Google Play services canceled the original
                 * PendingIntent */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onMarkerClick (final Marker marker) {
        Log.d(tag, "MapsActivityStachOverflow - onMarkerClick()");

        if (marker.equals(mLocationMarker)) {
            Toast.makeText(this, "Marker clicked", Toast.LENGTH_SHORT).show();
        }

        return false;
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

}
