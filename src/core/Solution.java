package core;

import models.Bin;
import models.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Medric on 16/05/2015.
 */
public class Solution {
    private List<Rectangle> application;
    private ArrayList<Bin> bins;
    private double patternUnitCost;
    private double fitness;

    /**
     *  Constructor.
     */
    public Solution() {
        this.bins = new ArrayList<Bin>();
        this.fitness = 0;
    }

    /**
     * Overwriting
     *
     * @param bins
     * @param application
     */
    public Solution(ArrayList<Bin> bins, List<Rectangle> application) {
        this.setBins(bins);
        this.setApplication(application);

        // Get the first pattern to get the cost
        this.setPatternUnitCost(this.bins.get(0).getCost());
        this.fitness = 0;
    }

    /**
     * Overwriting
     * @param solution
     */
    public Solution(Solution solution) {
        this.bins = (ArrayList<Bin>)solution.bins.clone();
    }

    public double getFitness() {
        return this.fitness;
    }

    /**
     * Get the list of patterns for the current solution.
     *
     * @return
     */
    public ArrayList<Bin> getBins() {
        return bins;
    }

    /**
     * Set the list of patterns for the current solution.
     *
     * @param bins
     */
    public void setBins(ArrayList<Bin> bins) {
        this.bins = (ArrayList<Bin>)bins.clone();
    }

    /**
     * Get the list of demanded rectangles
     *
     * @return a List of Rectangles
     */
    public List<Rectangle> getApplication() {
        return application;
    }

    /**
     * Set the list of demanded rectangles.
     *
     * @param application
     */
    public void setApplication(List<Rectangle> application) {
        this.application = application;
    }

    /**
     * Get pattern cost
     * @return
     */
    public double getPatternUnitCost() {
        return patternUnitCost;
    }

    /**
     * Set pattern cost
     * @param patternUnitCost
     */
    public void setPatternUnitCost(double patternUnitCost) {
        this.patternUnitCost = patternUnitCost;
    }

    /**
     * Skim each pattern to see where each rectangle is packed.
     * @return
     */
    public ArrayList<int[]> getSolutionVectors() {
        ArrayList<int[]> solutionVectors = new ArrayList<int[]>();
        int index;

        for(Bin pattern : this.bins) {
            index = 0;
            int[] vector = new int[this.application.size()];

            for(Rectangle rectangle : this.application) {
                vector[index] = pattern.getRectangleRepetition(rectangle);

                index++;
            }
    
            solutionVectors.add(vector);
        }

        return solutionVectors;
    }

    /**
     * Sets fitness
     * @return
     */
    public void calculFitness() throws Exception {
        // First, resolve simplex for the initial solution
        Simplex simplex = new Simplex(this);

        simplex.solve();

        this.fitness = simplex.getPointValuePair().getValue();
    }

    /**
     * Replaces a bin by a new one.
     * @param oldBin
     * @param newBin
     */
    public void replaceBin(Bin oldBin, Bin newBin) {
        this.getBins().remove(oldBin);
        this.getBins().add(newBin);
    }
}
