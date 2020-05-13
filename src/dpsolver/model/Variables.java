/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver.model;

import dpsolver.helpers.AdditionalFunctions;
import dpsolver.helpers.DimensionConverter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Stores all variables in a map
 *
 * @author Elekes Attila
 */
public class Variables {

    private static Map<String, Double> mScalars = new HashMap<>();
    private static Map<String, Variable> mVectors = new HashMap<>();
    private static String mTargetVariable = new String();
    private static int mDimensions;

    /**
     * Constructor
     */
    private Variables() {
    }
    
    public static void initialize(){
        mScalars.put("inf", Double.POSITIVE_INFINITY);
    }

    /**
     * Adds variables and their initial values to the map
     *
     * @param variables contains the variables and the values in string format
     */
    public static void addVariables(String[] variables) {
        for (String string : variables) {
            addVariable(string, false);
        }
    }

    /**
     * Adds a single variable and its initial value to the map
     *
     * @param input contains the variable and the value in string format
     * @param isTarget flag for target variable
     */
    public static void addVariable(String input, boolean isTarget) {
        int equationSignIndex = input.indexOf('=');
        String variable = input;
        String value = "";

        if (equationSignIndex != -1) {
            variable = input.substring(0, equationSignIndex).trim();
            value = input
                    .substring(equationSignIndex + 1, input.length()).trim();
        }

        if (variable.contains("(")) {
            addVector(variable, value, isTarget);
        } else {
            addScalar(variable, value);
        }
    }

    /**
     * Updates the value of a scalar
     *
     * @param key name of the variable
     * @param value new value
     */
    public static void updateScalar(String key, Double value) {
        mScalars.put(key, value);
    }

    /**
     * Updates the value of a vector, this method is used when the index is
     * given in linear form
     *
     * @param key name of the variable
     * @param value new value
     * @param index index of the element
     */
    public static void updateVector(String key, Double value, int index) {
        mVectors.get(key).setValue(value, index);
    }

    /**
     * Updates the value of a vector, this method is used when the indexes are
     * given in multidimensional form
     *
     * @param key name of the variable
     * @param value new value
     * @param indexes indexes of the element
     */
    public static void updateVector(String key, Double value, int... indexes) {
        mVectors.get(key).setValue(value, indexes);
    }

    /**
     * Adds a scalar to the map
     *
     * @param variable is the variable name (key in the map)
     * @param value is the initial value of the variable
     */
    private static void addScalar(String variable, String value) {
        if (value.isEmpty()) {
            mScalars.put(variable, Double.NaN);
        } else {
            mScalars.put(variable, Double.parseDouble(value));
        }
    }

    /**
     * Adds a vector to the map
     *
     * @param variable is the variable name (key in the map)
     * @param value is the initial value of the variable
     */
    private static void addVector(String variable, String value, boolean isTarget) {
        String variableName = variable.substring(0, variable.indexOf("("));
        String[] dimensionLimitsString = variable.replaceAll("[^0-9,]", "").
                split(",");
        String[] values = value.replaceAll("[^0-9,.]", "").split(",");
        int[] dimensionLimits = new int[dimensionLimitsString.length];
        for (int i = 0; i < dimensionLimitsString.length; ++i) {
            dimensionLimits[i] = Integer.parseInt(dimensionLimitsString[i]);
        }

        mVectors.put(variableName, new Variable(variableName, dimensionLimits));
        AdditionalFunctions.addFunction(variableName, dimensionLimits.length, isTarget);

        if (!value.isEmpty()) {
            for (int i = 0; i < values.length; ++i) {
                mVectors.get(variableName).
                        setValue(Double.parseDouble(values[i]), i);
            }
        } else {
            int limit = DimensionConverter.
                    multiDimensonalToLinear(dimensionLimits, dimensionLimits);
            for (int i = 0; i < limit; ++i) {
                mVectors.get(variableName).setValue(Double.NaN, i);
            }
        }

        if (isTarget) {
            mTargetVariable = variableName;
        }
    }

    /**
     * Sets one variable for each Variables starting with 'i' and continued by
     * number are reserved for index
     *
     * @param dimensions is the number of indexes required
     */
    public static void addIndexes(int dimensions) {
        mDimensions = dimensions;
        for (int i = 1; i <= dimensions; ++i) {
            addScalar("i" + i, "0");
        }
    }

    /**
     * Updates the indexes
     *
     * @param values new indexes
     */
    public static void setIndexes(int... values) {
        for (int i = 1; i <= values.length; ++i) {
            updateScalar("i" + i, (double) values[i - 1]);
        }
    }

    /**
     * Returns a scalar specified by name from map
     *
     * @param key name of the variable
     * @return variable
     */
    public static Double getScalar(String key) {
        return mScalars.get(key);
    }

    /**
     * Returns a vector specified by name from map
     *
     * @param key name of the variable
     * @return variable
     */
    public static Variable getVector(String key) {
        return mVectors.get(key);
    }

    /**
     * Returns a set with the names of scalars
     *
     * @return set with names
     */
    public static Set<String> getVariableNames() {
        return mScalars.keySet();
    }

    /**
     * Returns the map of scalars
     *
     * @return map of scalars
     */
    public static Map<String, Double> getVariableMap() {
        return mScalars;
    }

    /**
     * Clears all stored data
     */
    public static void clear() {
        mDimensions = 0;
        mScalars.clear();
        mVectors.clear();
        mTargetVariable = new String();
    }

    /**
     * Prints all variable
     */
    public static void printAll() {
        System.out.println("Scalars: " + mScalars.toString());
        System.out.println("Vectors: " + mVectors.toString());
    }

    /**
     * Returns the name of the target variable
     *
     * @return target variable
     */
    public static String getTargetVariable() {
        return mTargetVariable;
    }

    /**
     * Returns the indexes
     *
     * @return array of indexes
     */
    public static int[] getIndexes() {
        int[] indexes = new int[mDimensions];
        for (int i = 1; i <= indexes.length; ++i) {
            indexes[i - 1] = getScalar("i" + i).intValue();
        }
        return indexes;
    }
}
