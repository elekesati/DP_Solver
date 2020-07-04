package dpsolver.model;

import dpsolver.helpers.AdditionalFunctions;
import dpsolver.helpers.DimensionConverter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores all variables in a map.
 *
 * @author Elekes Attila
 */
public class Variables {

    private static Map<String, Double> mNumbers = new HashMap<>();
    private static Map<String, Variable> mArrays = new HashMap<>();
    private static String mTargetVariable = new String();
    private static int mDimensions;

    /**
     * Constructor.
     */
    private Variables() {
    }

    public static void initialize() {
        mNumbers.put("inf", Double.POSITIVE_INFINITY);
    }

    /**
     * Adds variables and their initial values to the map.
     *
     * @param variables contains the variables and the values in string format
     */
    public static void addVariables(String[] variables) throws Exception {
        for (String string : variables) {
            if (!string.trim().isEmpty()) {
                addVariable(string, false);
            }
        }
    }

    /**
     * Adds a single variable and its initial value to the map.
     *
     * @param input contains the variable and the value in string format
     * @param isTarget flag for target variable
     */
    public static void addVariable(String input, boolean isTarget) throws Exception {
        checkVariableInput(input);

        int equationSignIndex = input.indexOf('=');
        String variable = input;
        String value = "";

        if (equationSignIndex != -1) {
            variable = input.substring(0, equationSignIndex).trim();
            value = input
                    .substring(equationSignIndex + 1, input.length()).trim();
        }

        if (variable.contains("(")) {
            addArray(variable, value, isTarget);
        } else {
            addNumber(variable, value);
        }
    }

    /**
     * Updates the value of a number.
     *
     * @param key name of the variable
     * @param value new value
     */
    public static void updateNumber(String key, Double value) {
        mNumbers.put(key, value);
    }

    /**
     * Updates the value of an array, this method is used when the index is
     * given in linear form.
     *
     * @param key name of the variable
     * @param value new value
     * @param index index of the element
     */
    public static void updateArray(String key, Double value, int index) {
        mArrays.get(key).setValue(value, index);
    }

    /**
     * Updates the value of a array, this method is used when the indexes are
     * given in multidimensional form.
     *
     * @param key name of the variable
     * @param value new value
     * @param indexes indexes of the element
     */
    public static void updateArray(String key, Double value, int... indexes) {
        mArrays.get(key).setValue(value, indexes);
    }

    /**
     * Adds a number to the map.
     *
     * @param variable is the variable name (key in the map)
     * @param value is the initial value of the variable
     */
    private static void addNumber(String variable, String value) {
        if (value.isEmpty()) {
            mNumbers.put(variable, Double.NaN);
        } else {
            mNumbers.put(variable, Double.parseDouble(value));
        }
    }

    /**
     * Adds an array to the map.
     *
     * @param variable is the variable name (key in the map)
     * @param value is the initial value of the variable
     */
    private static void addArray(String variable, String value, boolean isTarget) {
        String variableName = variable.substring(0, variable.indexOf("("));
        String[] dimensionLimitsString = variable.replaceAll("[^0-9,]", "").
                split(",");
        String[] values = value.replaceAll("[^0-9,.\\-]", "").split(",");
        int[] dimensionLimits = new int[dimensionLimitsString.length];
        for (int i = 0; i < dimensionLimitsString.length; ++i) {
            dimensionLimits[i] = Integer.parseInt(dimensionLimitsString[i]);
        }

        if (isTarget) {
            mArrays.put(variableName, new TargetVariable(variableName, dimensionLimits));
            mTargetVariable = variableName;
        } else {
            mArrays.put(variableName, new Variable(variableName, dimensionLimits));
        }

        AdditionalFunctions.addFunction(variableName, dimensionLimits.length, isTarget);

        if (!value.isEmpty()) {
            for (int i = 0; i < values.length; ++i) {
                mArrays.get(variableName).
                        setValue(Double.parseDouble(values[i]), i);
            }
        } else {
            int capacity = DimensionConverter.capacity(dimensionLimits);
            for (int i = 0; i < capacity; ++i) {
                mArrays.get(variableName).setValue(Double.NaN, i);
            }
        }
    }

    /**
     * Check if the declaration of the variable is correct.
     *
     * @param input the variable
     * @throws Exception specifies the error in the message
     */
    private static void checkVariableInput(String input) throws Exception {
        String variable = input.replaceAll(" ", "");

        if (!variable.replaceAll("[a-zA-Z0-9_,.{}()=\\-]", "").trim().isEmpty()) {
            throw new IllegalArgumentException("Illegal characters in string: " + input);
        }

        if (variable.endsWith("=")) {
            throw new IllegalArgumentException("Assigned value is missing: " + input);
        }

        String roundBrackets = variable.replaceAll("[^()]", "").trim();

        if (!(roundBrackets.equals("()") || roundBrackets.isEmpty())) {
            throw new IllegalArgumentException("Parantheses mismatch: " + input);
        }

        String curlyBrackets = variable.replaceAll("[^{}]", "").trim();

        if (!(curlyBrackets.equals("{}") || curlyBrackets.isEmpty())) {
            throw new IllegalArgumentException("Parantheses mismatch: " + input);
        }

        String equationSign = variable.replaceAll("[^=]", "").trim();

        if (!(equationSign.equals("=") || equationSign.isEmpty())) {
            throw new IllegalArgumentException("Equation sign mismatch: " + input);
        }

        Matcher matcher = Pattern.compile("(?<=\\()(.*?)(?=\\))").matcher(variable);

        if (matcher.find()) {
            if (!matcher.group().replaceAll("[\\-,]", "").replaceAll("\\d", "").isEmpty()) {
                throw new IllegalArgumentException("Illegal number format: " + input);
            }

            if (matcher.group().startsWith(",") || matcher.group().endsWith(",")) {
                throw new IllegalArgumentException("Value missing: " + input);
            }
        }

        matcher = Pattern.compile("(?<=\\{)(.*?)(?=\\})").matcher(variable);

        if (matcher.find()) {
            if (!matcher.group().replaceAll("[\\-,]", "").replaceAll("\\d", "").isEmpty()) {
                throw new IllegalArgumentException("Illegal number format: " + input);
            }

            if (matcher.group().startsWith(",") || matcher.group().endsWith(",")) {
                throw new IllegalArgumentException("Value missing: " + input);
            }
        }

    }

    /**
     * Sets one variable for each Variables starting with 'i' and continued by
     * number are reserved for index.
     *
     * @param dimensions is the number of indexes required
     */
    public static void addIndexes(int dimensions) {
        mDimensions = dimensions;
        for (int i = 1; i <= dimensions; ++i) {
            addNumber("i" + i, "0");
        }
    }

    /**
     * Updates the indexes.
     *
     * @param values new indexes
     */
    public static void setIndexes(int... values) {
        for (int i = 1; i <= values.length; ++i) {
            updateNumber("i" + i, (double) values[i - 1]);
        }
    }

    /**
     * Returns a number specified by name from map.
     *
     * @param key name of the variable
     * @return variable
     */
    public static Double getNumber(String key) {
        return mNumbers.get(key);
    }

    /**
     * Returns an array specified by name from map.
     *
     * @param key name of the variable
     * @return variable
     */
    public static Variable getArray(String key) {
        return mArrays.get(key);
    }

    /**
     * Returns a set with the names of scalars.
     *
     * @return set with names
     */
    public static Set<String> getVariableNames() {
        return mNumbers.keySet();
    }

    /**
     * Returns the map of scalars.
     *
     * @return map of scalars
     */
    public static Map<String, Double> getVariableMap() {
        return mNumbers;
    }

    /**
     * Clears all stored data.
     */
    public static void clear() {
        mDimensions = 0;
        mNumbers.clear();
        mArrays.clear();
        mTargetVariable = new String();
    }

    /**
     * Prints all variable.
     */
    public static void printAll() {
        System.out.println("Numbers: " + mNumbers.toString());
        System.out.println("Arrays: " + mArrays.toString());
    }

    /**
     * Returns the name of the target variable.
     *
     * @return target variable
     */
    public static String getTargetVariable() {
        return mTargetVariable;
    }

    /**
     * Returns the indexes.
     *
     * @return array of indexes
     */
    public static int[] getIndexes() {
        int[] indexes = new int[mDimensions];
        for (int i = 1; i <= indexes.length; ++i) {
            indexes[i - 1] = getNumber("i" + i).intValue();
        }
        return indexes;
    }
}
