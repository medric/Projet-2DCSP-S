package core;

import models.*;

import java.util.ArrayList;


/**
 * Packing class.
 * <p>
 * Implements a Guillotine algorithm to pack a
 * sequence of rectangles into bins.
 *
 * @author Medric, Gabi, P-J
 * @version 1.0
 */
public class Packing {
    private ArrayList<Rectangle> rectangles;
    private ArrayList<Bin> bins;
    private Bin currentBin;

    /**
     * @param rectangles
     * @param bin
     */
    public Packing(ArrayList<Rectangle> rectangles, Bin bin) {
        // Initializes the sequence of rectangles.
        this.setRectangles(rectangles);

        this.initBins(bin);
    }


    /**
     * @param bin
     */
    private void initBins(Bin bin) {
        // Initializes the list of bins and add the given bin to it.
        this.bins = new ArrayList<Bin>();

        // Init current Bin.
        this.currentBin = bin;
    }

    /**
     *
     */
    public void pack() {
        Rectangle maximized = null;

        // Iterates the collection of rectangles to be packed.
        for (Rectangle rectangle : this.rectangles) {

            // For each opened bin in bins.
            for(Bin bin : this.bins) {
                Rectangle freeRectangle = this.findFreeRectangle(rectangle, null, 0, 0);

                if(maximized != null && maximized.getArea() < freeRectangle.getArea()) {
                    maximized = freeRectangle;
                }
            }

            // Then, update free Rectangles onto a current bin (split involved).
            this.update(rectangle, maximized);
        }
    }

    /**
     * Guillotine Worst Area Fit (GUILLOTINE-WAF)
     * algorithm packs R into the Fi such that the area
     * left over is maximized.
     *
     * @param rectangle
     * @param freeRectangle
     * @param index
     * @param maxLeftOverArea
     * @return
     */
    private Rectangle findFreeRectangle(Rectangle rectangle, Rectangle freeRectangle, int index, double maxLeftOverArea) {

        // End of collections reached.
        if(!(index == this.currentBin.getFreeRectangles().size())) {

            Rectangle currentFreeRectangle = this.currentBin.getFreeRectangles().get(index);
            double leftOverArea = currentFreeRectangle.getArea() - rectangle.getArea();

            if (!rectangle.sameAs(currentFreeRectangle)) {

                if (leftOverArea > maxLeftOverArea) {
                    freeRectangle = this.findFreeRectangle(rectangle, currentFreeRectangle, ++index, leftOverArea);
                } else {
                    freeRectangle = this.findFreeRectangle(rectangle, freeRectangle, ++index, maxLeftOverArea);
                }
            }
        }

        // Return found freeRectangle.
        return freeRectangle;
    }

    /**
     *
     */
    private void merge() {

    }

    /**
     * @param rectangle
     * @param freeRectangle
     */
    private void update(Rectangle rectangle, Rectangle freeRectangle) {
        if (freeRectangle == null) {
            this.bins.add(this.currentBin);

            // Call copy constructor to get a new identical Bin.
            this.currentBin = new Bin(this.currentBin);

            // Get first free rectangle of the new current bin.
            freeRectangle = this.currentBin.getFreeRectangles().get(0);
        }

        // Decides the orientation for the rectangle.
        this.orientate(rectangle, freeRectangle);

        // Places it.
        rectangle.setPosition(freeRectangle.getPosition());

        // Guillotine split to subdivide Fi into F' and F''
        this.split(freeRectangle, rectangle);

    }

    /**
     * @param rectangle
     * @param freeRectangle
     */
    private void orientate(Rectangle rectangle, Rectangle freeRectangle) {
        double deltaLX, deltaLY;

        deltaLX = freeRectangle.getDimension().getLX() - rectangle.getDimension().getLX();
        deltaLY = freeRectangle.getDimension().getLY() - rectangle.getDimension().getLY();

        if (deltaLX < 0 || deltaLY < 0) {
            rectangle.rotate();
        }
    }

    /**
     * Splits.
     *
     * @param freeRectangle
     */
    private void split(Rectangle rectangle, Rectangle freeRectangle) {
        double deltaLX, deltaLY;

        deltaLX = freeRectangle.getDimension().getLX() - rectangle.getDimension().getLX();
        deltaLY = freeRectangle.getDimension().getLY() - rectangle.getDimension().getLY();

        // Shorter Leftover Axis Split Rule (-SLAS).
        this.currentBin.splitFreeRectangle(rectangle, freeRectangle, deltaLX < deltaLY ? Direction.HORIZONTAL : Direction.VERTICAL);
    }

    /**
     * @param rectangles
     */
    public void setRectangles(ArrayList<Rectangle> rectangles) {
        this.rectangles = rectangles;
    }
}
