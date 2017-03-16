package k.weathlog;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class FiveDayForecastElement extends Fragment {

    private static final String[] daysOfTheWeek = {"MON", "TUE", "WEN", "THU", "FRI", "SAT", "SUN"};

    private double temperature;
    private Date date;
    private String conditions;
    private Bitmap iconBitmap;
    private IconManager iconManager;

    private TextView temperatureText;
    private TextView dateText;
    private ImageView iconImage;
    private TextView conditionsText;

    public FiveDayForecastElement() {}

    public static FiveDayForecastElement newInstance(Weather weather) {

        FiveDayForecastElement fragment = new FiveDayForecastElement();
        Bundle args = new Bundle();

        args.putDouble("temperature", weather.temperature());
        args.putString("icon_text", weather.conditions());
        args.putLong("date", weather.epochTime());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            temperature = getArguments().getDouble("temperature");
            conditions = getArguments().getString("icon_text");
            date = new Date(getArguments().getLong("date"));
        }

        iconManager = new IconManager(getParentFragment().getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View element = inflater.inflate(R.layout.element_five_day_forecast, container, false);

        temperatureText = (TextView) element.findViewById(R.id.temperature);
        conditionsText = (TextView) element.findViewById(R.id.conditions);
        dateText = (TextView) element.findViewById(R.id.date);
        iconImage = (ImageView) element.findViewById(R.id.five_day_forecast_element_icon);
        iconBitmap = iconManager.decodeIconText(conditions);

        updateViews();

        return element;
    }


    private void updateViews(){

        temperatureText.setText((Integer.toString((int)Math.round(temperature))));
        conditionsText.setText(conditions);

        GregorianCalendar cal =(GregorianCalendar) GregorianCalendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        dateText.setText(daysOfTheWeek[cal.get(Calendar.DAY_OF_WEEK)-1]);

        iconImage.setImageBitmap(iconBitmap);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
