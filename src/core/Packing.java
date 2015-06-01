package core;

import models.*;

import java.util.ArrayList;


/**
 * Packing class.
 * Implements a Guillotine algorithm to pack a
 * sequence of rectangles into bins.
 *
 * @author Medric, Gabi, P-J
 * @version 1.0
 */
public class Packing
{
    private ArrayList<Rectangle> rectangles;
    public Solution solution;

    private ArrayList<Bin> bins;
    private Bin currentBin;

    public ArrayList<Bin> getBins() {
        return bins;
    }

    /**
     * @param rectangles
     * @param bin
     */
    public Packing(ArrayList<Rectangle> rectangles, Bin bin) {
        // Initializes the sequence of rectangles.
        this.setRectangles(rectangles);

        this.initBins(bin);
    }

    public Packing() {

    }

    public ArrayList<Rectangle> getRectangles() {
        return  this.rectangles;
    }


    /**
     * @param bin
     */
    private void initBins(Bin bin) {
        // Initializes the list of bins through solution model
        this.solution = new Solution();

        // Init current Bin.
        this.currentBin = bin;

        // Add the given bin to the list of bin
        this.solution.getBins().add(this.currentBin);
    }

    /**
     * Main method. Packs rectangles into bins.
     *
     * @return the current Solution object.
     */
    public Solution pack() {
        Rectangle maximized;
        Rectangle freeRectangle;

        // Iterates the collection of rectangles to be packed.
        for (Rectangle rectangle : this.rectangles) {
            maximized = null;

            // For each opened bin.
            for (Bin bin : this.solution.getBins()) {
                freeRectangle = this.findFreeRectangle(bin, rectangle, null, 0, 0);

                if (maximized == null || (freeRectangle != null && (maximized.getArea() < freeRectangle.getArea()))) {
                    this.currentBin = bin;
                    maximized = freeRectangle;
                }
            }

            // Then, update free Rectangles onto a current bin (split involved).
            this.update(rectangle, maximized);
        }

        return this.solution;
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
    public Rectangle findFreeRectangle(Bin bin, Rectangle rectangle, Rectangle freeRectangle, int index, double maxLeftOverArea) {

        // End of collections reached.
        if (!(index == bin.getFreeRectangles().size())) {

            Rectangle currentFreeRectangle = bin.getFreeRectangles().get(index);

            double leftOverArea = currentFreeRectangle.getArea() - rectangle.getArea();

            //if (!rectangle.sameAs(currentFreeRectangle)) {

            if (currentFreeRectangle.compareTo(rectangle) <= 0 && leftOverArea > maxLeftOverArea) {
                freeRectangle = this.findFreeRectangle(bin, rectangle, currentFreeRectangle, ++index, leftOverArea);
            } else {
                freeRectangle = this.findFreeRectangle(bin, rectangle, freeRectangle, ++index, maxLeftOverArea);
            }
            //}
        }

        // Return found freeRectangle.
        return freeRectangle;
    }

    /**
     * @param rectangle
     * @param freeRectangle
     */
    private void update(Rectangle rectangle, Rectangle freeRectangle) {
        if (freeRectangle == null) {

            // Call copy constructor to get a new identical Bin.
            this.currentBin = new Bin(this.currentBin);

            // Get the first free rectangle of the new current bin.
            freeRectangle = this.currentBin.getFreeRectangles().get(0);

            if (!this.solution.getBins().contains(this.currentBin)) {
                this.solution.getBins().add(this.currentBin);
            }
        }

        // Decides the orientation for the rectangle.
        this.orientate(rectangle, freeRectangle);

        // Places it.
        rectangle.setPosition(freeRectangle.getPosition());
        this.currentBin.addRectangle(rectangle);

        // Guillotine split to subdivide Fi into F' and F''
        this.split(rectangle, freeRectangle);
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

    public void split(Bin bin, Rectangle rectangle, Rectangle freeRectangle) {
        double deltaLX, deltaLY;

        deltaLX = freeRectangle.getDimension().getLX() - rectangle.getDimension().getLX();
        deltaLY = freeRectangle.getDimension().getLY() - rectangle.getDimension().getLY();

        // Shorter Leftover Axis Split Rule (-SLAS).
        bin.splitFreeRectangle(rectangle, freeRectangle, deltaLX < deltaLY ? Direction.HORIZONTAL : Direction.VERTICAL);
    }


    /**
     * Performs split.
     *
     * @param freeRectangle
     */
    private void split(Rectangle rectangle, Rectangle freeRectangle) {
        this.split(this.currentBin, rectangle, freeRectangle);
    }

    /**
     * @param rectangles
     */
    public void setRectangles(ArrayList<Rectangle> rectangles) {
        this.rectangles = rectangles;
    }
}
