package k.weathlog;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

public class WeatherLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_log);

        GridView gv = (GridView) findViewById(R.id.weather_log_gv);

        DBHandler handler = new DBHandler(this);
        Cursor cursor = handler.getAllEntries();

        WeatherLogCursorAdapter adapter = new WeatherLogCursorAdapter(this,cursor);
        gv.setAdapter(adapter);
    }
}
