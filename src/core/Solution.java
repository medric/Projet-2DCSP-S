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
    private List<Bin> patterns;
    private double patternUnitCost;

    /**
     * Constructor.
     *
     * @param patterns
     * @param application
     */
    public Solution(List<Bin> patterns, List<Rectangle> application) {
        this.setPatterns(patterns);
        this.setApplication(application);

        // Get the first pattern to get the cost
        this.setPatternUnitCost(this.patterns.get(0).getCost());
    }

    /**
     * Get the list of patterns for the current solution.
     *
     * @return
     */
    public List<Bin> getPatterns() {
        return patterns;
    }

    /**
     * Set the list of patterns for the current solution.
     *
     * @param patterns
     */
    public void setPatterns(List<Bin> patterns) {
        this.patterns = patterns;
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
    public List<int[]> getSolutionVectors() {
        List<int[]> solutionVectors = new ArrayList<int[]>();
        int index;

        for(Bin pattern : this.patterns) {
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
}
