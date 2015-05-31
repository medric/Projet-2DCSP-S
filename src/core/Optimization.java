package core;

import models.*;

import java.util.*;

/**
 * Created by Epulapp on 26/05/2015.
 */
public class Optimization
{
    private Population population;
    private int generationNumber;
    private int mutationProbabilityIndex;

    /**
     *
     * @param generationNumber
     * @param size
     * @param firstIndividual
     */
    public Optimization(int generationNumber, int size, Solution firstIndividual) {
        this.population = new Population(size, firstIndividual);
        this.generationNumber = generationNumber;


        // Default, might change
        this.mutationProbabilityIndex = 2; // Will give a 1/2 probability
    }

    /**
     *
     * @return
     */
    public Population getPopulation() {
        return this.population;
    }

    /**
     *
     * @param population
     */
    public void setPopulation(Population population) {
        this.population = population;
    }

    /**
     *
     */
    public void optimize() {
        // Genesis
        this.genesis();

        // Evolution
        for (int i = 0; i < generationNumber; i++) {
            this.evolvePopulation();
        }

        // Apocalypse
        this.apocalypse();
    }

    /**
     *
     */
    private void genesis() {
        Solution firstIndividual = this.population.getIndividuals().get(0);
        Random random = new Random();
        CustomHashMap<Integer, ArrayList<Integer>> combination = new CustomHashMap<Integer, ArrayList<Integer>>();
        Solution newSolution;
        int size = 0;

        int numberOfBinInFirstSolution = firstIndividual.getBins().size();
        int firstBinChosen;
        int secondBinChosen;

        // Faire toutes les combinaisons possibles
        for (int i = 1; i <= numberOfBinInFirstSolution - 1; i++) {
            combination.put(i, new ArrayList<Integer>());

            for (int j = i + 1; j <= numberOfBinInFirstSolution; j++) {
                combination.get(i).add(j);
            }
        }

        // Tant que la population n'est pas entiÃ¨re
        for (int i = 1; i < this.population.getPopulationSize(); i++) {
            newSolution = null;

            while (newSolution == null) {
                // Tirer au sort une combinaison
                firstBinChosen = random.nextInt(numberOfBinInFirstSolution - 1) + 1;
                size = combination.get(firstBinChosen).size() - 1;
                secondBinChosen = size <= 0 ? 0 : random.nextInt(size);

                //Appeler SwitchImage
                newSolution = this.switchImage(firstIndividual.getBins().get(firstBinChosen), firstIndividual.getBins().get(secondBinChosen), firstBinChosen, secondBinChosen);

                // L'enlever de combination
                combination.get(combination.indexOf(firstBinChosen)).remove(combination.get(firstBinChosen).get(secondBinChosen));

                if (combination.get(firstBinChosen).isEmpty()) {
                    combination.remove(firstBinChosen);
                    numberOfBinInFirstSolution = combination.size();
                }
            }

            this.population.addIndividual(newSolution);
        }
    }

    /**
     *
     */
    private void evolvePopulation() {
        this.population = this.tournamentSelection();

        /*this.performCrossover();

        this.mutate();*/
    }

    /**
     *
     */
    private void apocalypse() {
        // MÃ©thode tabou
    }

    /**
     *
     * @param firstBinChosen
     * @param secondBinChosen
     * @param firstBinIndex
     * @param secondBinIndex
     * @return
     */
    private Solution switchImage(Bin firstBinChosen, Bin secondBinChosen, int firstBinIndex, int secondBinIndex) {
        Random random = new Random();
        Rectangle firstImageToSwitch;

        int indexOfSecondImageToSwitch;
        int indexOfImage;

        Solution newSolution = null;

        // Choisis une image dans le premier BIN
        indexOfImage = random.nextInt(firstBinChosen.getRectangles().size());
        firstImageToSwitch = firstBinChosen.getRectangles().get(indexOfImage);

        // Prend la premiÃ¨re image qui peut correspondre
        indexOfSecondImageToSwitch = this.getIndexSecondImage(firstImageToSwitch, firstBinChosen, secondBinChosen);

        //switch dans les patterns
        if (indexOfSecondImageToSwitch != 0) {
            newSolution = this.performSwitch(indexOfImage, indexOfSecondImageToSwitch, firstBinIndex, secondBinIndex);
        }

        return newSolution;
    }

    /**
     *
     * @param firstImage
     * @param secondBinChosen
     * @return
     */
    private int getIndexSecondImage(Rectangle firstImage, Bin firstBinChosen, Bin secondBinChosen) {
        Rectangle secondImage = null;
        Bin copySecondBinChoosen = new Bin(secondBinChosen);
        copySecondBinChoosen.setRectangles((ArrayList<Rectangle>) secondBinChosen.getRectangles());
        Bin copyFirstBinChoosen = null;
        Random random = new Random();
        boolean isSecondImageChosen = false;
        int numberOfImageInSecondBin = copySecondBinChoosen.getRectangles().size();
        int chosenIndex = 0;
        int iterationNumber = 1;

        while (!isSecondImageChosen && iterationNumber < 500) {
            copySecondBinChoosen.setRectangles((ArrayList<Rectangle>) secondBinChosen.getRectangles());
            iterationNumber++;
            chosenIndex = random.nextInt(numberOfImageInSecondBin);

            secondImage = copySecondBinChoosen.getRectangles().get(chosenIndex);

            if(secondImage.compareTo(firstImage) > 0) {
                secondImage.setDimension(firstImage.getDimension());

                if (secondImage.getPosition().getX() + secondImage.getDimension().getLX() <= copySecondBinChoosen.getDimension().getLX()
                        && secondImage.getPosition().getY() + secondImage.getDimension().getLY() <= copySecondBinChoosen.getDimension().getLY()) {
                    isSecondImageChosen = this.canFirstImageReplacedSecondImage(copySecondBinChoosen, secondImage);
                }
            } else {
                isSecondImageChosen = true;
            }

            if(isSecondImageChosen) {
                //Vérifier si la deuxième image rentre bien dans la première
            }

            if(!isSecondImageChosen && iterationNumber == 500) {
                chosenIndex = 0;
            }
        }

        return chosenIndex;
    }

    private boolean canFirstImageReplacedSecondImage(Bin copyOfSecondBin,  Rectangle secondImageChoosen) {
        boolean canFirstImageReplacedSecondImage = true;

        List<Rectangle> rectangles = copyOfSecondBin.getRectangles();

        for(Rectangle rectangle : rectangles) {
            if(secondImageChoosen.getPosition().getX() < rectangle.getPosition().getX() + rectangle.getDimension().getLX()
                    || secondImageChoosen.getPosition().getX() + secondImageChoosen.getDimension().getLX() > rectangle.getPosition().getX()) {
                if(rectangle.getPosition().getY() < secondImageChoosen.getPosition().getY()) {
                    if(secondImageChoosen.getPosition().getY() < rectangle.getPosition().getY() + rectangle.getDimension().getLY()
                            || secondImageChoosen.getPosition().getY() + secondImageChoosen.getDimension().getLY() < rectangle.getPosition().getY() + rectangle.getDimension().getLY()) {
                        canFirstImageReplacedSecondImage = false;
                    }
                } else {
                    canFirstImageReplacedSecondImage = false;
                }
            }
        }

        return canFirstImageReplacedSecondImage;
    }

    /**
     *
     * @param indexImageFirstBin
     * @param indexImageSecondBin
     * @    param firstBinIndex
     * @param secondBinIndex
     * @return
     */
    private Solution performSwitch(int indexImageFirstBin, int indexImageSecondBin, int firstBinIndex, int secondBinIndex) {
        Solution solution = new Solution(this.population.getIndividuals().get(0));
        Rectangle imageFirstBin = solution.getBins().get(firstBinIndex).getRectangles().get(indexImageFirstBin);
        Rectangle imageSecondBin = solution.getBins().get(secondBinIndex).getRectangles().get(indexImageSecondBin);
        Position secondImagePosition;
        /*Dimension dimensionFirstRectangle = this.getImagePlusFreeAreaDimension(imageFirstBin, firstBinIndex);
        Dimension dimensionSecondRectangle = this.getImagePlusFreeAreaDimension(imageSecondBin, secondBinIndex);
        imageFirstBin.setDimension(dimensionSecondRectangle);
        imageSecondBin.setDimension(dimensionFirstRectangle);*/
        secondImagePosition = imageSecondBin.getPosition();
        imageSecondBin.setPosition(imageFirstBin.getPosition());
        imageFirstBin.setPosition(secondImagePosition);

        return solution;
    }

    // Crossover individuals
    private Solution performCrossover(Solution firstIndividual, Solution secondIndividual) {
        Solution newSolution = new Solution();

        return newSolution;
    }

    // Mutate an individual
    private void mutate(Solution individual) {
        Random random = new Random();

        boolean mutation = new Random().nextInt(this.mutationProbabilityIndex)==0;

        if(mutation) {
            
        }
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

    /**
     *
     * @return
     */
    private Double solve(Solution solution) {
        // First, resolve simplex for the initial solution
        Simplex simplex = new Simplex(solution);

        simplex.solve();

        return  simplex.getPointValuePair().getValue();
    }
}
