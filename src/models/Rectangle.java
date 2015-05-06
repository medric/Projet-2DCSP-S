package models;

import javafx.geometry.Pos;

/**
 * Created by Medric on 18/04/2015.
 */
public class Rectangle implements Comparable<Rectangle> {
    protected Position position;
    protected Dimension dimension;
    protected Integer amount;

    /**
     * Default constructor.
     */
    public Rectangle() {
    }

    /**
     * Override constructor.
     *
     * @param dimension
     */
    public Rectangle(Dimension dimension) {
        this.setDimension(dimension);
    }

    /**
     * Override constructor.
     *
     * @param dimension
     */
    public Rectangle(Dimension dimension, Position position) {
        this.setDimension(dimension);
        this.setPosition(position);
    }

    /**
     * Override constructor.
     *
     * @param dimension
     */
    public Rectangle(Dimension dimension, int amount) {
        this.setDimension(dimension);
        this.setAmount(amount);
    }

    /**
     * Copy constructor.
     */
    public Rectangle(Bin bin) {
        this(bin.getDimension());
    }

    /**
     * Rotates the current Rectangle by reversing dimensions.
     */
    public void rotate() {
        this.getDimension().reverse();
    }

    /**
     * Computes current Rectangle area.
     */
    public double getArea() {
        return this.getDimension().getLX() * this.getDimension().getLY();
    }

    /**
     * @param rectangle
     * @return
     */
    public boolean sameAs(Rectangle rectangle) {
        return this.getArea() == rectangle.getArea();
    }

    /**
     * @return
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @param position
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * @return
     */
    public Dimension getDimension() {
        return dimension;
    }

    /**
     * @param dimension
     */
    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    /**
     * @return
     */
    public Integer getAmount() {
        return amount;
    }

    /**
     * @param amount
     */
    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public int compareTo(Rectangle rectangle) {
        int result = this.isSuitable(rectangle);

        if(result == 1) {
            rectangle.rotate();
            result = this.isSuitable(rectangle);
            rectangle.rotate();
        }

        return result;
    }

    private int isSuitable(Rectangle rectangle) {
        int result = 1;

        if(this.getDimension().getLX() > rectangle.getDimension().getLX() && this.getDimension().getLY() > rectangle.getDimension().getLY()) {
            result = -1;
        } else if (this.getDimension().getLX() == rectangle.getDimension().getLX() && this.getDimension().getLY() == rectangle.getDimension().getLY()) {
            result = 0;
        }

        return  result;
    }
}
