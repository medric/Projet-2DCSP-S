import core.Simplex;
import core.Solution;
import models.Rectangle;
import utils.DataModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

import core.Packing;

/**
 * Created by Medric on 18/04/2015.
 */
public class Main {
    public static void main(String[] args) {
        File file = new File("./data/data_50Valpha.txt");

        try {
            DataModel dm = new DataModel(file);

            Packing packing = new Packing(dm.getRectangles(), dm.getBin()); // Pack

            packing.pack();

            Solution init = new Solution(packing.getBins(), dm.getRectangles());

            Simplex simplex = new Simplex(init);

            simplex.solve();
        } catch (Exception e) {
           // TODO identify exceptions
            e.printStackTrace();
        }
    }
}
