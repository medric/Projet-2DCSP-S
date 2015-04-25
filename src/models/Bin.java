package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Medric on 18/04/2015.
 */
public class Bin
{
    private Dimension dimension;
    private double cost;
    private List<FreeRectangle> freeRectangles;

    /**
     *
     * @param dimension
     * @param cost
     */
    public Bin(Dimension dimension, double cost)
    {
        this.setDimension(dimension);
        this.setCost(cost);

        this.initFreeRectangles();
    }

    /**
     *
     */
    private void initFreeRectangles()
    {
        // Initializes the list of free rectangles.
        this.freeRectangles = new ArrayList<FreeRectangle>();

        FreeRectangle rectangle = new FreeRectangle(this.getDimension(), new Position(0, 0));
        this.freeRectangles.add(rectangle);
    }

    /**
     * Copy constructor.
     */
    public Bin(Bin bin)
    {
        this(bin.getDimension(), bin.getCost());
    }

    /**
     *
     * @return
     */
    public double getCost()
    {
        return cost;
    }

    /**
     *
     * @param cost
     */
    public void setCost(double cost)
    {
        this.cost = cost;
    }

    /**
     *
     * @return
     */
    public Dimension getDimension()
    {
        return dimension;
    }

    /**
     *
     * @param dimension
     */
    public void setDimension(Dimension dimension)
    {
        this.dimension = dimension;
    }

    /**
     *
     * @return
     */
    public List<FreeRectangle> getFreeRectangles()
    {
        return freeRectangles;
    }
}
