import core.Optimization;
import core.Simplex;
import core.Solution;
import utils.DataModel;

import java.io.File;

import core.Packing;

/**
 * Created by Medric on 18/04/2015.
 */
public class Main {
    public static void main(String[] args) {

        try {
            File file = new File("./data/data_20Salpha.txt");
            int numberOfGeneration = 50;
            int populationSize = 6;

            DataModel dm = new DataModel(file);

            Packing packing = new Packing(dm.getRectangles(), dm.getBin()); // Pack

            Solution solution = packing.pack();
            solution.setApplication(dm.getRectangles());

            Optimization optimization = new Optimization(numberOfGeneration, populationSize, solution);

            Solution bestSolution = optimization.optimize();

            System.out.println(String.format("fitness of best solution is %0$s", bestSolution.getFitness()));

        } catch (Exception e) {
           // TODO identify exceptions
            e.printStackTrace();
        }
    }
}
