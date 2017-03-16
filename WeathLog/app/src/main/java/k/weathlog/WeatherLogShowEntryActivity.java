package k.weathlog;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import org.apache.commons.lang3.SerializationUtils;

public class WeatherLogShowEntryActivity extends AppCompatActivity implements CurrentConditionsFragment.OnCreatedViewsListener {

    Weather weather;
    CurrentConditionsFragment conditionsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_log_show_entry);


        int entryID = getIntent().getIntExtra("id",0);

        DBHandler handler = new DBHandler(this);

        Cursor cursor = handler.getEntry(entryID);

        cursor.moveToFirst();

        byte[] serializedWeather = cursor.getBlob(cursor.getColumnIndexOrThrow(DBHandler.KEY_WEATHER));
        weather = SerializationUtils.deserialize(serializedWeather);

        String comment = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.KEY_COMMENT));


        conditionsFragment = CurrentConditionsFragment.newInstance();

        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.entry_conditions_frame, conditionsFragment);
        transaction.commit();

        TextView commentText = (TextView) findViewById(R.id.entry_comment);
        commentText.setText(comment);
    }

    @Override
    public void onConditionsFragmentViewsCreated() {
        conditionsFragment.update(weather);
    }

}
