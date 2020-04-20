/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver.model;

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

    public DpData() {
    }

    public List<String> getBranches() {
        return mBranches;
    }

    public List<String> getCriterias() {
        return mCriterias;
    }

    public String[] getVariables() {
        return mVariables;
    }

    public String getTargetVariable() {
        return mTargetVariable;
    }

    public String getDimension() {
        return mDimension;
    }

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

    public void clear() {
        mBranches.clear();
        mCriterias.clear();
        mVariables = null;
        mTargetVariable = "";
        mDimension = "";
        mStartIndexes = "";
    }

    public int[] getStartIndexesArray() {
        String[] indexStrings = mStartIndexes.split(",");
        int[] indexes = new int[indexStrings.length];

        for (int i = 0; i < indexes.length; ++i) {
            indexes[i] = Integer.parseInt(indexStrings[i].trim());
        }

        return indexes;
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
}
