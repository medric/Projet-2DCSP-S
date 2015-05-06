package models;

import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Medric on 18/04/2015.
 */
public class Bin {
    private Dimension dimension;
    private double cost;
    private List<Rectangle> freeRectangles;
    private List<Rectangle> rectangles;

    /**
     * @param dimension
     * @param cost
     */
    public Bin(Dimension dimension, double cost) {
        this.setDimension(dimension);
        this.setCost(cost);

        this.initFreeRectangles();
        this.rectangles = new ArrayList<Rectangle>();
    }

    /**
     *
     */
    private void initFreeRectangles() {
        // Initializes the list of free rectangles.
        this.freeRectangles = new ArrayList<Rectangle>();

        Rectangle rectangle = new Rectangle(this.getDimension(), new Position(0, 0));
        this.freeRectangles.add(rectangle);
    }


    /**
     * Updates free rectangles of the current bin.
     *
     * @param freeRectangle
     * @param firstSubFreeRectangle
     * @param secondSubFreeRectangle
     */
    private void updateBinsFreeRectangles(Rectangle freeRectangle, Rectangle firstSubFreeRectangle, Rectangle secondSubFreeRectangle) {
        // Removes the current free rectangle.
        this.removeFreeRectangle(freeRectangle);

        // Adds new subdivided free rectangles.
        this.addFreeRectangle(firstSubFreeRectangle);
        this.addFreeRectangle(secondSubFreeRectangle);
    }

    /**
     * @param rectangle
     * @param freeRectangle
     * @param splitDirection
     */
    public void splitFreeRectangle(Rectangle rectangle, Rectangle freeRectangle, Direction splitDirection) {
        Rectangle firstSubFreeRectangle;
        Rectangle secondSubFreeRectangle;

        // firstSubFreeRectangle
        double x1 = freeRectangle.getPosition().getX() + rectangle.getDimension().getLX();
        double y1 = freeRectangle.getPosition().getY();

        double LX = freeRectangle.getDimension().getLX() - rectangle.getDimension().getLX();
        double LY = freeRectangle.getDimension().getLY();

        if (splitDirection.equals(Direction.HORIZONTAL)) {

            LY -= rectangle.getDimension().getLY();
        }

        firstSubFreeRectangle = new Rectangle(new Dimension(LX, LY), new Position(x1, y1));

        // secondSubFreeRectangle
        x1 = freeRectangle.getPosition().getX();
        y1 = freeRectangle.getPosition().getY() + rectangle.getDimension().getLY();

        if (splitDirection.equals(Direction.VERTICAL)) {

            LX = rectangle.getDimension().getLX();
        }

        LY = freeRectangle.getDimension().getLY() - rectangle.getDimension().getLY();

        secondSubFreeRectangle = new Rectangle(new Dimension(LX, LY), new Position(x1, y1));

        // Update free rectangles collection.
        this.updateBinsFreeRectangles(freeRectangle, firstSubFreeRectangle, secondSubFreeRectangle);
    }

    /**
     * Copy constructor.
     */
    public Bin(Bin bin) {
        this(bin.getDimension(), bin.getCost());
    }

    /**
     * @return
     */
    public double getCost() {
        return cost;
    }

    /**
     * @param cost
     */
    public void setCost(double cost) {
        this.cost = cost;
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
    public List<Rectangle> getFreeRectangles() {
        return freeRectangles;
    }

    /**
     *
     * @return
     */
    public List<Rectangle> getRectangles() {
        return rectangles;
    }

    /**
     * Add free rectangle to the current bin.
     *
     * @param rectangle
     */
    public void addFreeRectangle(Rectangle rectangle) {
        this.freeRectangles.add(rectangle);
    }

    /**
     * Remove free rectangle from the current bin.
     *
     * @param rectangle
     */
    public void removeFreeRectangle(Rectangle rectangle) {
        this.freeRectangles.remove(rectangle);
    }


    public void addRectangle(Rectangle rectangle) {
        this.rectangles.add(rectangle);
    }
}
