package core;

import models.*;
import org.apache.commons.math3.optim.PointValuePair;

import java.util.logging.*;

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
    private static final int INIT_NB_IMAGES_SWITCHES = 1;
    private static final int NUMBER_OF_ITERATIONS = 500;
    private static final Logger LOGGER = Logger.getLogger(Optimization.class.getName());

    private Population population;
    private int generationNumber;
    private int mutationProbabilityIndex;
    private int switchImageProbability;
    private Random random;
    private int numberOfImagesSwitches;
    private FileHandler fileHandler;
    private int currentGenerationIndex;

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
        this.currentGenerationIndex = 1;

        this.mutationProbabilityIndex = MUTATION_INIT_VALUE; // Will give a 1/2 probability
        this.switchImageProbability = CROSSOVER_INIT_VALUE;

        this.numberOfImagesSwitches = INIT_NB_IMAGES_SWITCHES;

        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler = new FileHandler("./Optimizations.log");

        LOGGER.addHandler(fileHandler);
        fileHandler.setFormatter(formatter);
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
            this.currentGenerationIndex++;

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
        int retry = 0;
        int chosenBinId;
        Solution newSolution;
        Rectangle image;
        Packing packing = new Packing();
        ArrayList<Rectangle> imagesToPlace;

        for(int i = this.population.getIndividuals().size(); i < this.population.getPopulationSize(); i++) {
            newSolution = null;

            // Choose random image of image to place
            image = new Rectangle(this.population.getIndividuals().get(0).getApplication().get(random.nextInt(this.population.getIndividuals().get(0).getApplication().size())));

            // GENESIS_REPETITIONS essay to add image to a random bin
            while (cpt < GENESIS_REPETITIONS) {
                chosenBinId = random.nextInt(this.population.getIndividuals().get(0).getBins().size());
                newSolution = this.addImageToBin(this.population.getIndividuals().get(0), packing, chosenBinId, image);

                cpt++;
            }

            //If after GENESIS_REPETITIONS we failed to add image we add new bin which contain chosen image to the new individual
            if(newSolution == null) {
                newSolution = new Solution(this.population.getIndividuals().get(0));
                newSolution.getBins().add(new Bin(this.population.getIndividuals().get(0).getBins().get(0)));
                imagesToPlace = this.copyListOfImages(this.population.getIndividuals().get(0).getApplication());
                packing.setRectangles(imagesToPlace);
                packing.getRectangles().add(image);
                packing.setSolution(newSolution);
                newSolution = packing.pack();
            }

            // Copy list of image to place from adam to new individual
            newSolution.setApplication(this.population.getIndividuals().get(0).getApplication());

            if(newSolution.calculFitness()) {
                this.population.addIndividual(newSolution);
            } else {
                retry++;
                if(retry <= NUMBER_OF_ITERATIONS) {
                    i--;
                }
            }
        }
    }

    private ArrayList<Rectangle> copyListOfImages(List<Rectangle> origin) {
        ArrayList<Rectangle> copy = new ArrayList<Rectangle>();
        Rectangle newRectangle;

        for(Rectangle image : origin) {
            newRectangle = new Rectangle(image.getDimension(), image.getQuantity());
            newRectangle.setPosition(new Position(0,0));
            newRectangle.setId(image.getId());
            copy.add(newRectangle);
        }

        return copy;
    }

    /**
     * Evolves from genesis to apocalypse.
     */
    private void evolvePopulation(int numberOfCrossover) throws Exception {
        // Build next generation
        this.population = this.tournamentSelection();

        // Performs crossover
        while(this.population.getIndividuals().size() < this.population.getPopulationSize()) {
        //for(int i = this.population.getIndividuals().size(); i < this.population.getPopulationSize(); i++) {
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
    private Solution getRandomSolutionOverCurrentPopulation() throws Exception {
        return this.population.getIndividuals().get(random.nextInt(this.population.getIndividuals().size()));
    }

    /**
     * Gets the best solution from the current population.
     * @return The best solution
     */
    private Solution apocalypse() {
        this.logPopulation();
        fileHandler.close();

        return this.population.getBestSolution();
    }

    /**
     * Switch 2 images between 2 patterns.
     * @param firstBinChosen First bin containing first image
     * @param secondBinChosen Second bin containing second image
     */
    private void switchImage(Bin firstBinChosen, Bin secondBinChosen) throws Exception {
        Rectangle firstImageToSwitch;

        int indexOfSecondImageToSwitch;
        int indexOfImage;

        // Choose a random image from the first bin
        indexOfImage = random.nextInt(firstBinChosen.getRectangles().size());
        firstImageToSwitch = firstBinChosen.getRectangles().get(indexOfImage);

        // Get first image of the second bin that can match
        indexOfSecondImageToSwitch = this.getIndexSecondImage(firstImageToSwitch, secondBinChosen);

        // switch images between bin if second image found
        if (indexOfSecondImageToSwitch != 0) {
            this.performSwitch(indexOfImage, indexOfSecondImageToSwitch, firstBinChosen, secondBinChosen);
        }
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

        while (!isSecondImageChosen && iterationNumber < NUMBER_OF_ITERATIONS) {
            //Reinit list of rectangle of the copy of second bin because we modify a rectangle later
            copySecondBinChosen.setRectangles(secondBinChosen.getRectangles());
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
     * @param firstBinChosen First bin chosen
     * @param secondBinChosen Second bin chosen
     * @return New solution which is old individual with images switched
     */
    private void performSwitch(int indexImageFirstBin, int indexImageSecondBin, Bin firstBinChosen, Bin secondBinChosen) throws Exception {
        Rectangle imageFirstBin = null;
        Rectangle imageSecondBin = null;

        imageFirstBin = firstBinChosen.getRectangles().get(indexImageFirstBin);
        imageSecondBin = secondBinChosen.getRectangles().get(indexImageSecondBin);
        Position secondImagePosition = null;

        // Put second image into first bin
        secondImagePosition = imageSecondBin.getPosition();
        imageSecondBin.setPosition(imageFirstBin.getPosition());
        firstBinChosen.getRectangles().remove(imageFirstBin);
        firstBinChosen.getRectangles().add(imageSecondBin);

        // Put first image into second bin
        imageFirstBin.setPosition(secondImagePosition);
        secondBinChosen.getRectangles().remove(imageSecondBin);
        secondBinChosen.getRectangles().add(imageFirstBin);
    }

    /**
     * Crossover individuals.
     * @param firstIndividual First individual chosen for crossover
     * @param secondIndividual Second individual chosen for crossover
     */
    private void performCrossover(Solution firstIndividual, Solution secondIndividual) throws Exception {
        Solution children = null;
        Bin firstRandomBin;
        Bin secondRandomBin;
        Bin firstRandomBinSwitchImage;
        Bin secondRandomBinSwitchImage;
        boolean performCrossover = true;
        boolean firstIndividu = false;

        for(int j = 0; j < CROSSOVER_NUMBER; j++){
            firstIndividu = !firstIndividu;

            for (int l = 0; l < CROSSOVER_NUMBER; l++) {
                children = new Solution(firstIndividu ? firstIndividual : secondIndividual);

                // Select 2 bins
                firstRandomBin = this.getRandomBin(firstIndividual);
                secondRandomBin = this.getRandomBin(secondIndividual);

                //Switch bins
                children.replaceBin(firstRandomBin, secondRandomBin);

                //Reverse
                if (!this.allImagesArePresent(children)) {
                    children.replaceBin(secondRandomBin, firstRandomBin);
                    performCrossover = false;
                }

                // Switch images
               /* boolean imagesSwitch = random.nextInt(this.switchImageProbability) == 0;

                if (imagesSwitch) {
                    for (int i = 0; i < this.numberOfImagesSwitches; i++) {
                        firstRandomBinSwitchImage = this.getRandomBin(children);
                        secondRandomBinSwitchImage = this.getRandomBin(secondIndividual);

                        this.switchImage(firstRandomBin, secondRandomBin);

                        //Reverse
                        if (!this.allImagesArePresent(children)) {
                            this.switchImage(firstRandomBin, secondRandomBin);
                        } else {
                            performCrossover = true;
                        }
                    }
                }*/
            }

            if(performCrossover && children != null && this.allImagesArePresent(children)) {
                if(children.calculFitness()) {
                    this.population.addIndividual(children);
                }
            }

            if(this.population.getIndividuals().size() == this.population.getPopulationSize()) {
                j = CROSSOVER_NUMBER;
            }
        }
    }

    /**
     *
     * @param solutionToCheck
     * @return
     */
    private boolean allImagesArePresent(Solution solutionToCheck) {
        ArrayList<Integer> imagesIndex = new ArrayList<Integer>();

        for(Bin bin : solutionToCheck.getBins()) {
            for(Rectangle image : bin.getRectangles()) {
                if(!imagesIndex.contains(image.getId())) {
                    imagesIndex.add(image.getId());
                }
            }
        }

        return imagesIndex.size() == solutionToCheck.getApplication().size();
    }

    /**
     * Mutate an individual of the current population
     * @param individual Chosen individual
     * @throws Exception
     */
    private void mutate(Solution individual) throws  Exception {
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
                image = individual.getApplication().get(random.nextInt(individual.getApplication().size()));

                // from a random bin of the individual
                chosenBinId = random.nextInt(individual.getBins().size());
                chosenBin = individual.getBins().get(chosenBinId);

                if (add) {
                    Rectangle newRectangle = new Rectangle(image);
                    newSolution = this.addImageToBin(individual, packing, chosenBinId, newRectangle);

                    //newSolution is null if we can't add image to the chosen bin
                    canFitImage = newSolution != null;

                    // If image is added to bin, bins of new solution are image of bins of individual plus add action performed
                    if(canFitImage) {
                        individual.setBins(newSolution.getBins());
                    }

                // delete action chosen
                } else {
                    chosenBin.getRectangles().remove(image);

                    if (this.allImagesArePresent(individual)) {
                        chosenBin.getFreeRectangles().add(image);
                        canFitImage = true;
                    } else {
                        chosenBin.getRectangles().add(image);
                        canFitImage = false;
                    }
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
        Solution newSolution;

        this.logPopulation();

        Population nextGeneration = new Population(this.population.getPopulationSize());

        this.buildSelection(nextGeneration);

        for(int i = 0; i < this.population.getPopulationSize() / 3; i++) {
            nextGeneration.addIndividual(this.population.getIndividuals().get(i));
        }

        return nextGeneration;
    }

    private SortedMap<Double, Solution> buildSelection(Population generation) {
        SortedMap<Double, Solution> selection = new TreeMap<Double, Solution>();

        double totalFitness = this.population.getTotalFitness();

        // build pie chart for roulette wheel.
        for(Solution solution : this.population.getIndividuals()) {
            // We devide by nextGeneration.getPopulationSize() - 1 for the smallest fitness has the bigger part of pie chaart
            selection.put(solution.getFitness(), solution);
        }

        return  selection;
    }

    private void logPopulation(){
        int cpt = 1;
        for(Solution individual : this.population.getIndividuals()) {
            LOGGER.info(String.format("Generation number %0$s", this.currentGenerationIndex));
            LOGGER.info(String.format("Number of bins : %0$s", individual.getBins().size()));
            for(int i = 0; i < individual.getSimplex().getPointValuePair().getKey().length; i++) {
                LOGGER.info(String.format("Nombre de fois qu'on doit imprimer le pattern %1$s : %2$s", cpt, individual.getSimplex().getPointValuePair().getKey()[i]));
                cpt++;
            }
            LOGGER.info(String.format("Fitness : %0$s\r\n", individual.getFitness()));
        }
    }

    /**
     * Returns a random pattern from a given solution.
     * @param solution
     * @return
     * @throws Exception
     */
    private Bin getRandomBin(Solution solution) throws Exception {
        return solution.getBins().get(random.nextInt(solution.getBins().size()));
    }
}
