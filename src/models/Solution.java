package models;

import java.util.ArrayList;

/**
 * Created by Epulapp on 30/05/2015.
 */
public class Solution {
    private ArrayList<Bin> bins;

    public Solution(Solution solution) {
        this.bins = (ArrayList<Bin>)solution.bins.clone();
    }

    public ArrayList<Bin> getBins() {
        return this.bins;
    }

    public Solution(){
        this.bins = new ArrayList<Bin>();
    }
}
