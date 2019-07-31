package fr.klemek.genetics;

public class LaboratoryParameters {

    static final float NO_THRESHOLD = Float.MIN_VALUE;

    final int populationSize;
    final boolean bestLowest;
    final boolean mutateOnlyChildren;
    final float defaultMutation;
    final int highStagnation;
    final int maxStagnation;
    final float stopThreshold;
    final boolean verbose;

    public LaboratoryParameters(ConfigFile config) {
        this.populationSize = config.getInt("populationSize") + (4 - config.getInt("populationSize") % 4);
        this.bestLowest = config.getBoolean("bestLowest");
        this.mutateOnlyChildren = config.getBoolean("mutateOnlyChildren");
        this.defaultMutation = config.getFloat("defaultMutation");
        this.highStagnation = config.getInt("highStagnation");
        this.maxStagnation = config.getInt("maxStagnation");
        this.stopThreshold = config.get("stopThreshold").equals("false") ? LaboratoryParameters.NO_THRESHOLD : config.getFloat("stopThreshold");
        this.verbose = config.getBoolean("verbose");
    }
}
