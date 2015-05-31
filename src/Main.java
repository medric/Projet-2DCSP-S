import core.Optimization;
import core.Simplex;
import core.Solution;
import utils.DataModel;

import java.io.File;
import java.io.IOException;

import core.Packing;

/**
 * Created by Medric on 18/04/2015.
 */
public class Main {
    public static void main(String[] args) {
        File file = new File("./data/data_20Salpha.txt");

        try {
            DataModel dm = new DataModel(file);

            Packing packing = new Packing(dm.getRectangles(), dm.getBin()); // Pack

            Solution solution = packing.pack();

            // First, resolve simplex for the initial solution
            Simplex simplex = new Simplex(solution);

            simplex.solve();

            Optimization optimization = new Optimization(50, 6, solution);

            optimization.optimize();

        } catch (Exception e) {
           // TODO identify exceptions
            e.printStackTrace();
        }
    }
}
