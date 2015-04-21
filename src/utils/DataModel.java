package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Bin;
import models.Dimension;
<<<<<<< HEAD
import models.ItemPattern;
=======
import models.Rectangle;
>>>>>>> f579d628a3e916f4acf25cd93e709102c097f1fd

/**
 * Created by Medric on 18/04/2015.
 */
public class DataModel
{
    // Regex
    private static final Pattern BIN_REGEX = Pattern.compile("LX=(\\d+)\\s*LY=(\\d+)\\s*m=(\\d+)\\s*");
    private static final Pattern RECTANGLE_REGEX = Pattern.compile("(\\d+.\\d+)\\s*(\\d+.\\d+)\\s*(\\d+)");

    private static final Matcher BIN_MATCHER = BIN_REGEX.matcher("");
    private static final Matcher RECTANGLE_MATCHER = RECTANGLE_REGEX.matcher("");

    private static final int BIN_PARAMS_LENGTH = 3;

    private BufferedReader br;
    private FileReader fr;

    private Bin binPattern;
    private ArrayList<ItemPattern> itemsMap;

    /**
     * Constructor
     * @param file
     * @throws FileNotFoundException
     */
    public DataModel(File file) throws IOException
    {
        this.fr = new FileReader(file);
        this.br = new BufferedReader(this.fr);

        this.itemsMap = new ArrayList<ItemPattern>();
        this.fetch();
    }

    /**
     * Fetches data from the given data file
     * @throws IOException
     */
    private void fetch() throws IOException
    {
        this.fetchBinPattern();
        this.fetchRectanglesMap();

        this.br.close();
    }

    /**
     * Reads the lines that allow us to create a new Bin Object
     * If a line is not of the expected pattern, then an
     * IllegalStateException is thrown.
     * @throws IOException
     */
    private void fetchBinPattern() throws IOException
    {
        String sCurrentLine;
        String header = "";

        for(int i = 0; i < BIN_PARAMS_LENGTH; i++)
        {
            sCurrentLine = this.br.readLine();
            header += (sCurrentLine);
        }

        BIN_MATCHER.reset(header); //reset the input

        if(!BIN_MATCHER.find())
        {
            throw new IllegalStateException();
        }

        Dimension dimension = new Dimension(Double.parseDouble(BIN_MATCHER.group(1)), Double.parseDouble(BIN_MATCHER.group(2)));
        Double cost = Double.parseDouble(BIN_MATCHER.group(3));

        this.binPattern = new Bin(dimension, cost);
    }

    /**
     * Reads the lines that allow us to create a Map of rectangles
     * If a line is not of the expected pattern, then an
     * IllegalStateException is thrown.
     * @throws IOException
     */
    private void fetchRectanglesMap() throws IOException
    {
        String sCurrentLine;

        while ((sCurrentLine = this.br.readLine()) != null) {
            RECTANGLE_MATCHER.reset(sCurrentLine); //reset the input

            if (!RECTANGLE_MATCHER.find()) {
                throw new IllegalStateException();
            }

            Dimension dimension = new Dimension(Double.parseDouble(RECTANGLE_MATCHER.group(1)), Double.parseDouble(RECTANGLE_MATCHER.group(2)));
            Integer amount = Integer.parseInt(RECTANGLE_MATCHER.group(3));

            this.itemsMap.add(new ItemPattern(dimension, amount));
        }
    }

    /**
     * Returns current BinPattern
     * @return
     */
    public Bin getBinPattern()
    {
        return binPattern;
    }

    /**
     * Sets current binPattern
     * @param binPattern
     */
    public void setBinPattern(Bin binPattern)
    {
        this.binPattern = binPattern;
    }

    /**
     * Returns current ItemPattern List
     * @return
     */
    public ArrayList<ItemPattern> getItemPattern()
    {
        return this.itemsMap;
    }
}
