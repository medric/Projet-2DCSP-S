package core;

import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by Medric on 10/05/2015.
 */
public class Simplex {
    SimplexSolver solver;
    Collection<LinearConstraint> constraints;
    Solution solution;
    private PointValuePair pointValuePair;

    LinearObjectiveFunction f;

    /**a
     * Constructor.
     *
     * @param solution
     */
    public Simplex(Solution solution) throws Exception {
        this.solver = new SimplexSolver();
        this.constraints = new ArrayList<LinearConstraint>();
        this.solution = solution;

        // Init
        this.setUpConstraints();
        this.setUpFitness();
    }

    /**
     * Sets up the constraints for a given solution.
     */
    private void setUpConstraints() throws Exception {
        ArrayList<int[]> solutionVectors =  this.solution.getSolutionVectors();
        int size = solutionVectors.size();

        for(int i = 0; i < this.solution.getApplication().size(); i++) {
            double[] coefficients = new double[size];

            for (int j = 0; j < size; j++) {
                double value = solutionVectors.get(j)[i];

                coefficients[j] = value;
            }

            try {
                this.constraints.add(new LinearConstraint(coefficients,
                        Relationship.GEQ, this.solution.getApplication().get(i).getQuantity()));
            }catch(NullPointerException ex) {
                System.out.println();
            }
        }
    }

    /**
     *  Sets up the function to minimize for the given solution.
     */
    private void setUpFitness() throws Exception {
        double cost = this.solution.getBins().get(0).getCost();
        int nbOfPatterns = this.solution.getBins().size();

        double constantTerm = (nbOfPatterns * cost);

        // x1 ... xn define the number of patterns to print
        double[] coefficients = new double[nbOfPatterns];
        System.out.println(nbOfPatterns);
        for(int i = 0; i < nbOfPatterns; i++) {
            coefficients[i] = 1;
        }

        this.f = new LinearObjectiveFunction(coefficients, constantTerm);
    }

    /**
     *  Stores data and performs the optimization. The solution is stored in this.pointValuePair attribute.
     */
    public void solve() throws TooManyIterationsException, UnboundedSolutionException, NoFeasibleSolutionException {
        // Optimize given the current linear objective function and previously set constraints
        try {
            this.setPointValuePair(this.solver.optimize(f, new LinearConstraintSet(this.constraints), GoalType.MINIMIZE, new NonNegativeConstraint(true), PivotSelectionRule.BLAND));
        }catch(NoFeasibleSolutionException ex) {
            ex.printStackTrace();
        }
    }

    public PointValuePair getPointValuePair() {
        return pointValuePair;
    }

    public void setPointValuePair(PointValuePair pointValuePair) {
        this.pointValuePair = pointValuePair;
    }
}
