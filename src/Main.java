import core.Optimization;
import models.Solution;
import utils.DataModel;

import java.io.File;
import java.io.IOException;

import core.Packing;

/**
 * Created by Medric on 18/04/2015.
 */
public class Main {
    public static void main(String[] args) {
        File file = new File("./data/data_20Lalpha.txt");

        try {
            DataModel dm = new DataModel(file);

            Packing packing = new Packing(dm.getRectangles(), dm.getBin()); // Pack

            Solution solution = packing.pack();

            Optimization opti = new Optimization(50, 6, solution);
            opti.optimize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
