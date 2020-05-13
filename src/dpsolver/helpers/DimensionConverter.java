/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver.helpers;

/**
 * Contains functions which can convert multidimensional index to linear and
 * linearindex to multidimensional
 *
 * @author Elekes Attila
 */
public class DimensionConverter {

    /**
     * Private empty constructor
     */
    private DimensionConverter() {
    }

    /**
     * Converts the index of a multidimensional array to the corresponding index
     * of its linear representation
     *
     * @param bounds dimension limits
     * @param indexes array of indexes
     * @return index in the corresponding linear representation
     */
    public static int multiDimensonalToLinear(int[] bounds, int[] indexes) {
        int prod = 1;
        int index = 0;

        for (int i = 1; i < bounds.length; ++i) {
            prod *= bounds[i];
        }

        for (int i = 0; i < bounds.length - 1; ++i) {
            index += (indexes[i]) * prod;
            prod /= bounds[i + 1];
        }

        return index + indexes[bounds.length - 1];
    }

    /**
     * Converts the indexes of a linear array to the corresponding index of its
     * multidimensional representation
     *
     * @param bounds dimension limits
     * @param index index
     * @return indexes in the corresponding multidimensional representation
     */
    public static int[] linearToMultiDimensional(int[] bounds, int index) {
        int prod = 1;
        int[] indexes = new int[bounds.length];

        for (int i = 1; i < bounds.length; ++i) {
            prod *= bounds[i];
        }

        for (int i = 0; i < bounds.length - 1; ++i) {
            indexes[i] = index / prod;
            index %= prod;
            prod /= bounds[i + 1];
        }

        indexes[bounds.length - 1] = index;
        return indexes;
    }
}
