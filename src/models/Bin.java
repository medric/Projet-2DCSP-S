package models;

import java.util.List;

/**
 * Created by Medric on 18/04/2015.
 */
public class Bin
{
    private Dimension dimension;
    private double cost;
    private List<Rectangle> rectangles;

    public Bin(Dimension dimension, double cost)
    {
        this.setDimension(dimension);
        this.setCost(cost);
    }

    public double getCost()
    {
        return cost;
    }

    public void setCost(double cost)
    {
        this.cost = cost;
    }

    public Dimension getDimension()
    {
        return dimension;
    }

    public void setDimension(Dimension dimension)
    {
        this.dimension = dimension;
    }
}
