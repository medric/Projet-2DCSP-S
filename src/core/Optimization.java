package core;

import models.*;

import java.util.*;

/**
 * Created by Epulapp on 26/05/2015.
 */
public class Optimization
{
    private static final int GENESIS_REPETITIONS = 50;
    private static final int MUTATION_INC = 1;
    private static final int MUTATION_MAX = 10;
    private static final int MUTATION_INIT_VALUE = 2;

    private Population population;
    private int generationNumber;
    private int mutationProbabilityIndex;
    private Random random;

    /**
     *
     * @param generationNumber
     * @param size
     * @param firstIndividual
     */
    public Optimization(int generationNumber, int size, Solution firstIndividual) throws Exception {
        firstIndividual.calculFitness();
        this.population = new Population(size, firstIndividual);
        this.generationNumber = generationNumber;

        this.random = new Random();

        // Default, might change
        this.mutationProbabilityIndex = MUTATION_INIT_VALUE; // Will give a 1/2 probability
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
     * Optimizes.
     */
    public Solution optimize() throws Exception {
        // Genesis
        this.genesis();

        // Evolution
        for (int i = 0; i < generationNumber; i++) {
            this.evolvePopulation();
        }

        // Apocalypse
        return this.apocalypse();
    }

    /**
     * Complete the first generation
     */
    private void genesis() throws Exception {
        int cpt = 0;
        int chosenBinId = 0;
        Solution newSolution = null;
        Rectangle image;
        Packing packing = new Packing();
        ArrayList<Rectangle> imagesToPlace;

        for(int i = 0; i < this.population.getPopulationSize(); i++) {
            image = this.population.getIndividuals().get(0).getApplication().get(random.nextInt(this.population.getIndividuals().get(0).getApplication().size() - 1));

            while (cpt < GENESIS_REPETITIONS) {
                chosenBinId = random.nextInt(this.population.getIndividuals().get(0).getBins().size() - 1);
                newSolution = this.addImageToBin(this.population.getIndividuals().get(0), packing, chosenBinId, image);

                cpt++;
            }

            if(newSolution == null) {
                newSolution = new Solution(this.population.getIndividuals().get(0));
                newSolution.getBins().add(new Bin(this.population.getIndividuals().get(0).getBins().get(0)));
                imagesToPlace = (ArrayList<Rectangle>)((ArrayList<Rectangle>)this.population.getIndividuals().get(0).getApplication()).clone();
                packing.setRectangles(imagesToPlace);
                packing.getRectangles().add(image);
                newSolution = packing.pack();
            }

            newSolution.setApplication(this.population.getIndividuals().get(0).getApplication());
        }
    }

    /**
     * Evolves from genesis to apocalypse.
     */
    private void evolvePopulation() throws Exception {
        this.population = this.tournamentSelection();

        // Performs crossover
        Solution firstSolution = this.getRandomSolutionOverCurrentPopulation();
        Solution secondSolution;

        // FirstSolution has to be different
        while(firstSolution.equals(secondSolution = this.getRandomSolutionOverCurrentPopulation()));

        this.performCrossover(firstSolution, secondSolution);

        // Performs mutation
        this.mutate(this.getRandomSolutionOverCurrentPopulation());
    }

    private Solution getRandomSolutionOverCurrentPopulation() {
        return this.population.getIndividuals().get(random.nextInt(this.population.getPopulationSize() - 1));
    }

    /**
     * Gets the best solution.
     * @return Solution
     */
    private Solution apocalypse() {
        // Renvoyer la meilleure solution de la dernière génération.
        return this.population.getBestSolution();
    }

    /**
     * Switch 2 images between 2 patterns.
     * @param firstBinChosen
     * @param secondBinChosen
     * @param firstBinIndex
     * @param secondBinIndex
     * @return
     */
    private Solution switchImage(Bin firstBinChosen, Bin secondBinChosen, int firstBinIndex, int secondBinIndex) throws Exception {
        Rectangle firstImageToSwitch;

        int indexOfSecondImageToSwitch;
        int indexOfImage;

        Solution newSolution = null;

        // Choisis une image dans le premier BIN
        indexOfImage = random.nextInt(firstBinChosen.getRectangles().size());
        firstImageToSwitch = firstBinChosen.getRectangles().get(indexOfImage);

        // Prend la première image qui peut correspondre
        indexOfSecondImageToSwitch = this.getIndexSecondImage(firstImageToSwitch, firstBinChosen, secondBinChosen);

        // switch dans les patterns
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
    private int getIndexSecondImage(Rectangle firstImage, Bin firstBinChosen, Bin secondBinChosen) throws Exception {
        Rectangle secondImage = null;
        Bin copySecondBinChoosen = new Bin(secondBinChosen);
        copySecondBinChoosen.setRectangles((ArrayList<Rectangle>) secondBinChosen.getRectangles());
        Bin copyFirstBinChoosen = null;
        boolean isSecondImageChosen = false;
        int numberOfImageInSecondBin = copySecondBinChoosen.getRectangles().size();
        int chosenIndex = 0;
        int iterationNumber = 1;

        while (!isSecondImageChosen && iterationNumber < 500) {
            copySecondBinChoosen.setRectangles((ArrayList<Rectangle>) secondBinChosen.getRectangles());
            iterationNumber++;
            chosenIndex = random.nextInt(numberOfImageInSecondBin);

            secondImage = copySecondBinChoosen.getRectangles().get(chosenIndex);

            if(secondImage.getId() != firstImage.getId()) {

                if (secondImage.compareTo(firstImage) > 0) {
                    secondImage.setDimension(firstImage.getDimension());

                    if (secondImage.getPosition().getX() + secondImage.getDimension().getLX() <= copySecondBinChoosen.getDimension().getLX()
                            && secondImage.getPosition().getY() + secondImage.getDimension().getLY() <= copySecondBinChoosen.getDimension().getLY()) {
                        isSecondImageChosen = this.canFirstImageReplacedSecondImage(copySecondBinChoosen, secondImage);
                    }
                } else {
                    isSecondImageChosen = true;
                }

                if (!isSecondImageChosen && iterationNumber == 500) {
                    chosenIndex = 0;
                }
            }
        }

        return chosenIndex;
    }

    /**
     *
     * @param copyOfSecondBin
     * @param secondImageChoosen
     * @return
     */
    private boolean canFirstImageReplacedSecondImage(Bin copyOfSecondBin,  Rectangle secondImageChoosen) throws Exception {
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
    private Solution performSwitch(int indexImageFirstBin, int indexImageSecondBin, int firstBinIndex, int secondBinIndex) throws Exception {
        Solution solution = new Solution(this.population.getIndividuals().get(0));
        Rectangle imageFirstBin = solution.getBins().get(firstBinIndex).getRectangles().get(indexImageFirstBin);
        Rectangle imageSecondBin = solution.getBins().get(secondBinIndex).getRectangles().get(indexImageSecondBin);
        Position secondImagePosition = null;

        // Image2 dans rectangle 1
        secondImagePosition = imageSecondBin.getPosition();
        imageSecondBin.setPosition(imageFirstBin.getPosition());
        solution.getBins().get(firstBinIndex).getRectangles().remove(imageFirstBin);
        solution.getBins().get(firstBinIndex).getRectangles().add(imageSecondBin);

        // Image1 dans rectangle 2
        imageFirstBin.setPosition(secondImagePosition);
        solution.getBins().get(secondBinIndex).getRectangles().remove(imageSecondBin);
        solution.getBins().get(secondBinIndex).getRectangles().add(imageFirstBin);

        return solution;
    }

    /**
     * Crossover individuals.
     *
     * @param firstIndividual
     * @param secondIndividual
     * @return
     */
    private void performCrossover(Solution firstIndividual, Solution secondIndividual) throws Exception {
        // Select 2 bins
        Bin firstRandomBin = firstIndividual.getBins().get(random.nextInt(firstIndividual.getBins().size() - 1));
        Bin secondRandomBin = firstIndividual.getBins().get(random.nextInt(firstIndividual.getBins().size() - 1));

        firstIndividual.replaceBin(firstRandomBin, secondRandomBin);
        secondIndividual.replaceBin(secondRandomBin, firstRandomBin);
    }

    // Mutate an individual
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

            while (!canFitImage) {
                image = individual.getApplication().get(random.nextInt(individual.getApplication().size() - 1));
                chosenBinId = random.nextInt(individual.getBins().size() - 1);
                chosenBin = individual.getBins().get(chosenBinId);

                if (add) {
                    newSolution = this.addImageToBin(individual, packing, chosenBinId, image);
                    canFitImage = newSolution != null;

                    if(canFitImage) {
                        individual.setBins(newSolution.getBins());
                    }
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
     *
     * @param individual
     * @param packing
     * @param chosenBinId
     * @param image
     * @return
     * @throws Exception
     */
    private Solution addImageToBin(Solution individual, Packing packing, int chosenBinId, Rectangle image) throws Exception {
        Solution newSolution = new Solution(individual);
        Rectangle freeRectangle;
        Bin chosenBin = newSolution.getBins().get(chosenBinId);

        freeRectangle = packing.findFreeRectangle(chosenBin, image, null, 0, 0);

        if (freeRectangle != null) {
            //Ajouter l'image
            image.setPosition(freeRectangle.getPosition());
            chosenBin.getRectangles().add(image);

            //Calculer les nouveaux freeRectangles, les ajouter et supprimer le freeRectangle d'origine
            packing.split(chosenBin, image, freeRectangle);
        } else {
            newSolution = null;
        }

        return newSolution;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    private Population tournamentSelection() throws Exception {
        Population nextGeneration = new Population(this.population.getPopulationSize());
        CustomHashMap<Solution, Double> selection = new CustomHashMap<Solution, Double>();
        double totalFitness = this.population.getTotalFitness();
        double chosenNumber;

        for(Solution solution : this.population.getIndividuals()) {
            selection.put(solution,  (solution.getFitness() / totalFitness) / nextGeneration.getPopulationSize() - 1);
        }

        for (int i = 0; i < nextGeneration.getPopulationSize(); i++) {
            chosenNumber = random.nextDouble();
            nextGeneration.addIndividual(this.getSolution(chosenNumber, selection));
        }

        return  nextGeneration;
    }

    /**
     *
     * @param chosenNumber
     * @param selection
     * @return
     */
    private Solution getSolution(double chosenNumber, CustomHashMap<Solution, Double> selection) throws Exception {
        Solution chosenSolution = null;
        double rate = 0;

        for(int i = 0; i < selection.keySet().size(); i++) {
            rate += selection.get(selection.indexOf(i)).doubleValue();

            if(rate >= chosenNumber && chosenSolution == null) {
                chosenSolution = selection.indexOf(i);
                break;
            }
        }

        return chosenSolution;
    }
}
