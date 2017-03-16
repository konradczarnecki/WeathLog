package k.weathlog;

import android.content.pm.PackageManager;

import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        WeatherUpdater.WeatherUpdateListener, GoogleApiClient.OnConnectionFailedListener,
        CurrentConditionsFragment.OnSaveButtonClickedListener, SaveEntryFragment.SaveEntryListener,
        LocationListener, ManualLocationFragment.OnManualLocationListener {


    private GoogleApiClient gApiClient;
    private FusedLocationProviderApi locationProvider;

    private CurrentConditionsFragment conditionsFragment;
    private FiveDayForecastFragment fiveDayFragment;
    private TwelveHourForecastFragment twelveHourFragment;

    private FrameLayout currentConditionsFrame;

    private WeatherUpdater updater;

    private Weather currentWeather;
    private Boolean locationUpdateSuccess;

    private FragmentManager fm;

    private Location lastLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updater = new WeatherUpdater(this);

        currentConditionsFrame = (FrameLayout) findViewById(R.id.current_conditions_frame);
        currentConditionsFrame.setVisibility(View.INVISIBLE);

        conditionsFragment = CurrentConditionsFragment.newInstance();
        fiveDayFragment = FiveDayForecastFragment.newInstance();
        twelveHourFragment = TwelveHourForecastFragment.newInstance();

        locationProvider = LocationServices.FusedLocationApi;

        fm = getSupportFragmentManager();

        if (gApiClient == null) {
            gApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    @Override
    protected void onResume() {

        super.onResume();

        showDefaultFragments();
    }

    @Override
    protected void onStart() {
        gApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        gApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        ActivityCompat.requestPermissions(this,
                new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {


        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 1) {


            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationUpdateSuccess = false;

            startTimeoutTimer();

            try {

                locationProvider.requestLocationUpdates(gApiClient, locationRequest, this);

                Location location = locationProvider.getLastLocation(gApiClient);

                if(location != null) onLocationChanged(location);

            } catch(SecurityException s){}
        }

    }

    @Override
    public void onLocationChanged(Location loc){

        locationUpdateSuccess = true;

        updater.updateAll(loc.getLatitude(), loc.getLongitude());

        lastLocation = loc;

        locationProvider.removeLocationUpdates(gApiClient, this);
    }


    @Override
    public void onConnectionSuspended(int a){

    }

    @Override
    public void onCurrentConditionsUpdated(Weather currentWeather){

        conditionsFragment.update(currentWeather);
        currentConditionsFrame.setVisibility(View.VISIBLE);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onFiveDayForecastUpdated(Weather[] forecast){

        fiveDayFragment.update(forecast);
    }

    @Override
    public void onTwelveHourForecastUpdated(Weather[] forecast){

        twelveHourFragment.update(forecast);

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.twelve_hour_forecast_frame, twelveHourFragment);

        transaction.commit();

        FrameLayout twelveHourFrame = (FrameLayout) findViewById(R.id.twelve_hour_forecast_frame);
        twelveHourFrame.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveButtonClicked(Weather currentWeather){

        this.currentWeather = currentWeather;

        SaveEntryFragment saveFragment = SaveEntryFragment.newInstance();

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.twelve_hour_forecast_frame, saveFragment);
        transaction.remove(fiveDayFragment);

        transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    public void onEntrySaved(){

        showDefaultFragments();
    }

    public void onBackPressed(){

        super.onBackPressed();

        showDefaultFragments();
    }

    @Override
    public Weather getCurrentWeather(){
        return currentWeather;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result){

        Log.i("Connection failed code:",Integer.toString(result.getErrorCode()));
    }

    private void showDefaultFragments(){


        FragmentTransaction transaction = fm.beginTransaction();

        transaction.replace(R.id.current_conditions_frame, conditionsFragment);
        transaction.replace(R.id.five_day_forecast_frame, fiveDayFragment);
        transaction.replace(R.id.twelve_hour_forecast_frame, twelveHourFragment);

        transaction.commit();
    }

    private void showManualLocationFragment(){

        ManualLocationFragment manualLocation = ManualLocationFragment.newInstance();

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.twelve_hour_forecast_frame, manualLocation);

        transaction.commit();

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onManualLocationProvided(Location loc){

        FrameLayout twelveHourFrame = (FrameLayout) findViewById(R.id.twelve_hour_forecast_frame);
        twelveHourFrame.setVisibility(View.INVISIBLE);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        lastLocation = loc;

        updater.updateAll(loc.getLatitude(), loc.getLongitude());
    }


    private void startTimeoutTimer() {

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                if(!locationUpdateSuccess && lastLocation == null) showManualLocationFragment();
            }
        }.execute();
    }


}
