package models;

/**
 * Created by Medric on 18/04/2015.
 */
public class Position {
    private double x;
    private double y;

    /**
     * @param x
     * @param y
     */
    public Position(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    /**
     * @return x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y
     */
    public void setY(double y) {
        this.y = y;
    }
}
