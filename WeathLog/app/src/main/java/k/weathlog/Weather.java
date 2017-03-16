package k.weathlog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Weather implements java.io.Serializable {

    private double temperature;
    private String conditions;
    private double windSpeed;
    private double pressure;
    private double clouds;
    private Date date;
    private String location;

    private Weather(WeatherBuilder builder){

        this.temperature = builder.temperature;
        this.conditions = builder.conditions;
        this.windSpeed = builder.windSpeed;
        this.pressure = builder.pressure;
        this.clouds = builder.clouds;
        this.location = builder.location;
        this.date = builder.date;
    }


    public double temperature(){
        return temperature;
    }

    public String conditions(){
        return conditions;
    }

    public double windSpeed(){ return windSpeed; }

    public double pressure(){ return pressure;}

    public double clouds(){ return clouds;}

    public long epochTime(){
        return date.getTime();
    }

    public String dateAndLocation(){

        StringBuilder builder = new StringBuilder();

        builder.append(location);
        builder.append(", ");

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy");
        String dateString = format.format(date);
        builder.append(dateString);

        return builder.toString();
    }

    public static class WeatherBuilder {

        private double temperature;
        private String conditions;
        private double windSpeed;
        private double pressure;
        private double clouds;
        private Date date;
        private String location;

        public WeatherBuilder(long epochDate){

            date = new Date(epochDate*1000);
        }

        public WeatherBuilder setTemperature(double temperature){
            this.temperature = temperature;
            return this;
        }

        public WeatherBuilder setConditions(String conditions){
            this.conditions = conditions;
            return this;
        }

        public WeatherBuilder setWindSpeed(double windSpeed){
            this.windSpeed = windSpeed;
            return this;
        }

        public WeatherBuilder setPressure(double pressure){
            this.pressure = pressure;
            return this;
        }

        public WeatherBuilder setClouds(double clouds){
            this.clouds = clouds;
            return this;
        }

        public WeatherBuilder setLocation(String location){
            this.location = location;
            return this;
        }

        public Weather build(){
            return new Weather(this);
        }
    }

}