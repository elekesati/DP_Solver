/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver.model;

import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
import net.objecthunter.exp4j.Expression;

/**
 * Stores the formula of the problem
 *
 * @author Elekes Attila
 */
public class Formula {

    private static final String ELSE = "else";
    private static final String EXCEPTION_MESSAGE = "Input formula can have only one else branch.";
    private final List<Branch> mBranches;
    private boolean mHasElseBranch = false;
    private byte mElseBranchPosition; 

    /**
     * Constructor
     */
    public Formula() {
        this.mBranches = new ArrayList<>();
    }

    /**
     * Adds a branch of the formula
     *
     * @param branch the formula of the branch
     * @param criteria the criteria which defines when to use this branch
     */
    public void addBranch(String branch, String criteria) throws IllegalArgumentException {
        
        if (criteria.equals(ELSE)){
            if (mHasElseBranch){
                throw new IllegalArgumentException(EXCEPTION_MESSAGE);
            }
            else{
                mHasElseBranch = true;
                mElseBranchPosition = (byte) mBranches.size();
                criteria = "1";
            }
        }
        
        if (mHasElseBranch){
            mBranches.add(mElseBranchPosition, new Branch(branch, criteria));
        }
        else{
            mBranches.add(new Branch(branch, criteria));
        }
    }

    public void addBranch(Pair<String, String> branch) throws IllegalArgumentException {
        
        if (branch.getValue().equals(ELSE)){
            if (mHasElseBranch){
                throw new IllegalArgumentException(EXCEPTION_MESSAGE);
            }
            else{
                mHasElseBranch = true;
                mElseBranchPosition = (byte) mBranches.size();
                branch = new Pair<>(branch.getKey(), "1");
            }
        }
        
        if (mHasElseBranch){
            mBranches.add(mElseBranchPosition, new Branch(branch));
        }
        else{
            mBranches.add(new Branch(branch));
        }
    }

    /**
     * Adds branches to the formula
     *
     * @param branches array of branches
     * @param criterias array of criterias
     */
    public void addBranches(String[] branches, String[] criterias) throws IllegalArgumentException {
        for (int i = 0; i < branches.length; ++i) {
            addBranch(branches[i], criterias[i]);
        }
    }

    /**
     * Returns the corresponding branch for the cell specified by its indexes
     *
     * @return corresponding branch
     */
    public Expression getActualBranchExpression() {
        for (int i = 0; i < mBranches.size(); ++i) {
            if (mBranches
                    .get(i)
                    .getExpression()
                    .getValue()
                    .setVariables(Variables.getVariableMap())
                    .evaluate() == 1.0) {
                return mBranches
                        .get(i)
                        .getExpression()
                        .getKey()
                        .setVariables(Variables.getVariableMap());
            }
        }
        return null;
    }

    /**
     * Returns the formula as list of branches (executable part and criteria)
     *
     * @return list of branches
     */
    public List<Pair<String, String>> getFormula() {
        List<Pair<String, String>> formula = new ArrayList<>();
        for (Branch branch : mBranches) {
            formula.add(branch.getString());
        }
        return formula;
    }

    /**
     * Removes all branches
     */
    public void clear() {
        mBranches.clear();
        mHasElseBranch = false;
    }
}
