/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver.model;

import java.util.List;
import net.objecthunter.exp4j.Expression;

/**
 * Builds and solves the dynamic program from the given inputs
 *
 * @author Elekes Attila
 */
public class DynamicProgram {

    private static Formula mFormula = new Formula();
    private static String mTargetVariable;

    /**
     * Loads the input data and builds the dynamic program
     *
     * @param data
     */
    public static void load(DpData data) {
        List<String> branchList = data.getBranches();
        List<String> criteriaList = data.getCriterias();
        String[] branchArray = new String[branchList.size()];
        String[] criteriaArray = new String[criteriaList.size()];

        for (int i = 0; i < branchArray.length; ++i) {
            branchArray[i] = branchList.get(i);
            criteriaArray[i] = criteriaList.get(i);
        }

        Variables.addVariables(data.getVariables());
        Variables.addVariable(data.getTargetVariable(), true);
        mTargetVariable = Variables.getTargetVariable();
        Variables.addIndexes(Integer.parseInt(data.getDimension()));
        mFormula.addBranches(branchArray, criteriaArray);
    }

    /**
     * Solves the dynamic program.
     *
     * @param args the start indexes of the program
     * @return
     */
    public static Double solve(int... args) {
        Double result = Variables.getVector(mTargetVariable).getValue(args);
        if (result.isNaN()) {
            int[] tempArgs = Variables.getIndexes();
            Variables.setIndexes(args);
            result = new Expression(mFormula
                    .getCorrespondingBranchExpression(args))
                    .evaluate();
            Variables.updateVector(mTargetVariable, result, args);
            Variables.setIndexes(tempArgs);
        }
        return result;
    }

    /**
     * Resets the dynamic program
     */
    public static void restart() {
        Variables.clear();
        mTargetVariable = new String();
        mFormula.clear();
    }
}
