package k.weathlog;

import android.os.AsyncTask;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by konra on 12.02.2017.
 */
public class WeatherUpdater {

    private static final String accuApiKey = "gQpqPKjVDZkH6DizQzzCnPk68CdG3HH0";

    private WeatherUpdateListener mainCallbackListener;
    private String currentLocation;


    public WeatherUpdater(WeatherUpdateListener callbackListener){

        this.mainCallbackListener = callbackListener;
    }

    public void updateAll(double latitude, double longitude){

        AccuApiRequest locationRequest = new AccuApiRequest(new LocationUpdate(this));

        String locationApiRequestUrl = "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey=" +
                accuApiKey + "&q=" + latitude + "%2C" + longitude;

        locationRequest.execute(locationApiRequestUrl);
    }

    public void onLocationUpdate(InputStream stream){

        final WeatherUpdater requestCallbackListener = this;

        new AsyncTask<InputStream, Void, String>(){

            @Override
            protected String doInBackground(InputStream... inputStreams){

                JsonReader reader = Json.createReader(inputStreams[0]);
                JsonObject locationJson = reader.readObject();

                String locationKey = locationJson.getString("Key");
                currentLocation = locationJson.getString("EnglishName");

                return locationKey;
            }

            @Override
            protected void onPostExecute(String locationKey) {

                String currentConditionsRequestUrl = "http://dataservice.accuweather.com/currentconditions/v1/" + locationKey +
                        "?apikey=" + accuApiKey + "&details=true";

                String fiveDayForecastRequestUrl = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/" + locationKey +
                        "?apikey=" + accuApiKey + "&details=true&metric=true";

                String twelveHourForecastRequestUrl = "http://dataservice.accuweather.com/forecasts/v1/hourly/12hour/" + locationKey +
                        "?apikey=" + accuApiKey + "&metric=true";


                AccuApiRequest currentConditionsRequest = new AccuApiRequest(new ConditionsUpdate(requestCallbackListener));

                AccuApiRequest fiveDayForecastRequest = new AccuApiRequest(new FiveDayForecastUpdate(requestCallbackListener));

                AccuApiRequest twelveHourForecastRequest = new AccuApiRequest(new TwelveDayForecastUpdate(requestCallbackListener));

                currentConditionsRequest.execute(currentConditionsRequestUrl);
                fiveDayForecastRequest.execute(fiveDayForecastRequestUrl);
                twelveHourForecastRequest.execute(twelveHourForecastRequestUrl);
            }
        }.execute(stream);

    }

    public void onConditionsUpdate(InputStream jsonStream){

        new AsyncTask<InputStream, Void, Weather>(){

            @Override
            protected Weather doInBackground(InputStream... inputStreams) {

                JsonReader reader = Json.createReader(inputStreams[0]);
                JsonArray weatherJson = reader.readArray();

                JsonObject obj = weatherJson.getJsonObject(0);
                String conditions = obj.getString("WeatherText");
                double temperature = obj.getJsonObject("Temperature").getJsonObject("Metric").getInt("Value");
                double pressure = obj.getJsonObject("Pressure").getJsonObject("Metric").getInt("Value");
                double windSpeed = obj.getJsonObject("Wind").getJsonObject("Speed").getJsonObject("Metric").getInt("Value");
                double cloudCoverage = obj.getInt("CloudCover");
                long epochDate = obj.getInt("EpochTime");

                Weather.WeatherBuilder builder = new Weather.WeatherBuilder(epochDate);

                builder.setTemperature(temperature)
                        .setPressure(pressure)
                        .setWindSpeed(windSpeed)
                        .setClouds(cloudCoverage)
                        .setConditions(conditions)
                        .setLocation(currentLocation);

                Weather currentConditions = builder.build();

                return currentConditions;
            }

            public void onPostExecute(Weather currentConditions){

                mainCallbackListener.onCurrentConditionsUpdated(currentConditions);
            }

        }.execute(jsonStream);

    }

    public void onFiveDayForecastUpdate(InputStream jsonStream){

        new AsyncTask<InputStream, Void, Weather[]>(){

            @Override
            protected Weather[] doInBackground(InputStream... inputStreams) {

                Weather[] forecast = new Weather[5];

                JsonReader reader = Json.createReader(inputStreams[0]);
                JsonObject obj = reader.readObject();
                JsonArray fiveDayForecastJson = obj.getJsonArray("DailyForecasts");

                for(int i = 0; i < 5; i++){

                    JsonObject day = fiveDayForecastJson.getJsonObject(i);

                    long epochDate = day.getJsonNumber("EpochDate").longValue();

                    JsonObject temperatureJson = day.getJsonObject("Temperature");
                    double minTemperature = temperatureJson.getJsonObject("Minimum").getJsonNumber("Value").doubleValue();
                    double maxTemperature = temperatureJson.getJsonObject("Maximum").getJsonNumber("Value").doubleValue();

                    JsonObject daytimeForecast = day.getJsonObject("Day");
                    String conditions = daytimeForecast.getString("IconPhrase");

                    Weather.WeatherBuilder builder = new Weather.WeatherBuilder(epochDate);

                    builder.setTemperature((2*maxTemperature + minTemperature)/3)
                            .setConditions(conditions);

                    forecast[i] = builder.build();
                }

                return forecast;
            }

            public void onPostExecute(Weather[] forecast){

                mainCallbackListener.onFiveDayForecastUpdated(forecast);
            }

        }.execute(jsonStream);
    }

    public void onTwelveHourForecastUpdate(InputStream jsonStream){

        new AsyncTask <InputStream, Void, Weather[]> (){

            @Override
            protected Weather[] doInBackground(InputStream... inputStreams) {

                Weather[] forecast = new Weather[12];

                JsonReader reader = Json.createReader(inputStreams[0]);
                JsonArray twelveHourForecastJson = reader.readArray();

                for(int i = 0; i < 12; i++){

                    JsonObject hourlyForecast = twelveHourForecastJson.getJsonObject(i);

                    long epochDate = hourlyForecast.getJsonNumber("EpochDateTime").longValue();
                    String conditions = hourlyForecast.getString("IconPhrase");
                    double temperature = hourlyForecast.getJsonObject("Temperature").getJsonNumber("Value").doubleValue();

                    Weather.WeatherBuilder builder = new Weather.WeatherBuilder(epochDate);

                    forecast[i] = builder.setConditions(conditions)
                            .setTemperature(temperature)
                            .build();
                }

                return  forecast;
            }

            @Override
            protected void onPostExecute(Weather[] forecast) {

                mainCallbackListener.onTwelveHourForecastUpdated(forecast);
            }

        }.execute(jsonStream);

    }

    public interface WeatherUpdateListener {
        void onCurrentConditionsUpdated(Weather currentWeather);
        void onFiveDayForecastUpdated(Weather[] forecast);
        void onTwelveHourForecastUpdated(Weather[] forecast);
    }

    private abstract class UpdateTarget {

        protected WeatherUpdater callbackListener;

        public UpdateTarget(WeatherUpdater callbackListener){
            this.callbackListener = callbackListener;
        }
        public abstract void onUpdate(InputStream stream);
    }

    private class LocationUpdate extends UpdateTarget {

        public LocationUpdate(WeatherUpdater callbackListener){
            super(callbackListener);
        }
        public void onUpdate(InputStream stream){
            callbackListener.onLocationUpdate(stream);
        }
    }

    private class ConditionsUpdate extends UpdateTarget {

        public ConditionsUpdate(WeatherUpdater callbackListener){
            super(callbackListener);
        }
        public void onUpdate(InputStream stream){
            callbackListener.onConditionsUpdate(stream);
        }
    }

    private class FiveDayForecastUpdate extends UpdateTarget {

        public FiveDayForecastUpdate(WeatherUpdater callbackListener){super(callbackListener);}
        public void onUpdate(InputStream stream) {callbackListener.onFiveDayForecastUpdate(stream);}
    }

    private class TwelveDayForecastUpdate extends UpdateTarget {

        public TwelveDayForecastUpdate(WeatherUpdater callbackListener){super(callbackListener);}
        public void onUpdate(InputStream stream) {
            callbackListener.onTwelveHourForecastUpdate(stream);}
    }

    private class AccuApiRequest extends AsyncTask<String, Void, InputStream> {

        UpdateTarget target;

        public AccuApiRequest(UpdateTarget target){
            this.target = target;
        }

        public InputStream doInBackground(String... params) {

            String urlString = params[0];
            URL url;
            HttpURLConnection connection;
            InputStream stream = null;

            try {
                url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) stream = connection.getInputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return stream;
        }

        public void onPostExecute(InputStream stream) {

            target.onUpdate(stream);
        }

    }

}
