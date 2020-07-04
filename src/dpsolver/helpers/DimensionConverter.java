package dpsolver.helpers;

/**
 * Contains functions which can convert multidimensional index to linear and
 * linear index to multidimensional.
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
     * of its linear representation.
     *
     * @param bounds dimension limits
     * @param indexes array of indexes
     * @return index in the corresponding linear representation
     * @throws IndexOutOfBoundsException
     * @throws IllegalArgumentException
     */
    public static int multiDimensonalToLinear(int[] bounds, int[] indexes)
            throws IndexOutOfBoundsException, IllegalArgumentException {

        if (bounds.length != indexes.length) {
            throw new IllegalArgumentException("Arrays must have same size.");
        }

        for (int i = 0; i < bounds.length; ++i) {
            if (bounds[i] <= 0) {
                throw new IllegalArgumentException("Bounds must be greater or equal then one.");
            }
        }

        for (int i = 0; i < bounds.length; ++i) {
            if (indexes[i] < 0 || indexes[i] >= bounds[i]) {
                throw new IndexOutOfBoundsException("Index out of bounds.");
            }
        }

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
     * multidimensional representation.
     *
     * @param bounds dimension limits
     * @param index index
     * @return indexes in the corresponding multidimensional representation
     * @throws IndexOutOfBoundsException
     * @throws IllegalArgumentException
     */
    public static int[] linearToMultiDimensional(int[] bounds, int index)
            throws IndexOutOfBoundsException, IllegalArgumentException {

        for (int i = 0; i < bounds.length; ++i) {
            if (bounds[i] <= 0) {
                throw new IllegalArgumentException("Bounds must be greater or equal then one.");
            }
        }

        if (index >= capacity(bounds)) {
            throw new IndexOutOfBoundsException("Index out of bounds.");
        }

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

    /**
     * Returns the capacity of a multidimensional variable.
     *
     * @param bounds dimension limits
     * @return capacity
     * @throws IllegalArgumentException
     */
    public static int capacity(int... bounds) throws IllegalArgumentException {

        for (int i = 0; i < bounds.length; ++i) {
            if (bounds[i] <= 0) {
                throw new IllegalArgumentException("Bounds must be greater or equal then one.");
            }
        }

        int prod = 1;

        for (int i = 0; i < bounds.length; ++i) {
            prod *= bounds[i];
        }

        return prod;
    }
}
