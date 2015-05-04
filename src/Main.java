import utils.DataModel;

import java.io.File;
import java.io.IOException;

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

            int nb= 0;

            for(int i = 0; i < packing.bins.size(); i++) {
                nb += packing.bins.get(i).getRectangles().size();
            }

            System.out.print(nb);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
