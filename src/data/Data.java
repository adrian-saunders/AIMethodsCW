package data;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Data {
    private ArrayList<String> rawData = new ArrayList<>();
    private final ArrayList<DataPoint> dataPoints = new ArrayList<>();
    public ArrayList<DataPoint> cleanData = new ArrayList<>();
    public final ArrayList<DataPoint> trainingData = new ArrayList<>();
    public final ArrayList<DataPoint> testData = new ArrayList<>();
    public final ArrayList<DataPoint> validationData = new ArrayList<>();

    private double maxPanE;
    private double minPanE;

    public Data(String filePath)  {
        try {
            rawData = FileReader.readFile(filePath);
        } catch (FileNotFoundException e) {
            System.out.println("the file was not found");
        }
    }

    private void getDataPoints() {
        for (String rawDataRow : rawData) {
            try {
                dataPoints.add(parseToDataPoint((rawDataRow)));
            } catch (ParseException | NumberFormatException e) {
                // Ignore any errors from data being in the wrong format etc
            }
        }
    }

    private DataPoint parseToDataPoint(String dataRow) throws ParseException, NumberFormatException {
        ArrayList<String> values = new ArrayList<>();
        try (Scanner rowScanner = new Scanner(dataRow)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                String nextValue = rowScanner.next();
                if (nextValue.length() != 0) {
                    values.add(nextValue);
                }
            }
        }

        // Don't create data points that have missing values
        if (values.size() == 7) {
            // Convert strings to appropriate format and add create new DataPoint
            ArrayList<Double> parsedValues = new ArrayList<>();
            parsedValues.add(Double.parseDouble(values.get(1)));
            parsedValues.add(Double.parseDouble(values.get(2)));
            parsedValues.add(Double.parseDouble(values.get(3)));
            parsedValues.add(Double.parseDouble(values.get(4)));
            parsedValues.add(Double.parseDouble(values.get(5)));
            parsedValues.add(Double.parseDouble(values.get(6)));

            return new DataPoint(values.get(0), parsedValues.get(0), parsedValues.get(1), parsedValues.get(2), parsedValues.get(3), parsedValues.get(4), parsedValues.get(5));
        } else {
            throw new ParseException("There was 1 or more missing values", 0);
        }
    }

    private void cleanData() {
        ArrayList<DataPoint> dataPointsToRemove = new ArrayList<>();
        for (DataPoint dataPoint : dataPoints) {
            // Remove extreme temperature values
            if (dataPoint.temperature > 50 || dataPoint.temperature < -10.0) {
                dataPointsToRemove.add(dataPoint);
            }
            // Remove extreme wind speeds
            if (dataPoint.windSpeed > 10 || dataPoint.windSpeed < 0) {
                dataPointsToRemove.add(dataPoint);
            }
            // Remove solar radiation below 0
            if (dataPoint.solarRadiation > 850 || dataPoint.solarRadiation < 0) {
                dataPointsToRemove.add(dataPoint);
            }
            // Remove extreme air pressure values
            if (dataPoint.airPressure > 105 || dataPoint.airPressure < 95) {
                dataPointsToRemove.add(dataPoint);
            }
            // Remove extreme humidity
            if (dataPoint.humidity > 110 || dataPoint.humidity < 15) {
                dataPointsToRemove.add(dataPoint);
            }
        }
        this.cleanData = (ArrayList<DataPoint>) dataPoints.clone();
        this.cleanData.removeAll(dataPointsToRemove);
    }

    private void standardiseData() {
        double maxTemperature = Collections.max(cleanData, Comparator.comparing(s -> s.temperature)).temperature;
        double minTemperature = Collections.min(cleanData, Comparator.comparing(s -> s.temperature)).temperature;
        double maxWindSpeed = Collections.max(cleanData, Comparator.comparing(s -> s.windSpeed)).windSpeed;
        double minWindSpeed = Collections.min(cleanData, Comparator.comparing(s -> s.windSpeed)).windSpeed;
        double maxSolarRadiation = Collections.max(cleanData, Comparator.comparing(s -> s.solarRadiation)).solarRadiation;
        double minSolarRadiation = Collections.min(cleanData, Comparator.comparing(s -> s.solarRadiation)).solarRadiation;
        double maxAirPressure = Collections.max(cleanData, Comparator.comparing(s -> s.airPressure)).airPressure;
        double minAirPressure = Collections.min(cleanData, Comparator.comparing(s -> s.airPressure)).airPressure;
        double maxHumidity = Collections.max(cleanData, Comparator.comparing(s -> s.humidity)).humidity;
        double minHumidity = Collections.min(cleanData, Comparator.comparing(s -> s.humidity)).humidity;
        this.maxPanE = Collections.max(cleanData, Comparator.comparing(s -> s.panEvaporation)).panEvaporation;
        this.minPanE = Collections.min(cleanData, Comparator.comparing(s -> s.panEvaporation)).panEvaporation;

        for (DataPoint dataPoint : cleanData) {
            dataPoint.temperature = 0.8 * (dataPoint.temperature - minTemperature) / (maxTemperature - minTemperature) + 0.1;
            dataPoint.windSpeed = 0.8 * (dataPoint.windSpeed - minWindSpeed) / (maxWindSpeed - minWindSpeed) + 0.1;
            dataPoint.solarRadiation = 0.8 * (dataPoint.solarRadiation - minSolarRadiation) / (maxSolarRadiation - minSolarRadiation) + 0.1;
            dataPoint.airPressure = 0.8 * (dataPoint.airPressure - minAirPressure) / (maxAirPressure - minAirPressure) + 0.1;
            dataPoint.humidity = 0.8 * (dataPoint.humidity - minHumidity) / (maxHumidity - minHumidity) + 0.1;
            dataPoint.panEvaporation = 0.8 * (dataPoint.panEvaporation - minPanE) / (maxPanE - minPanE) + 0.1;
        }
    }

    private void getTrainingData() {
        for (int i = 0; i < this.cleanData.size(); i++) {
            if (i % 5 == 0 || i % 5 == 1 || i % 5 == 2) {
                this.trainingData.add(this.cleanData.get(i));
            }
        }
    }

    private void getTestData() {
        for (int i = 0; i < this.cleanData.size(); i++) {
            if (i % 5 == 3) {
                this.testData.add(this.cleanData.get(i));
            }
        }
    }

    private void getValidationData() {
        for (int i = 0; i < this.cleanData.size(); i++) {
            if (i % 5 == 4) {
                this.validationData.add(this.cleanData.get(i));
            }
        }
    }

    public double destandardisePanE(double value) {
        return ((value - 0.1) / 0.8) * (this.maxPanE - this.minPanE) + this.minPanE;
    }

    public void ingestData() {
        this.getDataPoints();
        this.cleanData();
//        for (DataPoint dataPoint : cleanData) {
//            System.out.println(dataPoint.date + ", " +
//                    dataPoint.temperature + ", " +
//                    dataPoint.windSpeed + ", " +
//                    dataPoint.solarRadiation + ", " +
//                    dataPoint.airPressure + ", " +
//                    dataPoint.humidity + ", " +
//                    dataPoint.panEvaporation);
//        }
        this.standardiseData();
        this.getTrainingData();
        this.getTestData();
        this.getValidationData();
    }
}
