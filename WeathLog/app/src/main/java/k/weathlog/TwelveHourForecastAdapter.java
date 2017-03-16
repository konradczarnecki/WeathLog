package k.weathlog;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by konra on 03.03.2017.
 */
public class TwelveHourForecastAdapter extends RecyclerView.Adapter<TwelveHourForecastAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView temperatureText;
        private TextView timeText;
        private ImageView weatherIcon;

        private ViewHolder(View itemView){

            super(itemView);

            temperatureText = (TextView) itemView.findViewById(R.id.temperature);
            timeText = (TextView) itemView.findViewById(R.id.time);
            weatherIcon = (ImageView) itemView.findViewById(R.id.weather_icon);
        }
    }

    private Weather[] forecast;
    private IconManager iconManager;


    public TwelveHourForecastAdapter(Weather[] forecast, Context mainActivity){

        this.forecast = forecast;
        iconManager = new IconManager(mainActivity);
    }

    public TwelveHourForecastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View forecastElement = inflater.inflate(R.layout.element_twelve_hour_forecast, parent, false);

        ViewHolder viewHolder = new ViewHolder(forecastElement);

        return viewHolder;
    }

    public void onBindViewHolder(TwelveHourForecastAdapter.ViewHolder viewHolder, int position){

        Weather weather = forecast[position];

        viewHolder.temperatureText.setText(Integer.toString((int) Math.round(weather.temperature())) + "°C");

        Bitmap iconImage = iconManager.decodeIconText(weather.conditions());
        viewHolder.weatherIcon.setImageBitmap(iconImage);

        GregorianCalendar cal = new GregorianCalendar();
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);

        int hourForElement = currentHour + position + 1;

        if(hourForElement >= 24) hourForElement -= 24;

        String time = hourForElement + ":00";

        viewHolder.timeText.setText(time);
    }

    public int getItemCount(){
        return forecast.length;
    }


}
