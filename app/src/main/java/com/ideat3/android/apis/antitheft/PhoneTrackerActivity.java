package com.ideat3.android.apis.antitheft;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.util.List;
import java.util.Locale;

/**
 * Created by akshayjnambiar on 11/13/2015.
 */
public class PhoneTrackerActivity extends Activity implements LocationListener {

    private String TrustedPhoneNumber;
    private boolean mLocationTracking = false;
    private boolean mDisableTracking = false;
    private boolean mFirstTrack = true;

    private static PhoneTrackerActivity pTActivity;

    private LocationManager mLocationManager;
    private String mBestProvider;

    private Location mLocation;

    private final Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        pTActivity = this;
    }

    @Override
    public void onResume() {
        Log.d("TAG","onResume");
        super.onResume();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        handler.post(getData);
    }

    @Override
    public void onDestroy() {
        Log.d("TAG", "onDestroy");
        super.onDestroy();
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            Log.d("TAG", "No permission for GPS || internet");
            //enableGPS();
            return;
        }
        mLocationManager.removeUpdates(this);
        handler.removeCallbacksAndMessages(null);
        if(!StartActivity.debug) {
            //DevicePolicyManaging.resetPhone(pTActivity);
        }
    }

    private final Runnable getData = new Runnable() {
        @Override
        public void run() {
            getDataFrame();
        }
    };

    private void getDataFrame() {
        Criteria criteria = new Criteria();
        final boolean gpsEnabled = mLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.d("TAG", "getDataFrame");

        if (!mFirstTrack) {
            Log.d("TAG","not first track");
            Log.d("TAG",Boolean.toString(mLocationTracking) +Boolean.toString(mDisableTracking));
            if (!mLocationTracking && !mDisableTracking) {
                Log.d("TAG","not location tracking and not disable tracking");
                if (!gpsEnabled && isGPSToggleable()) {
                    enableGPS();
                }

                if (gpsEnabled) {
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                } else {
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                }

                mLocationTracking = true;
                mBestProvider = mLocationManager.getBestProvider(criteria, true);
                Log.d("TAG", "Provider : " + mBestProvider );
                startTracking();
            }

        } else {
            Log.d("TAG", "First Track");
            Log.d("TAG",Boolean.toString(mLocationTracking) +Boolean.toString(mDisableTracking));
            startTracking();
        }


        if (mLocationTracking && mDisableTracking) {
            stopTracking();
        }

        handler.postDelayed(getData, 1000);
    }

    private boolean isGPSToggleable() {
        Log.d("TAG","In isGPSToggleable");
        PackageManager pacman = getPackageManager();
        PackageInfo pacInfo = null;

        try {
            pacInfo = pacman.getPackageInfo("com.android.settings",
                    PackageManager.GET_RECEIVERS);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

        if (pacInfo != null) {
            for (ActivityInfo actInfo : pacInfo.receivers) {
                if (actInfo.name
                        .equals("com.android.settings.widget.SettingsAppWidgetProvider")
                        && actInfo.exported) {
                    Log.d("TAG","returning true");
                    return true;
                }
            }
        }
        Log.d("TAG", "returning false");
        return false;
    }

    private void stopTracking() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            Log.d("TAG", "No permission for GPS || internet");
            //enableGPS();
            return;
        }
        mLocationManager.removeUpdates(this);
        handler.removeCallbacksAndMessages(null);
    }


    private void startTracking() {
        Log.d("TAG","startTracking");
        final boolean gpsEnabled = mLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        final boolean networkEnabled = mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        TrustedPhoneNumber = preferences.getString(MissedCallDetector.PREFERENCES_TRUSTED_PHONENUMBER,"Default");
        if (!mFirstTrack && gpsEnabled) {
            Log.d("TAG", "Start Tracking by GPS and not first track here");
            Log.d("TAG",Boolean.toString(mLocationTracking) +Boolean.toString(mDisableTracking));

            int mLocateUpdateDuration = 2;

            mLocateUpdateDuration = mLocateUpdateDuration * 1000 * 60;

            int mLocateMinimumDistance = 10;

            Log.d("TAG", "Location update interval is set at: " + mLocateUpdateDuration);
            Log.d("TAG", "Location minimum distance is set at: " + mLocateMinimumDistance);

            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                Log.d("TAG", "No permission for GPS || internet");
                //enableGPS();
                return;
            }
            mLocationManager.requestLocationUpdates(mBestProvider, mLocateUpdateDuration,
                    mLocateMinimumDistance, this);

            mLocation = mLocationManager.getLastKnownLocation(mBestProvider);
            if(mLocation!=null) {
                Log.d("TAG",Double.toString(mLocation.getLatitude()) + " " + Double.toString(mLocation.getLongitude()));
            }
            else Log.d("TAG","mLocation null");

        } else if (!mFirstTrack && networkEnabled) {
            Log.d("TAG", "Tracking by Network Location and not first track");
            Log.d("TAG",Boolean.toString(mLocationTracking) +Boolean.toString(mDisableTracking));

            int mLocateUpdateDuration = 2;

            mLocateUpdateDuration = mLocateUpdateDuration * 1000 * 60;

            int mLocateMinimumDistance = 10;

            Log.d("TAG", "Location update interval is set at: " + mLocateUpdateDuration);
            Log.d("TAG", "Location minimum distance is set at: " + mLocateMinimumDistance);

            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, mLocateUpdateDuration, mLocateMinimumDistance, this);
            mLocation = mLocationManager.getLastKnownLocation(mBestProvider);
            if(mLocation!=null) {
                Log.d("TAG",Double.toString(mLocation.getLatitude()) + " " + Double.toString(mLocation.getLongitude()));
            }
            else Log.d("TAG","mLocation null");

        } else {
            mFirstTrack = false;
            Log.d("TAG", "First Track && Tracking by Network Location");
            Log.d("TAG",Boolean.toString(mLocationTracking) +Boolean.toString(mDisableTracking));
            mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(mLocation!=null) {
                Log.d("TAG",Double.toString(mLocation.getLatitude()) + " " + Double.toString(mLocation.getLongitude()));
                onLocationChanged(mLocation);
            }
            else Log.d("TAG","mLocation null");
        }
    }

    public void disableTracking() {
        mDisableTracking = true;
    }

    private void enableGPS() {
        Log.d("TAG", "enableGPS()");
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) {
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("TAG","Location changed");
        String mLocationToSend;
        mLocationToSend = locationFormatForSite(location) + "\n" + locationFormatForGeocode(location);
        Log.d("TAG",mLocationToSend);
        if ((isBetterLocation(location, mLocation)) || mFirstTrack) {
            Log.d("TAG", "Sending SMS location update");
            Log.d("TAG", mLocationToSend);
            if(StartActivity.debug) {
                Toast.makeText(PhoneTrackerActivity.this, mLocationToSend, Toast.LENGTH_LONG).show();
            }
            else {
                Utils.SendSMS(this, mLocationToSend);
            }
            onDestroy();
        } else {
            Log.d("TAG", "Not better Location");
        }
    }

    private String locationFormatForSite(Location location) {
        Log.d("TAG","locationFormatForSite()");
        return "Your phone is here: " + "https://maps.google.com/maps?q=" + location.getLatitude() + ",+" + location.getLongitude()
                 + "\n" + "With accuracy of: " + location.getAccuracy() + " meters";
    }

    private String locationFormatForGeocode(Location location) {
        Log.d("TAG","locationFormatForGeocode()");
        return (geoCodeMyLocation(location.getLatitude(),
                location.getLongitude()));
    }

    private String geoCodeMyLocation(double latitude, double longitude) {
        Log.d("TAG","Geocode my location");
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,
                    longitude, 1);

            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder(getResources().getString(R.string.tracking_address));
                // strReturnedAddress.append("\n");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress
                            .append(returnedAddress.getAddressLine(i)).append(
                            "\n");
                }
                return strReturnedAddress.toString();
            } else {
                return getResources().getString(R.string.tracking_returned_no_location);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return getResources().getString(R.string.tracking_returned_no_location);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    private boolean isBetterLocation(Location location,
                                     Location currentBestLocation) {
        Log.d("TAG","isBetterLocation");
        int mLocateUpdateDurationOlder = 2;

        //Multiply chosen value by 2 to return a location regardless if more accurate or not.
        mLocateUpdateDurationOlder = mLocateUpdateDurationOlder * 1000 * 2;

        Log.d("TAG", "Significantly Older interval is set at: " + mLocateUpdateDurationOlder);

        if (currentBestLocation == null) {
            Log.d("TAG","Is this true");
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        Log.d("TAG", Long.toString(timeDelta));
        boolean isSignificantlyNewer = timeDelta > mLocateUpdateDurationOlder;
        boolean isSignificantlyOlder = timeDelta < mLocateUpdateDurationOlder;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
                .getAccuracy());

        Log.d("TAG",Integer.toString(accuracyDelta));

        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate
                && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
