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

        int size;

        // Tant que la population n'est pas entière
        for (int i = 1; i < this.population.getPopulationSize(); i++) {
            newSolution = null;

            while (newSolution == null) {
                // Tirer au sort une combinaison
                firstBinChosen = random.nextInt(numberOfBinInFirstSolution - 1) + 1;
                size = combination.get(firstBinChosen).size() - 1;
                secondBinChosen = size == 0 ? 0 : random.nextInt(size);

                //Appeler SwitchImage
                newSolution = this.switchImage(firstIndividual.getBins().get(firstBinChosen), firstIndividual.getBins().get(secondBinChosen), firstBinChosen, secondBinChosen);

                this.population.addIndividual(newSolution);

                // L'enlever de combination
                combination.get(combination.indexOf(firstBinChosen)).remove(combination.get(firstBinChosen).get(secondBinChosen));

                if (combination.get(firstBinChosen).isEmpty()) {
                    combination.remove(firstBinChosen);
                    numberOfBinInFirstSolution = combination.size();
                }
            }
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
        // Méthode tabou
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

        // Prend la première image qui peut correspondre
        indexOfSecondImageToSwitch = this.getIndexSecondImage(firstImageToSwitch, secondBinChosen);

        //switch dans les patterns
        if (indexOfSecondImageToSwitch != 0) {
            this.performSwitch(indexOfImage, indexOfSecondImageToSwitch, firstBinIndex, secondBinIndex);
        }

        return newSolution;
    }

    /**
     *
     * @param firstImage
     * @param secondBinChosen
     * @return
     */
    private int getIndexSecondImage(Rectangle firstImage, Bin secondBinChosen) {
        Random random = new Random();
        boolean isSecondImageChosen = false;
        int numberOfImageInSecondBin = secondBinChosen.getRectangles().size();
        int chosenIndex = 0;
        int iterationNumber = 0;

        Dimension imagePlusFreeAreaDimension;

        while (!isSecondImageChosen && iterationNumber < 500) {
            iterationNumber++;
            chosenIndex = random.nextInt(numberOfImageInSecondBin);

            imagePlusFreeAreaDimension = this.getImagePlusFreeAreaDimension(secondBinChosen.getRectangles().get(chosenIndex), chosenIndex);

            if (firstImage.getDimension().getLY() <= imagePlusFreeAreaDimension.getLY()
                    && firstImage.getDimension().getLX() <= imagePlusFreeAreaDimension.getLX()) {
                imagePlusFreeAreaDimension = this.getImagePlusFreeAreaDimension(firstImage, chosenIndex);
                if (firstImage.getDimension().getLY() <= imagePlusFreeAreaDimension.getLY()
                        && firstImage.getDimension().getLX() <= imagePlusFreeAreaDimension.getLX()) {
                    isSecondImageChosen = true;
                }
            }
        }

        return chosenIndex;
    }

    /**
     *
     * @param chosenRectangle
     * @param indexOfSecondChosenBin
     * @return
     */
    private Dimension getImagePlusFreeAreaDimension(Rectangle chosenRectangle, int indexOfSecondChosenBin) {
        Rectangle freeRectangle;
        ArrayList<Rectangle> freeRectanglesX = new ArrayList<Rectangle>();
        ArrayList<Rectangle> freeRectanglesY = new ArrayList<Rectangle>();
        Bin pattern = this.population.getIndividuals().get(0).getBins().get(indexOfSecondChosenBin);
        Iterator<Rectangle> iterator = pattern.getFreeRectangles().iterator();
        Dimension imagePlusFreeAreaDimension = new Dimension(0, 0);

        while (iterator.hasNext()) {
            freeRectangle = iterator.next();

            if (freeRectangle.getPosition().getY() == chosenRectangle.getPosition().getY()) {
                if ((freeRectangle.getDimension().getLX() + freeRectangle.getPosition().getX() == chosenRectangle.getPosition().getX()
                        || chosenRectangle.getDimension().getLX() + chosenRectangle.getPosition().getX() == freeRectangle.getPosition().getX())
                        && (freeRectangle.getDimension().getLY() >= chosenRectangle.getDimension().getLY())) {
                    freeRectanglesX.add(freeRectangle);
                } else if ((freeRectangle.getDimension().getLY() + freeRectangle.getPosition().getY() == chosenRectangle.getPosition().getY()
                        || chosenRectangle.getDimension().getLY() + chosenRectangle.getPosition().getY() == freeRectangle.getPosition().getY())
                        && (freeRectangle.getDimension().getLX() >= chosenRectangle.getDimension().getLX())) {
                    freeRectanglesY.add(freeRectangle);
                }
            }
        }

        for (Rectangle rectangle : freeRectanglesX) {
            imagePlusFreeAreaDimension.setLX(imagePlusFreeAreaDimension.getLX() + rectangle.getDimension().getLX());
        }

        for (Rectangle rectangle : freeRectanglesY) {
            imagePlusFreeAreaDimension.setLY(imagePlusFreeAreaDimension.getLY() + rectangle.getDimension().getLY());
        }

        return imagePlusFreeAreaDimension;
    }

    /**
     *
     * @param indexImageFirstBin
     * @param indexImageSecondBin
     * @param firstBinIndex
     * @param secondBinIndex
     * @return
     */
    private Solution performSwitch(int indexImageFirstBin, int indexImageSecondBin, int firstBinIndex, int secondBinIndex) {
        Solution solution = new Solution(this.population.getIndividuals().get(0));
        Rectangle imageFirstBin = solution.getBins().get(firstBinIndex).getRectangles().get(indexImageFirstBin);
        Rectangle imageSecondBin = solution.getBins().get(secondBinIndex).getRectangles().get(indexImageSecondBin);
        Position secondImagePosition;
        Dimension dimensionFirstRectangle = this.getImagePlusFreeAreaDimension(imageFirstBin, firstBinIndex);
        Dimension dimensionSecondRectangle = this.getImagePlusFreeAreaDimension(imageSecondBin, secondBinIndex);
        imageFirstBin.setDimension(dimensionSecondRectangle);
        imageSecondBin.setDimension(dimensionFirstRectangle);
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
