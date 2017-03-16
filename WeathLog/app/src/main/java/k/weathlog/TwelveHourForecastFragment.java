package k.weathlog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TwelveHourForecastFragment extends Fragment {

    private RecyclerView forecastRV;
    private Weather[] lastForecast = null;

    public TwelveHourForecastFragment() {}


    public static TwelveHourForecastFragment newInstance() {

        TwelveHourForecastFragment fragment = new TwelveHourForecastFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void update(Weather[] forecast){

        TwelveHourForecastAdapter adapter = new TwelveHourForecastAdapter(forecast, getActivity());
        lastForecast = forecast;

        forecastRV.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_twelve_hour_forecast, container, false);

        forecastRV = (RecyclerView) view.findViewById(R.id.twelve_hour_forecast_rv);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        forecastRV.setLayoutManager(manager);

        if(lastForecast != null){
            TwelveHourForecastAdapter adapter = new TwelveHourForecastAdapter(lastForecast, getActivity());
            forecastRV.setAdapter(adapter);
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {super.onAttach(context);}

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
