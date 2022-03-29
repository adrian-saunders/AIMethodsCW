package data;

public class DataPoint {
    public final String date;
    public double temperature;
    public double windSpeed;
    public double solarRadiation;
    public double airPressure;
    public double humidity;
    public double panEvaporation;


    public DataPoint(String date, double temperature, double windSpeed, double solarRadiation, double airPressure, double humidity, double panEvaporation) {
        this.date = date;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.solarRadiation = solarRadiation;
        this.airPressure = airPressure;
        this.humidity = humidity;
        this.panEvaporation = panEvaporation;
    }
}
