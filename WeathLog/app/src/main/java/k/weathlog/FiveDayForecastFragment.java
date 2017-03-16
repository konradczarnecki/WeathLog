package k.weathlog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FiveDayForecastFragment extends Fragment {

    private FiveDayForecastElement[] elements;
    private Weather[] lastForecast;

    public FiveDayForecastFragment() {}

    public static FiveDayForecastFragment newInstance() {

        FiveDayForecastFragment fragment = new FiveDayForecastFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        elements = new FiveDayForecastElement[5];

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_five_day_forecast, container, false);

        if(lastForecast != null) update(lastForecast);

        return view;
    }

    public void update(Weather[] forecast){

        lastForecast = forecast;

        for(int i = 0; i < 5; i++){

            elements[i] = FiveDayForecastElement.newInstance(forecast[i]);
        }

        FragmentManager fm = this.getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        transaction.replace(R.id.day_1, elements[0]);
        transaction.replace(R.id.day_2, elements[1]);
        transaction.replace(R.id.day_3, elements[2]);
        transaction.replace(R.id.day_4, elements[3]);
        transaction.replace(R.id.day_5, elements[4]);

        transaction.commit();
    }

}
