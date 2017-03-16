package k.weathlog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by konra on 02.03.2017.
 */
public class IconManager {


    private Context mainActivity;

    public IconManager(Context mainActivity){
        this.mainActivity = mainActivity;
    }

    public Bitmap decodeIconText(String iconText){

        for(WeatherIcon icon: WeatherIcon.values()){

            if(icon.textMatches(iconText)) return BitmapFactory.decodeResource(mainActivity.getResources(), icon.resourceId);
        }


        WeatherIcon defaultIcon = WeatherIcon.CLOUDY;

        return BitmapFactory.decodeResource(mainActivity.getResources(), defaultIcon.resourceId);
    }


    enum WeatherIcon {

        SUNNY(R.drawable.sunny, SUNNY_TEXTS),
        SUNNY_CLOUDY(R.drawable.sunny_cloudy, SUNNY_CLOUDY_TEXTS),
        CLOUDY(R.drawable.cloudy,CLOUDY_TEXTS),
        RAINY(R.drawable.rainy, RAINY_TEXTS),
        SNOWY(R.drawable.snowy, SNOWY_TEXTS),
        MOONY(R.drawable.moony, MOONY_TEXTS);

        private int resourceId;
        private String[] iconTexts;

        WeatherIcon(int resourceId, String[] iconTexts){

            this.resourceId = resourceId;
            this.iconTexts = iconTexts;
        }

        public boolean textMatches(String text){

            for(int i = 0; i < iconTexts.length; i++){

                if(text.equals(iconTexts[i])) return true;
            }

            return false;
        }

    }

    private static final String[] SUNNY_TEXTS = {"Sunny", "Mostly sunny", "Partly sunny"};
    private static final String[] SUNNY_CLOUDY_TEXTS = {"Intermittent clouds", "Hazy sunshine"};
    private static final String[] CLOUDY_TEXTS = {"Mostly cloudy", "Cloudy", "Dreary (overcast)", "Fog"};
    private static final String[] SNOWY_TEXTS = {"Snow", "Mostly cloudy w/ snow"};
    private static final String[] MOONY_TEXTS = {"Clear", "Mostly clear", "Partly cloudy"};
    private static final String[] RAINY_TEXTS = {"Showers", "Mostly cloudy w/ showers", "Partly sunny w/ showers", "T-storms",
                                                   "Mostly cloudy w/ T-storms", "Partly sunny w/ T-Storms", "Rain", "Sleet",
                                                   "Rain and snow", "Partly cloudy w/ showers", "Mostly cloudy w/ showers",
                                                   "Partly cloudy w/ T-Storms", "Mostly cloudy w/ T-Storms"};
}

