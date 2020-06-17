/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.objecthunter.exp4j.Expression;

/**
 * Builds and solves the dynamic program from the given inputs
 *
 * @author Elekes Attila
 */
public class DynamicProgram {

    private static Formula mFormula = new Formula();
    private static String mTargetVariable;

    private static boolean mError = false;
    private static boolean mHasCircle = false;
    private static boolean mIndexOutOfBounds = false;

    private static List<DpLog> mLog = new ArrayList<>();
    private static Map<String, HashSet<int[]>> mHierarchy = new HashMap<>();

    /**
     * Loads the input data and builds the dynamic program
     *
     * @param data
     */
    public static void load(DpData data) throws Exception {
        List<String> branchList = data.getBranches();
        List<String> criteriaList = data.getCriterias();
        String[] branchArray = new String[branchList.size()];
        String[] criteriaArray = new String[criteriaList.size()];

        for (int i = 0; i < branchArray.length; ++i) {
            branchArray[i] = branchList.get(i);
            criteriaArray[i] = criteriaList.get(i);
        }

        if (data.getVariables().length != 0) {
            Variables.addVariables(data.getVariables());
        }
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
        if (mError){
            return Double.NaN;
        }
        
        TargetVariable targetVariable = (TargetVariable) Variables.getArray(mTargetVariable);
        int[] parentArgs = Variables.getIndexes();
        Double result = Double.NaN;
        
        try{
            result = targetVariable.getValue(args);
            
            String key = Arrays.toString(parentArgs);
            if (!mHierarchy.containsKey(key)) {
                if (mHierarchy.isEmpty()) {
                    key = "start";
                }
                mHierarchy.put(key, new HashSet<>());
            }
            mHierarchy.get(key).add(args);

            if (targetVariable.getStatus(args) == TargetVariable.GRAY) {
                mError = true;
                mHasCircle = true;
                result = Double.NaN;

                mLog.add(new DpLog(DpLog.ERROR, "circle", args, parentArgs));
            }
            
            if (targetVariable.getStatus(args) == TargetVariable.WHITE) {
		targetVariable.updateStatus(TargetVariable.GRAY, args);
            }

            if (result.isNaN()) {
                Variables.setIndexes(args);
                Expression expression = mFormula.getActualBranchExpression();
                
                if (expression == null){
                    mError = true;
                    mIndexOutOfBounds = true;
                    result = Double.NaN;

                    mLog.add(new DpLog(DpLog.ERROR, "not defined", args, parentArgs));
                }
                else{
                    result = new Expression(expression).evaluate();
                    Variables.updateArray(mTargetVariable, result, args);
                    Variables.setIndexes(parentArgs);

                    mLog.add(new DpLog(DpLog.SET, result.toString(), args, parentArgs));
                }
                
            }

            targetVariable.updateStatus(TargetVariable.BLACK, args);
        }
        catch (IndexOutOfBoundsException ex){
            Variables.setIndexes(args);
            Expression expression = mFormula.getActualBranchExpression();
            
            if (!indexesInBounds(targetVariable, args) || expression == null){
                mError = true;
		mIndexOutOfBounds = true;
		result = Double.NaN;

		mLog.add(new DpLog(DpLog.ERROR, "index out of bound", args, parentArgs));
            }
            else{
                result = expression.evaluate();
            }
            
            Variables.setIndexes(parentArgs);
        }
        
        return result;
    }
    
    private static boolean indexesInBounds(TargetVariable targetVariable, int... indexes){
        int[] limits = targetVariable.getDimensionLimits();
        
        for (int i=0; i<indexes.length; ++i){
            if ((indexes[i] < -1) || (indexes[i] > limits[i])){
                return false;
            }
        }
        
        return true;
    }

    /**
     * Resets the dynamic program
     */
    public static void restart() {
        Variables.clear();
        Variables.initialize();
        mTargetVariable = new String();
        mFormula.clear();
        mLog.clear();
        mHierarchy.clear();
        mError = false;
        mHasCircle = false;
        mIndexOutOfBounds = false;
    }

    /**
     * Prints the steps of the recursive calls.
     */
    public static void printLog() {
        for (DpLog log : mLog) {
            System.out.println(log.toString());
        }
    }

    /**
     * Prints the parent-children relations defined by the recursive calls.
     */
    public static void printHierarchy() {
        for (Map.Entry<String, HashSet<int[]>> node : mHierarchy.entrySet()) {
            System.out.print(node.getKey());
            System.out.print("={");
            for (int[] child : node.getValue()) {
                System.out.print(Arrays.toString(child));
            }
            System.out.println("}");
        }
    }

    /**
     * Returns the steps of the recursive calls.
     *
     * @return log of the steps
     */
    public static List<DpLog> getLog() {
        return mLog;
    }

    /**
     * Returns the parent-children relations defined by the recursive calls.
     *
     * @return the parent-children relations
     */
    public static Map<String, HashSet<int[]>> getHierarchy() {
        return mHierarchy;
    }
    
    /**
     * Returns a string which specifies the errors occurred or an empty string
     * if no errors were logged.
     * @return error message
     */
    public static String getErrorMessage(){
        if (!mError){
            return "";
        }
        
        if (mIndexOutOfBounds){
            return "Index out of bounds or the function is not defined for some indexes.";
        }
        
        if (mHasCircle){
            return "Circle found.";
        }
        
        return "";
    }
}
