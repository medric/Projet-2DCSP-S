package models;

/**
 * Created by Medric on 18/04/2015.
 */
public class Dimension
{

    private double LX;
    private double LY;

    /**
     *
     * @param LX
     * @param LY
     */
    public Dimension(double LX, double LY)
    {
        this.setLX(LX);
        this.setLY(LY);
    }

    /**
     * Reverses LX / LY.
     */
    public void reverse()
    {
        double LX = this.getLX();
        this.setLX(this.getLY());
        this.setLY(LX);
    }

    /**
     *
     * @return
     */
    public double getLX()
    {
        return LX;
    }

    /**
     *
     * @param LX
     */
    public void setLX(double LX)
    {
        this.LX = LX;
    }

    /**
     *
     * @return
     */
    public double getLY()
    {
        return LY;
    }

    /**
     *
     * @param LY
     */
    public void setLY(double LY)
    {
        this.LY = LY;
    }
}
