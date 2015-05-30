package models;

import core.Packing;

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
}
