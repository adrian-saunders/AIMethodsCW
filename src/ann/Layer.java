package ann;

import java.util.ArrayList;

public class Layer {
    final public ArrayList<Neuron> neurons = new ArrayList<>();

    Layer(int numberOfNeurones, int numberOfInputs) {
        for (int i = 0; i < numberOfNeurones; i++) {
            neurons.add(new Neuron(numberOfInputs));
        }
    }
}
