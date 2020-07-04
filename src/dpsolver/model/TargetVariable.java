package dpsolver.model;

import dpsolver.helpers.DimensionConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the target variable of the dynamic program. Extends Variable
 * class.
 *
 * @author Elekes Attila
 */
public class TargetVariable extends Variable {

    public static final byte WHITE = 0;
    public static final byte GRAY = 1;
    public static final byte BLACK = 2;
    private final List<Byte> mStatus;

    /**
     * Constructor.
     *
     * @param key name of the target variable
     * @param bounds bounds of the target variable
     */
    public TargetVariable(String key, int... bounds) {
        super(key, bounds);
        this.mStatus = new ArrayList<>(Collections.nCopies(mValues.size(), WHITE));
    }

    /**
     * Returns the status of the specified cell. A cell's status can be WHITE
     * (default), which means the cell was not used before. GRAY state means
     * this cell's value is calculating now. BLACK status means this cell's
     * value is calculated.
     *
     * @param indexes index of the specified cell
     * @return status of the specified cell
     */
    public Byte getStatus(int... indexes) {
        int index = DimensionConverter.multiDimensonalToLinear(mBounds, indexes);
        return this.mStatus.get(index);
    }

    /**
     * Updates the status of the specified cell. A cell's status can be WHITE
     * (default), which means the cell was not used before. GRAY state means
     * this cell's value is calculating now. BLACK status means this cell's
     * value is calculated.
     *
     * @param status status of the specified cell
     * @param indexes index of the specified cell
     */
    public void updateStatus(Byte status, int... indexes) {
        int index = DimensionConverter.multiDimensonalToLinear(mBounds, indexes);
        this.mStatus.set(index, status);
    }

    /**
     * Resets the status of all cell to the default (WHITE).
     */
    public void clearStatus() {
        for (int i = 0; i < mStatus.size(); ++i) {
            mStatus.set(i, WHITE);
        }
    }

    /**
     * Resets the value of all cell to the default (NaN).
     */
    public void clearValues() {
        for (int i = 0; i < mValues.size(); ++i) {
            mValues.set(i, Double.NaN);
        }
    }

    /**
     * Resets the target variable the default. By default the status is WHITE
     * and the value is NaN in all cell.
     */
    public void clear() {
        clearStatus();
        clearValues();
    }
}
