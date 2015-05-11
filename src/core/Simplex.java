package core;

import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;


/**
 * Created by Medric on 10/05/2015.
 */
public class Simplex {

    SimplexSolver solver;
    Collection<LinearConstraint> constraints;
    Collection<Pattern> patterns;

    LinearObjectiveFunction f;

    public Simplex(Collection<Pattern> solution, Collection<Pattern> patterns) {
        this.solver = new SimplexSolver();
        this.constraints = new ArrayList<LinearConstraint>();
    }

        // TODO; identifier les rectangles sur chaque pattern
    public void setUpConstraints() {
        Iterator it = this.patterns.iterator();

        while(it.hasNext()) {
           // this.constraints.add()
        }
    }

    public void solve() {
        org.apache.commons.math3.optim.PointValuePair solution = this.solver.optimize();
    }

    /*Collection<LinearConstraint> constraints = new ArrayList<>();
    constraints.add(new LinearConstraint(new double[] { -1, 2}, Relationship.LEQ,  5));
    constraints.add(new LinearConstraint(new double[] { 1, 2}, Relationship.LEQ,  14));
    constraints.add(new LinearConstraint(new double[] { 1}, Relationship.LEQ,  8));

    PointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, false);

    double x = solution.getPoint()[0];
    double y = solution.getPoint()[1];

    double max = solution.getValue();

    System.out.println("x : "+ x);
    System.out.println("y : "+ y);
    System.out.println("max : "+ max);*/
}
