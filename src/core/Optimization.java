package core;

import models.*;

import java.util.*;

/**
 * Created by Epulapp on 26/05/2015.
 */
public class Optimization {
    private Population population;
    private int generationNumber;

    public  Optimization(int generationNumber, int size, Solution firstIndividual) {
        this.population = new Population(size, firstIndividual);
        this.generationNumber = generationNumber;
    }

    public Population getPopulation() {
        return this.population;
    }

    public void setPopulation(Population population) {
        this.population = population;
    }

    public void optimize() {
        // Genesis
        this.genesis();
        // Evolution
        for(int i = 0; i < generationNumber; i++) {
            this.evolvePopulation();
        }
        // Apocalypse
        this.apocalypse();
    }

    private void genesis() {
        Solution firstIndividual = this.population.getIndividuals().get(0);
        int numberOfBinInFirstSolution = firstIndividual.getBins().size();
        Random random = new Random();
        CustomHashMap<Integer, ArrayList<Integer>> combination = new CustomHashMap<Integer, ArrayList<Integer>>();
        int firstBinChoosen = 0;
        int secondBinChoosen = 0;
        Solution newSolution = null;

        // Faire toutes les combinaisons possibles
        for(int i = 1; i <= numberOfBinInFirstSolution - 1; i++) {
            combination.put(i, new ArrayList<Integer>());
            for(int j = i + 1; j <= numberOfBinInFirstSolution; j++) {
                combination.get(i).add(j);
            }
        }


        int size = 0;
        // Tant que la population n'est pas entière
        for(int i = 1; i < this.population.getPopulationSize(); i++) {
            newSolution = null;
            while (newSolution == null){
                // Tirer au sort une combinaison
                firstBinChoosen = random.nextInt(numberOfBinInFirstSolution - 1) + 1;
                size = combination.get(firstBinChoosen).size() - 1;
                secondBinChoosen = size == 0 ? 0 : random.nextInt(size);

                //Appeler SwitchImage
                newSolution = this.switchImage(firstIndividual.getBins().get(firstBinChoosen), firstIndividual.getBins().get(secondBinChoosen), firstBinChoosen, secondBinChoosen);
                this.population.addIndividual(newSolution);

                // L'enlever de combination
                combination.get(combination.indexOf(firstBinChoosen)).remove(combination.get(firstBinChoosen).get(secondBinChoosen));

                if (combination.get(firstBinChoosen).isEmpty()) {
                    combination.remove(firstBinChoosen);
                    numberOfBinInFirstSolution = combination.size();
                }
            }
        }
    }

    private void evolvePopulation() {
        this.population = this.tournamentSelection();

        this.performCrossover();

        this.mutate();
    }

    private void apocalypse() {
        // Méthode tabou
    }

    private Solution switchImage(Bin firstChoosenBin, Bin secondChoosenBin, int firstBinIndex, int secondBinIndex) {
        Random random = new Random();
        int indexOfImage = 0;
        Rectangle firstImageToSwitch = null;
        int indexOfSecondImageToSwitch = 0;
        Solution newSolution = null;

        // Choisis une image dans le premier BIN
        indexOfImage = random.nextInt(firstChoosenBin.getRectangles().size());
        firstImageToSwitch = firstChoosenBin.getRectangles().get(indexOfImage);

        // Prend la première image qui peut correspondre
        indexOfSecondImageToSwitch = this.getIndexSecondImage(firstImageToSwitch, secondChoosenBin);

        //switch dans les patterns
        if(indexOfSecondImageToSwitch != 0) {
            this.performSwitch(indexOfImage, indexOfSecondImageToSwitch, firstBinIndex, secondBinIndex);
        }
        return newSolution;
    }

    private int getIndexSecondImage(Rectangle firstImage, Bin secondChoosenBin) {
        Random random = new Random();
        boolean isSecondImageChoose = false;
        int numberOfImageInSecondBin = secondChoosenBin.getRectangles().size();
        int choosenIndex = 0;
        int iterationNumber = 0;
        Dimension imagePlusFreeAreaDimension = null;

        while (!isSecondImageChoose && iterationNumber < 500) {
            iterationNumber++;
            choosenIndex = random.nextInt(numberOfImageInSecondBin);

            imagePlusFreeAreaDimension = this.getImagePlusFreeAreaDimension(secondChoosenBin.getRectangles().get(choosenIndex), choosenIndex);

            if(firstImage.getDimension().getLY() <= imagePlusFreeAreaDimension.getLY()
                    && firstImage.getDimension().getLX() <= imagePlusFreeAreaDimension.getLX()) {
                imagePlusFreeAreaDimension = this.getImagePlusFreeAreaDimension(firstImage, choosenIndex);
                if(firstImage.getDimension().getLY() <= imagePlusFreeAreaDimension.getLY()
                        && firstImage.getDimension().getLX() <= imagePlusFreeAreaDimension.getLX()) {
                    isSecondImageChoose = true;
                }
            }
        }

        return  choosenIndex;
    }

    private Dimension getImagePlusFreeAreaDimension(Rectangle choosenRectangle, int indexOfSecondChoosenBin) {
        Rectangle freeRectangle = null;
        ArrayList<Rectangle> freeRectanglesX = new ArrayList<>();
        ArrayList<Rectangle> freeRectanglesY = new ArrayList<>();
        Bin pattern = this.population.getIndividuals().get(0).getBins().get(indexOfSecondChoosenBin);
        Iterator<Rectangle> iterator = pattern.getFreeRectangles().iterator();
        Dimension imagePlusFreeAreaDimension = new Dimension(0,0);

        while(iterator.hasNext()) {
            freeRectangle = iterator.next();

            if(freeRectangle.getPosition().getY() == choosenRectangle.getPosition().getY()) {
                if((freeRectangle.getDimension().getLX() + freeRectangle.getPosition().getX() == choosenRectangle.getPosition().getX()
                        || choosenRectangle.getDimension().getLX() + choosenRectangle.getPosition().getX() == freeRectangle.getPosition().getX())
                        && (freeRectangle.getDimension().getLY() >= choosenRectangle.getDimension().getLY())) {
                    freeRectanglesX.add(freeRectangle);
                } else if((freeRectangle.getDimension().getLY() + freeRectangle.getPosition().getY() == choosenRectangle.getPosition().getY()
                        || choosenRectangle.getDimension().getLY() + choosenRectangle.getPosition().getY() == freeRectangle.getPosition().getY())
                        && (freeRectangle.getDimension().getLX() >= choosenRectangle.getDimension().getLX())) {
                    freeRectanglesY.add(freeRectangle);
                }
            }
        }

        for(Rectangle rectangle : freeRectanglesX) {
            imagePlusFreeAreaDimension.setLX(imagePlusFreeAreaDimension.getLX() + rectangle.getDimension().getLX());
        }

        for(Rectangle rectangle : freeRectanglesY) {
            imagePlusFreeAreaDimension.setLY(imagePlusFreeAreaDimension.getLY() + rectangle.getDimension().getLY());
        }

        return imagePlusFreeAreaDimension;
    }

    private Solution performSwitch(int indexImageFirstBin, int indexImageSecondBin, int firstBinIndex, int secondBinIndex) {
        Solution solution = new Solution(this.population.getIndividuals().get(0));
        Rectangle imageFirstBin = solution.getBins().get(firstBinIndex).getRectangles().get(indexImageFirstBin);
        Rectangle imageSecondBin = solution.getBins().get(secondBinIndex).getRectangles().get(indexImageSecondBin);
        Position secondImagePosition = null;
        Dimension dimensionFirstRectangle = this.getImagePlusFreeAreaDimension(imageFirstBin, firstBinIndex);
        Dimension dimensionSecondRectangle = this.getImagePlusFreeAreaDimension(imageSecondBin, secondBinIndex);
        imageFirstBin.setDimension(dimensionSecondRectangle);
        imageSecondBin.setDimension(dimensionFirstRectangle);
        secondImagePosition = imageSecondBin.getPosition();
        imageSecondBin.setPosition(imageFirstBin.getPosition());
        imageFirstBin.setPosition(secondImagePosition);

        return  solution;
    }

    // Crossover individuals
    private Solution performCrossover(Solution firstIndividual, Solution secondIndividual) {
        Solution newSolution = new Solution();

        return newSolution;
    }

    // Mutate an individual
    private void mutate(Solution individual) {

    }

    // Roulette
    private Population tournamentSelection() {
        Random random = new Random();
        Population nextGeneration = new Population(this.population.getPopulationSize());

        for (int i = 0; i < nextGeneration.getPopulationSize(); i++) {
            int randomId = random.nextInt(nextGeneration.getPopulationSize());
            nextGeneration.addIndividual(this.population.getIndividuals().get(randomId));
        }

        return nextGeneration;
    }
}
