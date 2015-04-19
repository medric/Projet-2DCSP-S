package models;

/**
 * Created by Medric on 18/04/2015.
 */
public class Dimension
{

    private double LX;
    private double LY;

    public Dimension(double LX, double LY)
    {
        this.setLX(LX);
        this.setLY(LY);
    }

    public double getLX()
    {
        return LX;
    }

    public void setLX(double LX)
    {
        this.LX = LX;
    }

    public double getLY()
    {
        return LY;
    }

    public void setLY(double LY)
    {
        this.LY = LY;
    }
}
