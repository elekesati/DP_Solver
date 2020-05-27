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
        if (mError) {
            return Double.NaN;
        }

        TargetVariable targetVariable = (TargetVariable) Variables.getVector(mTargetVariable);
        int[] parentArgs = Variables.getIndexes();
        Double result = Double.NaN;

        try {
            result = targetVariable.getValue(args);
            
            mLog.add(new DpLog(DpLog.GET, result.toString(), args, parentArgs));
            
            String key = Arrays.toString(parentArgs);
            if (!mHierarchy.containsKey(key)){
                if (mHierarchy.isEmpty()){
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
                result = new Expression(mFormula
                        .getActualBranchExpression())
                        .evaluate();
                Variables.updateVector(mTargetVariable, result, args);
                Variables.setIndexes(parentArgs);
                
                mLog.add(new DpLog(DpLog.SET, result.toString(), args, parentArgs));
            }

            targetVariable.updateStatus(TargetVariable.BLACK, args);
            
        } catch (IndexOutOfBoundsException e) {
            Variables.setIndexes(args);
            Expression expression = mFormula.getActualBranchExpression();

            if (expression != null) {
                result = expression.evaluate();
            } else {
                mError = true;
                mIndexOutOfBounds = true;
                result = Double.NaN;
                
                mLog.add(new DpLog(DpLog.ERROR, "index out of bound", args, parentArgs));
            }
            
            Variables.setIndexes(parentArgs);
        }
        
        return result;
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
    
    public static void printLog(){
        for (DpLog log : mLog){
            System.out.println(log.toString());
        }
    }
    
    public static void printHierarchy(){
        for (Map.Entry<String, HashSet<int[]>> node : mHierarchy.entrySet()){
            System.out.print(node.getKey());
            System.out.print("={");
            for (int[] child : node.getValue()){
                System.out.print(Arrays.toString(child));
            }
            System.out.println("}");
        }
    }
}
