package models;

import core.Solution;

import java.util.ArrayList;

/**
 * Created by Epulapp on 26/05/2015.
 */
public class Population {
    int populationSize;
    private ArrayList<Solution> individuals;

    public Population(int size) {
        this.individuals = new ArrayList<Solution>();
        this.populationSize = size;
    }

    public Population(int size, Solution firstIndividual) {
        this.individuals = new ArrayList<Solution>();
        this.addIndividual(firstIndividual);
        this.populationSize = size;
    }

    public int getPopulationSize() {
        return this.populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public ArrayList<Solution> getIndividuals() {
        return this.individuals;
    }

    public void setIndividuals(ArrayList<Solution> individuals) {
        this.individuals = individuals;
    }

    public void addIndividual(Solution individual) {
        this.individuals.add(individual);
    }

    public Solution getBestSolution() {
        Solution bestSolution = null;

        for(Solution solution : this.getIndividuals()) {
            if(bestSolution == null || solution.getFitness() < bestSolution.getFitness()) {
                bestSolution = solution;
            }
        }

        return bestSolution;
    }

    public double getTotalFitness() {
        double total = 0;

        for(Solution solution : this.getIndividuals()) {
            total += solution.getFitness();
        }

        return  total;
    }
}
