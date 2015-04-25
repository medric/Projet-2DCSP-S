package core;

import models.Bin;
import models.FreeRectangle;
import models.Rectangle;

import java.util.ArrayList;


/**
 * Packing class.
 *
 * Implements a Guillotine algorithm to pack a
 * sequence of rectangles into bins.
 *
 * @author Medric
 * @version 1.0
 */
public class Packing
{
    private ArrayList<Rectangle> rectangles;
    private ArrayList<Bin> bins;
    private Bin currentBin;

    private enum Direction {
        VERTICAL,
        HORIZONTAL
    }

    /**
     *
     * @param rectangles
     * @param bin
     */
    public Packing(ArrayList<Rectangle> rectangles, Bin bin)
    {
        // Initializes the sequence of rectangles.
        this.setRectangles(rectangles);

        this.initBins(bin);
    }


    /**
     *
     * @param bin
     */
    private void initBins(Bin bin)
    {
        // Initializes the list of bins and add the given bin to it.
        this.bins = new ArrayList<Bin>();

        // Set current Bin.
        this.currentBin = bin;
    }

    /**
     *
     */
    public void pack()
    {
        // Iterates the collection of rectangles to be packed.
        for(Rectangle rectangle : this.rectangles)
        {
            FreeRectangle freeRectangle = this.findFreeRectangle(rectangle, null, 0, 0);

            this.update(rectangle, freeRectangle);
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
    private FreeRectangle findFreeRectangle(Rectangle rectangle, FreeRectangle freeRectangle, int index, double maxLeftOverArea)
    {
        FreeRectangle currentFreeRectangle = this.currentBin.getFreeRectangles().get(index);
        double remainingArea = currentFreeRectangle.getArea() - rectangle.getArea();

        // Ends recursion.
        if(index == this.currentBin.getFreeRectangles().size() - 1 || rectangle.sameAs(currentFreeRectangle))
        {
            return freeRectangle;
        }

        if(remainingArea > maxLeftOverArea)
        {
            this.findFreeRectangle(rectangle, currentFreeRectangle, index++, remainingArea);
        }

        this.findFreeRectangle(rectangle, freeRectangle, index++, maxLeftOverArea);

        return null;
    }

    /**
     *
     */
    private void merge()
    {

    }

    /**
     *
     * @param rectangle
     * @param freeRectangle
     */
    private void update(Rectangle rectangle, FreeRectangle freeRectangle)
    {
        if(freeRectangle == null)
        {
            this.bins.add(this.currentBin);

            // Call copy constructor to get a new identical Bin.
            this.currentBin = new Bin(this.currentBin);
        }
        else
        {
            // Decides the orientation for the rectangle.
            this.orientate(rectangle, freeRectangle);

            // Places it.
            rectangle.setPosition(freeRectangle.getPosition());
            freeRectangle.setPackedRectangle(rectangle);

            // Guillotine split to subdivide Fi into F' and F''
            this.split(freeRectangle);
        }
    }

    /**
     *
     * @param rectangle
     * @param freeRectangle
     */
    private void orientate(Rectangle rectangle, FreeRectangle freeRectangle)
    {
        double deltaLX, deltaLY;

        deltaLX = freeRectangle.getDimension().getLX() - rectangle.getDimension().getLX();
        deltaLY = freeRectangle.getDimension().getLY() - rectangle.getDimension().getLY();

        if(deltaLX < 0 || deltaLY < 0)
        {
            rectangle.rotate();
        }
    }

    /**
     *
     * @param freeRectangle
     */
    private void split(FreeRectangle freeRectangle)
    {
        ArrayList<FreeRectangle> freeRectangles = new ArrayList<FreeRectangle>();

        
    }

    private void splitVertically()
    {

    }

    private void splitHorizontally()
    {

    }

    /**
     *
     * @param rectangles
     */
    public void setRectangles(ArrayList<Rectangle> rectangles)
    {
        this.rectangles = rectangles;
    }
}
