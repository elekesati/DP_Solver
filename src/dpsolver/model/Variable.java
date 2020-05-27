/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver.model;

import dpsolver.helpers.DimensionConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores a variable which can have any dimensions
 *
 * @author Elekes Attila
 */
public class Variable {

    private final String mKey;
    final int[] mBounds;
    final List<Double> mValues;

    /**
     * Constructor
     *
     * @param key name of the variable
     * @param bounds limits of each dimension
     */
    public Variable(String key, int... bounds) {
        this.mKey = key;
        this.mBounds = bounds;
        int capacity = DimensionConverter.capacity(bounds);
        this.mValues = new ArrayList<>(Collections.nCopies(capacity, Double.NaN));
    }

    /**
     * Sets a variable, this method is used when the index is given in linear
     * form
     *
     * @param value value of the variable
     * @param index index in linear form
     */
    public void setValue(Double value, int index) {
        mValues.set(index, value);
    }

    /**
     * Sets a variable, this method is used when the indexes are given for each
     * dimension
     *
     * @param value value of the variable
     * @param indexes index for each dimension
     */
    public void setValue(Double value, int... indexes) {
        int index = DimensionConverter.
                multiDimensonalToLinear(mBounds, indexes);
        mValues.set(index, value);
    }

    /**
     * Returns a value, this method is used when the index is given in linear
     * form
     *
     * @param index index in linear form
     * @return value stored in the given index
     */
    public Double getValue(int index) {
        return mValues.get(index);
    }

    /**
     * Returns a value, this method is used when the indexes are given for each
     * dimension
     *
     * @param indexes index for each dimension
     * @return value stored in the given indexes
     */
    public Double getValue(int... indexes) {
        int index = DimensionConverter.
                multiDimensonalToLinear(mBounds, indexes);
        return mValues.get(index);
    }

    /**
     * Returns the name of the variable
     *
     * @return key
     */
    public String getKey() {
        return mKey;
    }

    /**
     * Returns the size of each dimension
     *
     * @return array of dimensoion limits
     */
    public int[] getDimensionLimits() {
        return mBounds;
    }

    /**
     * Returns the value of the variable
     *
     * @return value in string formt
     */
    @Override
    public String toString() {
        return mValues.toString();
    }
}
