package dpsolver.model;

/**
 * Stores the log of one step from the solution.
 *
 * @author Elekes Attila
 */
public class DpLog {

    public static final String GET = "get";
    public static final String SET = "set";
    public static final String ERROR = "error";

    private final String mKey;
    private final String mDescription;
    private final int[] mIndexes;
    private final int[] mParent;
    private boolean mFirstUse = false;
    private boolean mIsClosed = false;

    /**
     * Constructor.
     *
     * @param key type of the operation performed in the current step (key).
     * @param description addtitional informations
     * @param indexes current indexes
     * @param parent parent indexes
     */
    public DpLog(String key, String description, int[] indexes, int[] parent) {
        this.mKey = key;
        this.mDescription = description;
        this.mIndexes = indexes;
        this.mParent = parent;
    }

    /**
     * Returns the type of the operation performed in the current step (key).
     *
     * @return key
     */
    public String getKey() {
        return mKey;
    }

    /**
     * Returns the addtitional informations.
     *
     * @return description
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Returns the current indexes.
     *
     * @return current indexes
     */
    public int[] getIndexes() {
        return mIndexes;
    }

    /**
     * Returns the parent indexes.
     *
     * @return parent indexes
     */
    public int[] getParent() {
        return mParent;
    }

    /**
     * Check if the cell was closed in this step.
     *
     * @return true if the cell was closed
     */
    public boolean isClosed() {
        return mIsClosed;
    }

    /**
     * Check if the cell was used first time.
     *
     * @return true if the cell was used first time
     */
    public boolean isFirstUse() {
        return mFirstUse;
    }

    /**
     * Logs that the cell was used first time.
     */
    public void setFirstUse() {
        this.mFirstUse = true;
    }

    /**
     * Logs that the cell was closed in this step.
     */
    public void setClosed() {
        this.mIsClosed = true;
    }

    /**
     * Returns the string represenation of the current step's data.
     *
     * @return string represenation of the current step's data
     */
    @Override
    public String toString() {
        String indexString = "{";

        for (int i = 0; i < mIndexes.length - 1; ++i) {
            indexString += mIndexes[i] + ", ";
        }

        indexString += mIndexes[mIndexes.length - 1] + "}";

        return "Index = " + indexString + ": " + mKey + " - " + mDescription;
    }

}
