package models;

import java.util.ArrayList;

/**
 * Created by Medric on 25/04/2015.
 */
public class FreeRectangle extends Rectangle
{
    private Rectangle packedRectangle;

    /**
     *
     * @param dimension
     * @param position
     */
    public  FreeRectangle(Dimension dimension, Position position)
    {
        super(dimension);
        super.setPosition(position);
    }

    /**
     *
     * @param rectangle
     */
    public void setPackedRectangle(Rectangle rectangle)
    {
        this.packedRectangle = rectangle;
    }
}
