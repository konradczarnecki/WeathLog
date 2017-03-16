package k.weathlog;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.apache.commons.lang3.SerializationUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by konra on 05.03.2017.
 */
public class WeatherLogCursorAdapter extends CursorAdapter {


    public WeatherLogCursorAdapter(Context context, Cursor cursor){
        super(context,cursor,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.element_weather_log,viewGroup,false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView dateText = (TextView) view.findViewById(R.id.date);
        ImageView weatherIcon = (ImageView) view.findViewById(R.id.weather_icon);

        byte[] serializedWeather = cursor.getBlob(cursor.getColumnIndexOrThrow(DBHandler.KEY_WEATHER));
        Weather weather = SerializationUtils.deserialize(serializedWeather);

        IconManager manager = new IconManager(context);
        Bitmap weatherIconBitmap = manager.decodeIconText(weather.conditions());
        weatherIcon.setImageBitmap(weatherIconBitmap);

        final int entryId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));

        view.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                Intent showItem = new Intent(context, WeatherLogShowEntryActivity.class);
                showItem.putExtra("id", entryId);
                context.startActivity(showItem);
            }
        });

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(weather.epochTime());

        String month;
        if(cal.get(Calendar.MONTH)+1 >= 10) month = Integer.toString(cal.get(Calendar.MONTH)+1);
        else month = 0 + Integer.toString(cal.get(Calendar.MONTH)+1);

        String date = cal.get(Calendar.DAY_OF_MONTH) + "." + month + "." + cal.get(Calendar.YEAR);

        dateText.setText(date);
    }
}
