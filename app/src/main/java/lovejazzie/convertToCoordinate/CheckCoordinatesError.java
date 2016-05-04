package lovejazzie.convertToCoordinate;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class CheckCoordinatesError {
    private static double[] defultLoc;
    private static double[] bDloc;
    private static checkloc mListener;

    public CheckCoordinatesError(Context context) {
    }

    @Nullable
    public static String getDefultLoc(Context mContext) {
        if (mContext == null) return null;
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    defultLoc = new double[2];
                    defultLoc[0] = location.getLatitude();
                    defultLoc[1] = location.getLongitude();

                    locIsSame();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            }, Looper.getMainLooper());
            if (lastKnownLocation == null) return null;
            double latitude = lastKnownLocation.getLatitude();
            double longitude = lastKnownLocation.getLongitude();
            return latitude + "," + longitude;
        }
        return null;
    }


    @Nullable
    public static void getBDLoc(Context mContext) {
        if (mContext == null) return;

        Context applicationContext = mContext.getApplicationContext();
        LocationClient client = new LocationClient(applicationContext);
        BDLocationListener locationListener = new MyLocationListener();
        client.registerLocationListener(locationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setScanSpan(0);
        option.setIgnoreKillProcess(false);
        client.setLocOption(option);
        client.start();

    }

    public interface checkloc {
        boolean check(boolean a);
    }

    public static void locIsSame() {
        if (bDloc == null) return;
        if (defultLoc == null) return;
        mListener.check(Math.abs(defultLoc[0] - bDloc[0]) > 0.001 && Math.abs(defultLoc[1] - bDloc[1]) > 0.001);
        bDloc = defultLoc = null;

    }

    private static class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            bDloc = new double[2];
            bDloc[0] = bdLocation.getLatitude();
            bDloc[1] = bdLocation.getLongitude();
            locIsSame();
        }
    }

    public static void setCheckListener(checkloc listener) {
        mListener = listener;
    }

    public static void init(final Context mContext) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                getBDLoc(mContext);
                getDefultLoc(mContext);


            }
        });

    }
}
