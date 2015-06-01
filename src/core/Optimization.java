package core;

import models.*;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.*;

/**
 * Optimization class.
 * Implements genetic algorithm.
 *
 * @author Medric, Gabi, P-J
 * @version 1.0
 */
public class Optimization
{
    // GLOBAL PARAMETERS (INIT)
    private static final int GENESIS_REPETITIONS = 50;
    private static final int MUTATION_INC = 1;
    private static final int MUTATION_MAX = 10;
    private static final int MUTATION_INIT_VALUE = 2;
    private static final int CROSSOVER_NUMBER = 2;
    private static final int CROSSOVER_INIT_VALUE = 2;
    private static final int INIT_NB_IMAGES_SWITCHES = 2;

    private Population population;
    private int generationNumber;
    private int mutationProbabilityIndex;
    private int switchImageProbabilty;
    private Random random;
    private int numberOfImagesSwitches;

    /**
     * Constructor
     * @param generationNumber Number of generation chosen
     * @param size Size of population
     * @param adam First individual in first generation
     */
    public Optimization(int generationNumber, int size, Solution adam) throws Exception {
        adam.calculFitness();
        this.population = new Population(size, adam);
        this.generationNumber = generationNumber;

        this.random = new Random();

        this.mutationProbabilityIndex = MUTATION_INIT_VALUE; // Will give a 1/2 probability
        this.switchImageProbabilty = CROSSOVER_INIT_VALUE;

        this.numberOfImagesSwitches = INIT_NB_IMAGES_SWITCHES;
    }

    /**
     * Optimize Adam over generation
     * @return The best solution after n generation
     * @throws Exception
     */
    public Solution optimize() throws Exception {
        // Genesis
        this.genesis();

        // Evolution
        for (int i = 0; i < generationNumber; i++) {
            this.evolvePopulation(CROSSOVER_NUMBER);
        }

        // Apocalypse
        return this.apocalypse();
    }

    /**
     * Complete the first generation creating neighbors
     */
    private void genesis() throws Exception {
        int cpt = 0;
        int chosenBinId = 0;
        Solution newSolution = null;
        Rectangle image;
        Packing packing = new Packing();
        ArrayList<Rectangle> imagesToPlace;

        for(int i = 0; i < this.population.getPopulationSize(); i++) {
            // Choose random image of image to place
            image = this.population.getIndividuals().get(0).getApplication().get(random.nextInt(this.population.getIndividuals().get(0).getApplication().size() - 1));

            // GENESIS_REPETITIONS essay to add image to a random bin
            while (cpt < GENESIS_REPETITIONS) {
                chosenBinId = random.nextInt(this.population.getIndividuals().get(0).getBins().size() - 1);
                newSolution = this.addImageToBin(this.population.getIndividuals().get(0), packing, chosenBinId, image);

                cpt++;
            }

            //If after GENESIS_REPETITIONS we failed to add image we add new bin which contain chosen image to the new individual
            if(newSolution == null) {
                newSolution = new Solution(this.population.getIndividuals().get(0));
                newSolution.getBins().add(new Bin(this.population.getIndividuals().get(0).getBins().get(0)));
                imagesToPlace = (ArrayList<Rectangle>)((ArrayList<Rectangle>)this.population.getIndividuals().get(0).getApplication()).clone();
                packing.setRectangles(imagesToPlace);
                packing.getRectangles().add(image);
                newSolution = packing.pack();
            }

            // Copy list of image to place from adam to new individual
            newSolution.setApplication(this.population.getIndividuals().get(0).getApplication());
        }
    }

    /**
     * Evolves from genesis to apocalypse.
     */
    private void evolvePopulation(int numberOfCrossover) throws Exception {
        // Build next generation
        this.population = this.tournamentSelection();

        // Performs crossover
        for(int i = 0; i < numberOfCrossover; i++) {
            // Choose random first individual from the new generation for crossover
            Solution firstSolution = this.getRandomSolutionOverCurrentPopulation();
            Solution secondSolution;

            // second solution and first solution has to be different. While they are the same, we choose random second individual from the new generation for crossover
            while (firstSolution.equals(secondSolution = this.getRandomSolutionOverCurrentPopulation())) ;

            // Performs crossover
            this.performCrossover(firstSolution, secondSolution);
        }

        // Performs mutation
        this.mutate(this.getRandomSolutionOverCurrentPopulation());
    }

    /**
     * Get a random individual from actual population
     * @return Chosen solution
     */
    private Solution getRandomSolutionOverCurrentPopulation() {
        return this.population.getIndividuals().get(random.nextInt(this.population.getPopulationSize() - 1));
    }

    /**
     * Gets the best solution from the current population.
     * @return The best solution
     */
    private Solution apocalypse() {
        return this.population.getBestSolution();
    }

    /**
     * Switch 2 images between 2 patterns.
     * @param firstBinChosen First bin containing first image
     * @param secondBinChosen Second bin containing second image
     * @param firstBinIndex Index of first bin containing first image
     * @param secondBinIndex Index of second bin containing second image
     * @return solution with images switched
     */
    private Solution switchImage(Bin firstBinChosen, Bin secondBinChosen, int firstBinIndex, int secondBinIndex) throws Exception {
        Rectangle firstImageToSwitch;

        int indexOfSecondImageToSwitch;
        int indexOfImage;

        Solution newSolution = null;

        // Choose a random image from the first bin
        indexOfImage = random.nextInt(firstBinChosen.getRectangles().size());
        firstImageToSwitch = firstBinChosen.getRectangles().get(indexOfImage);

        // Get first image of the second bin that can match
        indexOfSecondImageToSwitch = this.getIndexSecondImage(firstImageToSwitch, secondBinChosen);

        // switch images between bin if second image found
        if (indexOfSecondImageToSwitch != 0) {
            newSolution = this.performSwitch(indexOfImage, indexOfSecondImageToSwitch, firstBinIndex, secondBinIndex);
        }

        return newSolution;
    }

    /**
     * Get first image of the second bin that can match
     * @param firstImage Random image chosen of the first bin
     * @param secondBinChosen Random bin which contain the second image to get
     * @return First image of the second bin that can match
     */
    private int getIndexSecondImage(Rectangle firstImage, Bin secondBinChosen) throws Exception {
        Rectangle secondImage = null;

        // This is a simulation, we can't modify a bin so we copy a bin to use it for the simulation
        Bin copySecondBinChosen = new Bin(secondBinChosen);
        boolean isSecondImageChosen = false;
        int numberOfImageInSecondBin = secondBinChosen.getRectangles().size();
        int chosenIndex = 0;
        int iterationNumber = 1;

        while (!isSecondImageChosen && iterationNumber < 500) {
            //Reinit list of rectangle of the copy of second bin because we modify a rectangle later
            copySecondBinChosen.setRectangles((ArrayList<Rectangle>) secondBinChosen.getRectangles());
            iterationNumber++;

            // Get a random image of the second bin
            chosenIndex = random.nextInt(numberOfImageInSecondBin);
            secondImage = copySecondBinChosen.getRectangles().get(chosenIndex);

            // If the second image chosen is the first image and we perform the switch the generated solution will be the initial solution. useless
            if(secondImage.getId() != firstImage.getId()) {

                // if first image is bigger than second image
                if (secondImage.compareTo(firstImage) > 0) {
                    secondImage.setDimension(firstImage.getDimension());

                    if (secondImage.getPosition().getX() + secondImage.getDimension().getLX() <= copySecondBinChosen.getDimension().getLX()
                            && secondImage.getPosition().getY() + secondImage.getDimension().getLY() <= copySecondBinChosen.getDimension().getLY()) {
                        isSecondImageChosen = this.canFirstImageReplacedSecondImage(copySecondBinChosen, secondImage);
                    }
                // else we can put second image instead of first image
                } else {
                    isSecondImageChosen = true;
                }

                // if we can't get an image after 500 iteration, index returned is 0
                if (!isSecondImageChosen && iterationNumber == 500) {
                    chosenIndex = 0;
                }
            }
        }

        return chosenIndex;
    }

    /**
     * Can an image be replaced by another image
     * @param copyOfSecondBin simulated second bin
     * @param secondImageChosen second image chosen
     * @return False if new image infringes on another, else true
     */
    private boolean canFirstImageReplacedSecondImage(Bin copyOfSecondBin,  Rectangle secondImageChosen) throws Exception {
        boolean canFirstImageReplacedSecondImage = true;

        List<Rectangle> rectangles = copyOfSecondBin.getRectangles();

        for(Rectangle rectangle : rectangles) {
            if(secondImageChosen.getPosition().getX() < rectangle.getPosition().getX() + rectangle.getDimension().getLX()
                    || secondImageChosen.getPosition().getX() + secondImageChosen.getDimension().getLX() > rectangle.getPosition().getX()) {
                if(rectangle.getPosition().getY() < secondImageChosen.getPosition().getY()) {
                    if(secondImageChosen.getPosition().getY() < rectangle.getPosition().getY() + rectangle.getDimension().getLY()
                            || secondImageChosen.getPosition().getY() + secondImageChosen.getDimension().getLY() < rectangle.getPosition().getY() + rectangle.getDimension().getLY()) {
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
     * Perform switch image
     * @param indexImageFirstBin Index of first chosen image in the first bin
     * @param indexImageSecondBin Index of second chosen image in the second bin
     * @param firstBinIndex Index of first bin chosen
     * @param secondBinIndex Index of second bin chosen
     * @return New solution which is old individual with images switched
     */
    private Solution performSwitch(int indexImageFirstBin, int indexImageSecondBin, int firstBinIndex, int secondBinIndex) throws Exception {
        Solution solution = new Solution(this.population.getIndividuals().get(0));
        Rectangle imageFirstBin = solution.getBins().get(firstBinIndex).getRectangles().get(indexImageFirstBin);
        Rectangle imageSecondBin = solution.getBins().get(secondBinIndex).getRectangles().get(indexImageSecondBin);
        Position secondImagePosition = null;

        // Put second image into first bin
        secondImagePosition = imageSecondBin.getPosition();
        imageSecondBin.setPosition(imageFirstBin.getPosition());
        solution.getBins().get(firstBinIndex).getRectangles().remove(imageFirstBin);
        solution.getBins().get(firstBinIndex).getRectangles().add(imageSecondBin);

        // Put first image into second bin
        imageFirstBin.setPosition(secondImagePosition);
        solution.getBins().get(secondBinIndex).getRectangles().remove(imageSecondBin);
        solution.getBins().get(secondBinIndex).getRectangles().add(imageFirstBin);

        return solution;
    }

    /**
     * Crossover individuals.
     * @param firstIndividual First individual chosen for crossover
     * @param secondIndividual Second individual chosen for crossover
     */
    private void performCrossover(Solution firstIndividual, Solution secondIndividual) throws Exception {
        // Select 2 bins
        Bin firstRandomBin = this.getRandomBin(firstIndividual);
        Bin secondRandomBin = this.getRandomBin(firstIndividual);

        firstIndividual.replaceBin(firstRandomBin, secondRandomBin);
        secondIndividual.replaceBin(secondRandomBin, firstRandomBin);

        if(!(allImagesArePresent(firstIndividual) && allImagesArePresent(secondIndividual))) {
            // reverse
            secondIndividual.replaceBin(secondRandomBin, firstRandomBin);
            secondIndividual.replaceBin(firstRandomBin, secondRandomBin);
        }

        // Switch d'images
        boolean imagesSwitch = random.nextInt(this.switchImageProbabilty)==0;

        if(imagesSwitch) {
            for(int i = 0; i < this.numberOfImagesSwitches; i++) {

                Bin alpha = this.getRandomBin(firstIndividual);
                Bin beta = this.getRandomBin(secondIndividual);

                this.switchImage(alpha, beta, firstIndividual.getBins().indexOf(alpha), firstIndividual.getBins().indexOf(beta));
            }
        }
    }

    /**
     *
     * @param solutionToCheck
     * @return
     */
    private boolean allImagesArePresent(Solution solutionToCheck) {
        ArrayList<int[]> vectors = solutionToCheck.getSolutionVectors();
        ArrayList<Integer> result = new ArrayList<Integer>();

        //Additionne tous les vecteurs
        for(int index = 0; index < solutionToCheck.getApplication().size(); index++) {
            int value = 0;

            for(int[] vector : vectors) {
                value += vector[index];

                result.add(value);
            }
        }

        return result.contains(0);
    }

    /**
     * Mutate an individual of the current population
     * @param individual Chosen individual
     * @throws Exception
     */
    private void mutate(Solution individual) throws Exception {
        boolean add;
        boolean mutation = random.nextInt(this.mutationProbabilityIndex)==0;
        boolean canFitImage = false;
        Rectangle image;
        Bin chosenBin;
        Packing packing = new Packing();
        Solution newSolution;
        int chosenBinId;

        if(mutation) {
            // Decides if we proceed to an add or delete action
            add = random.nextBoolean();

            // While add or delete action is not performed
            while (!canFitImage) {
                // Choose random image to add or delete
                image = individual.getApplication().get(random.nextInt(individual.getApplication().size() - 1));

                // from a random bin of the individual
                chosenBinId = random.nextInt(individual.getBins().size() - 1);
                chosenBin = individual.getBins().get(chosenBinId);

                if (add) {
                    newSolution = this.addImageToBin(individual, packing, chosenBinId, image);

                    //newSolution is null if we can't add image to the chosen bin
                    canFitImage = newSolution != null;

                    // If image is added to bin, bins of new solution are image of bins of individual plus add action performed
                    if(canFitImage) {
                        individual.setBins(newSolution.getBins());
                    }
                // delete action chosen
                } else {
                    chosenBin.getRectangles().remove(image);
                    chosenBin.getFreeRectangles().add(image);
                    canFitImage = true;
                }
            }

            // Update mutation probability for the next generation
            this.mutationProbabilityIndex = this.mutationProbabilityIndex < MUTATION_MAX ? this.mutationProbabilityIndex + MUTATION_INC : MUTATION_INIT_VALUE;
        }
    }

    /**
     * add a chosen image to a chosen bin
     * @param individual Original individual
     * @param packing packing object to apply guillotin algorithm
     * @param chosenBinId Id of chosen bin which will receive the image
     * @param image Chosen image to add
     * @return New solution which is original individual plus chosen image added
     * @throws Exception
     */
    private Solution addImageToBin(Solution individual, Packing packing, int chosenBinId, Rectangle image) throws Exception {
        Solution newSolution = new Solution(individual);
        Rectangle freeRectangle;
        Bin chosenBin = newSolution.getBins().get(chosenBinId);

        freeRectangle = packing.findFreeRectangle(chosenBin, image, null, 0, 0);

        if (freeRectangle != null) {
            //Add image
            image.setPosition(freeRectangle.getPosition());
            chosenBin.getRectangles().add(image);

            // Apply guillotin algorithm for the new free rectangle generated
            packing.split(chosenBin, image, freeRectangle);
        } else {
            newSolution = null;
        }

        return newSolution;
    }

    /**
     * Build the new generation from current generation
     * @return New generation of population
     * @throws Exception
     */
    private Population tournamentSelection() throws Exception {
        Population nextGeneration = new Population(this.population.getPopulationSize());
        CustomHashMap<Solution, Double> selection = new CustomHashMap<Solution, Double>();
        double totalFitness = this.population.getTotalFitness();
        double chosenNumber;

        // build pie chart for roulette wheel.
        for(Solution solution : this.population.getIndividuals()) {
            // We devide by nextGeneration.getPopulationSize() - 1 for the smallest fitness has the bigger part of pie chaart
            selection.put(solution,  (solution.getFitness() / totalFitness) / nextGeneration.getPopulationSize() - 1);
        }

        // Choose individual according to random number
        for (int i = 0; i < nextGeneration.getPopulationSize(); i++) {
            chosenNumber = random.nextDouble();
            nextGeneration.addIndividual(this.getSelectedSolutionByRouletteWheel(chosenNumber, selection));
        }

        return nextGeneration;
    }

    /**
     * Get solution which is selected by roulette wheel algorithm
     * @param chosenNumber Random number generated
     * @param selection pie chart
     * @return Solution which is selected by roulette wheel algorithm
     */
    private Solution getSelectedSolutionByRouletteWheel(double chosenNumber, CustomHashMap<Solution, Double> selection) throws Exception {
        Solution chosenSolution = null;
        double rate = 0;

        // While rate < chosenNumber
        for(int i = 0; i < selection.keySet().size(); i++) {
            rate += selection.get(selection.indexOf(i)).doubleValue();

            // If new rate > chosenNumber, we have found the selected individual
            if(rate >= chosenNumber && chosenSolution == null) {
                chosenSolution = selection.indexOf(i);
                break;
            }
        }

        return chosenSolution;
    }

    /**
     * Returns a random pattern from a given solution.
     * @param solution
     * @return
     * @throws Exception
     */
    private Bin getRandomBin(Solution solution) throws Exception {
        return solution.getBins().get(random.nextInt(solution.getBins().size() - 1));
    }
}
