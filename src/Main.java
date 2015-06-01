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

            DataModel dm = new DataModel(file);

            Packing packing = new Packing(dm.getRectangles(), dm.getBin()); // Pack

            Solution solution = packing.pack();
            solution.setApplication(dm.getRectangles());

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
