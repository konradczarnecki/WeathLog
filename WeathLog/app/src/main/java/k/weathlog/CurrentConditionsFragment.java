package k.weathlog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class CurrentConditionsFragment extends Fragment {

    private TextView temperatureText;
    private TextView pressureText;
    private TextView windSpeedText;
    private TextView cloudsText;
    private TextView dateAndLocation;
    private ImageView weatherIcon;

    private ImageView saveButton;
    private ImageView logButton;

    private IconManager iconManager;

    private Intent openWeatherLogIntent;

    private OnSaveButtonClickedListener onSaveClickedCallbackListener;
    private OnCreatedViewsListener onViewsCreatedCallbackListener;

    private Weather currentWeather;

    public CurrentConditionsFragment() {}

    public static CurrentConditionsFragment newInstance() {

        CurrentConditionsFragment fragment = new CurrentConditionsFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_current_conditions, container, false);

        temperatureText = (TextView) fragmentView.findViewById(R.id.current_temperature);
        pressureText = (TextView) fragmentView.findViewById(R.id.pressure);
        windSpeedText = (TextView) fragmentView.findViewById(R.id.wind_speed);
        cloudsText = (TextView) fragmentView.findViewById(R.id.clouds);
        weatherIcon = (ImageView) fragmentView.findViewById(R.id.current_weather_icon);

        dateAndLocation = (TextView) fragmentView.findViewById(R.id.date_and_location);

        saveButton = (ImageView) fragmentView.findViewById(R.id.save_button);
        logButton = (ImageView) fragmentView.findViewById(R.id.log_button);

        if(onViewsCreatedCallbackListener != null){

            saveButton.setVisibility(View.INVISIBLE);
            logButton.setVisibility(View.INVISIBLE);
            onViewsCreatedCallbackListener.onConditionsFragmentViewsCreated();

        } else if (onSaveClickedCallbackListener != null){

            bindLogAndSaveButtons();
        }

        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        iconManager = new IconManager(context);
        openWeatherLogIntent = new Intent(context, WeatherLogActivity.class);

        if(context instanceof OnSaveButtonClickedListener){

            onSaveClickedCallbackListener = (OnSaveButtonClickedListener) context;
            onViewsCreatedCallbackListener = null;

        } else if(context instanceof OnCreatedViewsListener){

            onSaveClickedCallbackListener = null;
            onViewsCreatedCallbackListener = (OnCreatedViewsListener) context;

        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void update(Weather currentWeather){

        this.currentWeather = currentWeather;

        temperatureText.setText(Long.toString(Math.round(currentWeather.temperature())) + "°C");
        pressureText.setText(Long.toString(Math.round(currentWeather.pressure())) + " hPa");
        windSpeedText.setText(Long.toString(Math.round(currentWeather.windSpeed())) + " km/h");
        cloudsText.setText(Long.toString(Math.round(currentWeather.clouds())) + " %");
        weatherIcon.setImageBitmap(iconManager.decodeIconText(currentWeather.conditions()));
        dateAndLocation.setText(currentWeather.dateAndLocation());

        if(currentWeather.temperature() >= 10) temperatureText.setTranslationX(-15);

    }

    private void bindLogAndSaveButtons(){

        logButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch(motionEvent.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        logButton.setImageResource(R.drawable.log_clicked);
                        return true;

                    case MotionEvent.ACTION_UP:
                        logButton.setImageResource(R.drawable.log);
                        startActivity(openWeatherLogIntent);
                        return true;
                }

                return false;
            }
        });

        saveButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch(motionEvent.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        saveButton.setImageResource(R.drawable.save_clicked);
                        return true;

                    case MotionEvent.ACTION_UP:
                        saveButton.setImageResource(R.drawable.save);
                        onSaveClickedCallbackListener.onSaveButtonClicked(currentWeather);
                        return true;
                }

                return false;
            }
        });
    }

    public interface OnSaveButtonClickedListener {
        void onSaveButtonClicked(Weather currentWeather);
    }

    public interface OnCreatedViewsListener {
        void onConditionsFragmentViewsCreated();
    }

}
