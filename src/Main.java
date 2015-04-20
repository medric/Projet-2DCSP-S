import utils.DataModel;
import java.io.File;
import java.io.IOException;

/**
 * Created by Medric on 18/04/2015.
 */
public class Main
{
    public static void main(String[] args)
    {
        File file = new File("./data/data_50Valpha.txt");

        try
        {
            DataModel dm = new DataModel(file);
            
            System.out.print("foo");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
