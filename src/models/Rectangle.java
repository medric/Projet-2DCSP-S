package models;

/**
 * Created by Medric on 18/04/2015.
 */
public class Rectangle
{
    Position position;
    Dimension dimension;

    public Rectangle(Dimension dimension)
    {
        this.setDimension(dimension);
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
