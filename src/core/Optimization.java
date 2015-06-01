package core;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import models.*;
import org.w3c.dom.css.Rect;

import java.util.*;

/**
 * Created by Epulapp on 26/05/2015.
 */
public class Optimization
{
    private static final int GENESIS_REPTITION = 50;
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
        firstIndividual.calculFitness();
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
    public Solution optimize() {
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
     *
     */
    private void genesis() {
        int cpt = 0;
        int chosenBinId = 0;
        Solution newSolution = null;
        Rectangle image;
        Random random = new Random();
        Packing packing = new Packing();
        ArrayList<Rectangle> imagesToPlace;

        for(int i = 0; i < this.population.getPopulationSize(); i++) {
            image = this.population.getIndividuals().get(0).getApplication().get(random.nextInt(this.population.getIndividuals().get(0).getApplication().size() - 1));
            while (cpt < GENESIS_REPTITION) {
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

        /*Solution firstIndividual = this.population.getIndividuals().get(0);
        Random random = new Random();
        CustomHashMap<Integer, ArrayList<Integer>> combination = new CustomHashMap<Integer, ArrayList<Integer>>();
        Solution newSolution;
        int size = 0;

        int numberOfBinInFirstSolution = firstIndividual.getBins().size();
        int firstBinChosen;
        int secondBinChosen;

        // Faire toutes les combinaisons possibles
        for (int i = 1; i <= numberOfBinInFirstSolution; i++) {
            combination.put(i, new ArrayList<Integer>());

            for (int j = i + 1; j <= numberOfBinInFirstSolution; j++) {
                combination.get(i).add(j);
            }
        }

        // Tant que la population n'est pas entiÃ¨re
        for (int i = 1; i < this.population.getPopulationSize() - 1; i++) {
            newSolution = null;

            while (newSolution == null) {
                // Tirer au sort une combinaison
                firstBinChosen = random.nextInt(numberOfBinInFirstSolution - 1) + 1;
                if (combination.containsKey(firstBinChosen)) {
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

                if(newSolution != null) {
                    newSolution.calculFitness();
                    newSolution.setApplication(this.population.getIndividuals().get(0).getApplication());
                    this.population.addIndividual(newSolution);
                }
            }
        }*/
    }

    /**
     *
     */
    private void evolvePopulation() {
        Random random = new Random();

        this.population = this.tournamentSelection();

        this.performCrossover();

        this.mutate(this.population.getIndividuals().get(random.nextInt(this.population.getPopulationSize() - 1)));
    }

    /**
     *
     */
    private Solution apocalypse() {
        // Renvoyer la meilleure solution de la dernière génération.
        return this.population.getBestSolution();
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

    // Crossover individuals
    private Solution performCrossover(Solution firstIndividual, Solution secondIndividual) {
        Solution newSolution = new Solution();

        return newSolution;
    }

    // Mutate an individual
    private void mutate(Solution individual) {
        Random random = new Random();
        boolean add;
        boolean mutation = new Random().nextInt(this.mutationProbabilityIndex)==0;
        boolean canFitImage = false;
        Rectangle image;
        Bin chosenBin;
        Packing packing = new Packing();
        Solution newSolution;
        int chosenBinId;

        if(mutation) {
            add = random.nextBoolean();

            while (!canFitImage) {
                image = individual.getApplication().get(random.nextInt(individual.getApplication().size() - 1));
                chosenBinId = random.nextInt(individual.getBins().size() - 1);
                chosenBin = individual.getBins().get(chosenBinId);

                if (add) {
                    newSolution = this.addImageToBin(individual, packing, chosenBinId, image);
                    canFitImage = newSolution != null;
                } else {
                    chosenBin.getRectangles().remove(image);
                    chosenBin.getFreeRectangles().add(image);
                    canFitImage = true;
                }
            }
        }
    }

    private Solution addImageToBin(Solution individual, Packing packing, int chosenBinId, Rectangle image) {
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

    private Population tournamentSelection() {
        Population nextGeneration = new Population(this.population.getPopulationSize());
        CustomHashMap<Solution, Double> selection = new CustomHashMap<>();
        double totalFitness = this.population.getTotalFitness();
        Random random = new Random();
        double chosenNumber = 0;
        Rectangle rectangle = null;

        for(Solution solution : this.population.getIndividuals()) {
            selection.put(solution,  (solution.getFitness() / totalFitness) / nextGeneration.getPopulationSize() - 1);
        }

        for (int i = 0; i < nextGeneration.getPopulationSize(); i++) {
            chosenNumber = random.nextDouble();
            nextGeneration.addIndividual(this.getSolution(chosenNumber, selection));
        }

        return  nextGeneration;
    }

    private Solution getSolution(double chosenNumber, CustomHashMap<Solution, Double> selection) {
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
