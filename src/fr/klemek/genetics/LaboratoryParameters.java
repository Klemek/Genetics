package fr.klemek.genetics;

public class LaboratoryParameters {

    public static final float NO_THRESHOLD = Float.MIN_VALUE;

    public final int populationSize;
    public final boolean bestLowest;
    public final boolean mutateOnlyChildren;
    public final float defaultMutation;
    public final int highStagnation;
    public final int maxStagnation;
    public final float stopThreshold;
    public final boolean verbose;

    public LaboratoryParameters(int populationSize, boolean bestLowest, boolean mutateOnlyChildren, float defaultMutation, int highStagnation, int maxStagnation, float stopThreshold, boolean verbose) {
        this.populationSize = populationSize + (4 - populationSize % 4);
        this.bestLowest = bestLowest;
        this.mutateOnlyChildren = mutateOnlyChildren;
        this.defaultMutation = defaultMutation;
        this.highStagnation = highStagnation;
        this.maxStagnation = maxStagnation;
        this.stopThreshold = stopThreshold;
        this.verbose = verbose;
    }
}
