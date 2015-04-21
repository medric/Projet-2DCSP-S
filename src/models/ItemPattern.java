package models;

/**
 * Created by Medric on 21/04/2015.
 */
public class ItemPattern
{
    private Dimension dimension;
    private int amount;

    public ItemPattern(Dimension dimension, int amount)
    {
        this.setDimension(dimension);
        this.setAmount(amount);
    }

    public int getAmount()
    {
        return amount;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
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
