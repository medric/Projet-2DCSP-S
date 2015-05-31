package models;

/**
 * Created by Medric on 18/04/2015.
 */
public class Dimension {

    private double LX;
    private double LY;

    /**
     * @param LX
     * @param LY
     */
    public Dimension(double LX, double LY) {
        this.setLX(LX);
        this.setLY(LY);
    }

    public Dimension(Dimension newDimension) {
        this(newDimension.getLX(), newDimension.getLY());
    }

    /**
     * Reverses LX / LY.
     */
    public void reverse() {
        double LX = this.getLX();
        this.setLX(this.getLY());
        this.setLY(LX);
    }

    /**
     * @return
     */
    public double getLX() {
        return LX;
    }

    /**
     * @param LX
     */
    public void setLX(double LX) {
        this.LX = LX;
    }

    /**
     * @return
     */
    public double getLY() {
        return LY;
    }

    /**
     * @param LY
     */
    public void setLY(double LY) {
        this.LY = LY;
    }

    @Override
    public boolean equals(Object o){
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Dimension))return false;

        Dimension oDimension = (Dimension)o;

        if(oDimension.getLX() == this.getLX() && oDimension.getLY() == this.getLY()) {
            return true;
        }

        // Try reversing
        oDimension.reverse();

        if(oDimension.getLX() == this.getLX() && oDimension.getLY() == this.getLY()) {
            return true;
        }

        // Reset
        oDimension.reverse();

        return false;
    }
}
