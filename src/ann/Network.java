package ann;

import java.util.ArrayList;

public class Network {
    final public ArrayList<Layer> layers = new ArrayList<>();

    public Network(int numberOfInputs, int numberOfHiddenLayers, int numberOfHiddenNeurons, int numberOfOutputs) {
        // create n hidden layers
        for (int i = 0; i < numberOfHiddenLayers; i++) {
            layers.add(new Layer(numberOfHiddenNeurons, numberOfInputs));
        }
        // add output layer
        layers.add(new Layer(numberOfOutputs, numberOfHiddenNeurons));
    }

    public ArrayList<Double> fullCycle(ArrayList<Double> data, ArrayList<Double> expectedOutputs) {
        // Takes a row of data and then uses it to complete a forward and backward pass and then updates the weights
        ArrayList<Double> forwardPassValues = this.forwardPass(data);
        this.backwardPass(expectedOutputs);
        this.updateWeights();
        return forwardPassValues;
    }

    public ArrayList<Double> forwardPass(ArrayList<Double> data) {
        // Takes a set of inputs and completes a forward pass
        ArrayList<Double> inputs = data;
        ArrayList<Double> nextLayerInputs = new ArrayList<>();
        // Iterate over each layer and get the activation of each neuron
        for (Layer layer : layers) {
            nextLayerInputs.clear();
            for (Neuron neuron : layer.neurons) {
                neuron.updateInputs(inputs);
                nextLayerInputs.add(neuron.getActivation());
            }
            // The activations of each neuron in this layer become the inputs for the next layer
            inputs = (ArrayList<Double>) nextLayerInputs.clone();
        }
        return inputs;
    }

    private void backwardPass(ArrayList<Double> expectedOutputs) {
        for (int i = layers.size() - 1; i >= 0; i--) {
            // Iterate over each layer
            Layer layer = layers.get(i);
            if (i != layers.size() - 1) {
                // If this is not the ouptut layer do:
                for (int j = 0; j < layer.neurons.size(); j++) {
                    // Iterate over each Neuron in layer
                    double error = 0.0;
                    for (int k = 0; k < layers.get(i + 1).neurons.size(); k++) {
                        // Iterate over each neuron in the next layer and get the input that corresponds to the current neuron
                        Neuron nextLayerNeuron = layers.get(i + 1).neurons.get(k);
                        error += nextLayerNeuron.inputs.get(j).getInputWeight() * nextLayerNeuron.getDelta();
                    }
                    layer.neurons.get(j).setDelta(error);
                }
            } else {
                // If this is the output layer do:
                for (int j = 0; j < layer.neurons.size(); j++) {
                    Neuron neuron = layer.neurons.get(j);
                    neuron.setDelta(expectedOutputs.get(j) - neuron.getActivation());
                }
            }
        }
    }

    private void updateWeights() {
        for (Layer layer : layers) {
            for (int j = 0; j < layer.neurons.size(); j++) {
                // Iterate over each neuron in each layer
                Neuron neuron = layer.neurons.get(j);
                double learningRate = 0.1;
                for (int k = 0; k < neuron.inputs.size(); k++) {
                    // Update each input for the neuron with new weights
                    double currentWeight = neuron.inputs.get(k).getInputWeight();
                    double currentInput = neuron.inputs.get(k).getInputValue();
                    double newWeight = currentWeight + learningRate * neuron.getDelta() * currentInput;
                    neuron.inputs.get(k).setInputWeight(newWeight);
                }
                neuron.bias += learningRate * neuron.getDelta();
            }
        }
    }
}
