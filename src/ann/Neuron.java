package ann;

import java.util.ArrayList;
import java.util.Random;

public class  Neuron {
    public final ArrayList<Input> inputs = new ArrayList<>();
    public double bias;
    private double activation;
    private double delta;

    Neuron(int numberOfInputs) throws IllegalArgumentException {
        if (numberOfInputs >= 1) {
            // Initialise the bias
            Random randomNumberGenerator = new Random();
            this.bias = randomNumberGenerator.nextDouble();

            // Add n inputs to the new neuron and initialise their weights
            for (int i = 0; i < numberOfInputs; i++) {
                this.inputs.add(new Input(0, randomNumberGenerator.nextDouble()));
            }

            this.setActivation();
        } else {
            throw new IllegalArgumentException("Each neurone must have at least 1 input");
        }
    }

    private double getSum() {
        // Get the weighted sum of the neuron
        double sum = bias;
        for (Input currentInput : inputs) {
            sum += currentInput.value * currentInput.weight;
        }
        return sum;
    }

    public double getActivation() {
        return this.activation;
    }

    private void setActivation() {
        // Calculate the sigmoid function for the weighted sum
        this.activation = 1 / (1 + Math.exp(-getSum()));
    }

    public double getActivationGradient() {
        return getActivation() * (1 - getActivation());
    }

    public void setDelta(double error) {
        this.delta = error * getActivationGradient();
    }

    public double getDelta() {
        return this.delta;
    }

    public void updateInputs(ArrayList<Double> inputs) {
        // Takes a list of new inputs and updates the value for each input
        for (int i = 0; i < inputs.size(); i++) {
            this.inputs.get(i).setInputValue(inputs.get(i));
        }
    }

    public class Input {
        private double value;
        private double weight;

        public Input(double inputValue, double inputWeight) {
            value = inputValue;
            weight = inputWeight;
            Neuron.this.setActivation();
        }

        public double getInputValue() {
            return this.value;
        }

        public void setInputValue(double value) {
            this.value = value;
            Neuron.this.setActivation();
        }

        public double getInputWeight() {
            return this.weight;
        }

        public void setInputWeight(double newWeight) {
            this.weight = newWeight;
            Neuron.this.setActivation();
        }
    }
}