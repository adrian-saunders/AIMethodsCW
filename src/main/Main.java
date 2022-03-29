package main;

import ann.Network;
import data.Data;
import data.DataPoint;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        final int numberOfEpochs = 5000;

        Data data = new Data("rawData.csv");
        // Read, clean and standardise data from file
        data.ingestData();

        Network ann = new Network(5, 3, 4, 1);
        for (int i = 0; i < numberOfEpochs; i++) {
            // For each clean data point complete a full pass
            for (DataPoint dataPoint : data.cleanData) {
                ArrayList<Double> inputs = new ArrayList<>();
                ArrayList<Double> outputs = new ArrayList<>();
                inputs.add(dataPoint.temperature);
                inputs.add(dataPoint.windSpeed);
                inputs.add(dataPoint.solarRadiation);
                inputs.add(dataPoint.airPressure);
                inputs.add(dataPoint.humidity);
                outputs.add(dataPoint.panEvaporation);

                ann.fullCycle(inputs, outputs);
            }
        }

        System.out.println("Training complete!");

        // Pass the validation data through the network and get its expected output and the models actual output.
        for (int i = 0; i < data.validationData.size(); i++) {
            ArrayList<Double> inputs = new ArrayList<>();
            inputs.add(data.validationData.get(i).temperature);
            inputs.add(data.validationData.get(i).windSpeed);
            inputs.add(data.validationData.get(i).solarRadiation);
            inputs.add(data.validationData.get(i).airPressure);
            inputs.add(data.validationData.get(i).humidity);

            double passOutput = ann.forwardPass(inputs).get(0);
            System.out.println(data.testData.get(i).date + ", " + data.destandardisePanE(data.validationData.get(i).panEvaporation) + ", " + data.destandardisePanE(passOutput));
        }
    }
}
