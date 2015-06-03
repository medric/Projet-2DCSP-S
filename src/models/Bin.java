package models;

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

        double x1 = 0d;
        double y1 = 0d;
        // firstSubFreeRectangle
        try {
            x1 = freeRectangle.getPosition().getX() + rectangle.getDimension().getLX();
            y1 = freeRectangle.getPosition().getY();
        }catch(NullPointerException ex) {
            System.out.println();
        }

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
     * @param rectangles new list of rectangle
     */
    public void setRectangles(List<Rectangle> rectangles) {
        Rectangle newRectangle = null;
        this.rectangles = new ArrayList();

        for(Rectangle rectangle : rectangles) {
            newRectangle = new Rectangle();
            newRectangle.setId(rectangle.getId());
            newRectangle.setDimension(new Dimension(rectangle.getDimension()));
            newRectangle.setPosition(new Position(rectangle.getPosition()));

            this.rectangles.add(newRectangle);
        }
    }

    /**
     * Adds free rectangle to the current bin.
     *
     * @param rectangle
     */
    public void addFreeRectangle(Rectangle rectangle) {
        this.freeRectangles.add(rectangle);
    }

    /**
     * Removes free rectangle from the current bin.
     *
     * @param rectangle
     */
    public void removeFreeRectangle(Rectangle rectangle) {
        this.freeRectangles.remove(rectangle);
    }

    /**
     * Adds a rectangle to the list of rectangles packed in this bin.
     *
     * @param rectangle
     */
    public void addRectangle(Rectangle rectangle) {
        this.rectangles.add(rectangle);
    }

    /**
     * Gets the number of times a rectangle is found in the current bin.
     *
     * @param _rectangle
     * @return
     */
    public int getRectangleRepetition(Rectangle _rectangle) {
        int repetition = 0;

        for(Rectangle rectangle : this.rectangles) {
            // The comparison is based on the dimension
            try {
                if (rectangle.getId().equals(_rectangle.getId())) {
                    repetition++;
                }
            }catch(NullPointerException ex) {
                System.out.println();
            }
        }

        return repetition;
    }

    public boolean equals(Bin binToCompare) {
        boolean result = false;

        List<Rectangle> rectangles = binToCompare.getRectangles();
        Dimension rectangleDimension = null;
        Position rectanglePosition = null;

        for (int i = 0; i < rectangles.size(); i++) {
            rectangleDimension = rectangles.get(i).getDimension();
            rectanglePosition = rectangles.get(i).getPosition();

            if(rectangleDimension.equals(this.getRectangles().get(i).getDimension()) && rectanglePosition.equals(this.getRectangles().get(i).getPosition())) {
                result = true;
            }
        }
        return result;
    }
}
