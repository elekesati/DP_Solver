/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver.model;

import java.util.Arrays;
import java.util.List;

/**
 * Contains the input data which describes the dynamic program
 *
 * @author Elekes Attila
 */
public class DpData {

    private List<String> mBranches;
    private List<String> mCriterias;
    private String[] mVariables;
    private String mTargetVariable;
    private String mDimension;
    private String mStartIndexes;

    /**
     * Constructor
     */
    public DpData() {
        mBranches = null;
        mCriterias = null;
        mVariables = null;
        mTargetVariable = null;
        mDimension = null;
        mStartIndexes = null;
    }

    /**
     * Returns the list of branches.
     *
     * @return list of branches
     */
    public List<String> getBranches() {
        return mBranches;
    }

    /**
     * Returns the list of criterias.
     *
     * @return list of criterias
     */
    public List<String> getCriterias() {
        return mCriterias;
    }

    /**
     * Returns the array of variables.
     *
     * @return array of variables
     */
    public String[] getVariables() {
        return mVariables;
    }

    /**
     * Returns the target variable.
     *
     * @return the target variable
     */
    public String getTargetVariable() {
        return mTargetVariable;
    }

    /**
     * Returns the dimension
     *
     * @return the dimension
     */
    public String getDimension() {
        return mDimension;
    }

    /**
     * Returns the start indexes
     *
     * @return the start indexes
     */
    public String getStartIndexes() {
        return mStartIndexes;
    }

    public DpData setBranches(List<String> mBranches) {
        this.mBranches = mBranches;
        return this;
    }

    public DpData setCriterias(List<String> mCriterias) {
        this.mCriterias = mCriterias;
        return this;
    }

    public DpData setVariables(String[] mVariables) {
        this.mVariables = mVariables;
        return this;
    }

    public DpData setTargetVariable(String mTargetVariable) {
        this.mTargetVariable = mTargetVariable;
        return this;
    }

    public DpData setDimension(String mDimension) {
        this.mDimension = mDimension;
        return this;
    }

    public DpData setStartIndexes(String mStartIndexes) {
        this.mStartIndexes = mStartIndexes;
        return this;
    }

    /**
     * Clears all fields. By default all fields are null.
     */
    public void clear() {
        this.mBranches = null;
        this.mCriterias = null;
        this.mVariables = null;
        this.mTargetVariable = null;
        this.mDimension = null;
        this.mStartIndexes = null;
    }

    /**
     * Returns the array of start indexes
     *
     * @return array of start indexes
     */
    public int[] getStartIndexesArray() {
        String[] indexStrings = mStartIndexes.split(",");
        int[] indexes = new int[indexStrings.length];

        for (int i = 0; i < indexes.length; ++i) {
            indexes[i] = Integer.parseInt(indexStrings[i].trim());
        }

        return indexes;
    }

    /**
     * Check if all fields are empty.
     *
     * @return
     */
    public boolean isEmpty() {
        return mBranches == null
                && mCriterias == null
                && mVariables == null
                && mTargetVariable == null
                && mDimension == null
                && mStartIndexes == null;
    }

    @Override
    public String toString() {
        String string = new String();

        string = string.concat(String.valueOf(mBranches.size()))
                .concat(" ")
                .concat(String.valueOf(mVariables.length))
                .concat("\n");

        for (String branch : mBranches) {
            string = string.concat(branch).concat("\n");
        }

        for (String criteria : mCriterias) {
            string = string.concat(criteria).concat("\n");
        }

        for (String variable : mVariables) {
            string = string.concat(variable).concat("\n");
        }

        string = string.concat(mTargetVariable)
                .concat("\n")
                .concat(mDimension)
                .concat("\n")
                .concat(mStartIndexes);

        return string;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null) {
            return false;
        }

        if (this.getClass() != object.getClass()) {
            return false;
        }

        DpData dpData = (DpData) object;

        return this.mBranches.equals(dpData.mBranches)
                && this.mCriterias.equals(dpData.mCriterias)
                && Arrays.equals(this.mVariables, dpData.mVariables)
                && this.mTargetVariable.equals(dpData.mTargetVariable)
                && this.mDimension.equals(dpData.mDimension)
                && this.mStartIndexes.equals(dpData.mStartIndexes);
    }
}
