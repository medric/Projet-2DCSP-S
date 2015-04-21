package models;

/**
 * Created by Medric on 18/04/2015.
 */
public class Item
{
    Position position;
    Dimension dimension;

    public Item(Dimension dimension)
    {
        this.setDimension(dimension);
    }

    public Item(ItemPattern pattern)
    {
        this.setDimension(pattern.getDimension());
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }
}
