package fr.klemek.genetics;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Laboratory<T extends Subject> {

    //variables

    private Subject[] population;
    private int generation;
    private int stagnation;

    private final CopyOnWriteArrayList<Float> bestScoreHistory;
    private final CopyOnWriteArrayList<Float> meanScoreHistory;
    private final CopyOnWriteArrayList<Float> mutationHistory;

    private final LaboratoryParameters params;

    //constructor

    public Laboratory(Class<T> type, LaboratoryParameters params) {
        this.params = params;

        population = new Subject[params.populationSize];
        generation = 0;
        for (int i = 0; i < population.length; i++)
            population[i] = Utils.instantiate(type);

        this.bestScoreHistory = new CopyOnWriteArrayList<>();
        this.meanScoreHistory = new CopyOnWriteArrayList<>();
        this.mutationHistory = new CopyOnWriteArrayList<>();

        this.mutationHistory.add(params.defaultMutation);
        this.bestScoreHistory.add(this.getBest().score());
        this.meanScoreHistory.add(this.getMeanScore());
    }

    //accessors

    public int getGeneration() {
        return generation;
    }

    int getBestGeneration() {
        return generation - stagnation;
    }

    List<Float> getBestScoreHistory() {
        return bestScoreHistory;
    }

    private float getCurrentBestScore() {
        return bestScoreHistory.get(bestScoreHistory.size() - 1);
    }

    List<Float> getMeanScoreHistory() {
        return meanScoreHistory;
    }

    float getCurrentMeanScore() {
        return meanScoreHistory.get(meanScoreHistory.size() - 1);
    }

    List<Float> getMutationHistory() {
        return mutationHistory;
    }

    float getCurrentMutation() {
        return mutationHistory.get(mutationHistory.size() - 1);
    }

    LaboratoryParameters getParams() {
        return params;
    }

    public Subject[] getPopulation() {
        return population;
    }

    //functions

    public void nextGeneration() {
        generation++;

        if (params.verbose)
            System.out.println(String.format("Computing generation %d...", generation));

        float mutation = params.defaultMutation;
        if (stagnation > params.highStagnation)
            mutation = params.defaultMutation + (1f - params.defaultMutation) * ((stagnation - params.highStagnation) / (float) (params.maxStagnation - params.highStagnation));

        if (params.verbose)
            System.out.println(String.format("\tmutation used : %.2f%%", mutation * 100));

        this.population = selection(population.clone(), params.bestLowest);
        this.population = breeding(population.clone(), mutation, params.mutateOnlyChildren, params.bestLowest);

        float bestScore = this.getBest().score();
        float meanScore = this.getMeanScore();

        if (Utils.compare(params.bestLowest, bestScore, getCurrentBestScore()))
            stagnation = 0;
        else
            stagnation++;

        if (params.verbose) {
            System.out.println(String.format("\tmean score : %f", meanScore));
            System.out.println(String.format("\tbest score : %f", bestScore));
            System.out.println(String.format("\t\tstagnation : %d", stagnation));
            if (this.shouldStop())
                System.out.println("\t\tsimulation should stop");
        }

        this.mutationHistory.add(mutation);
        this.bestScoreHistory.add(bestScore);
        this.meanScoreHistory.add(meanScore);
    }

    public T getBest() {
        //noinspection unchecked
        return (T) Laboratory.getBest(population.clone(), params.bestLowest);
    }

    private float getMeanScore() {
        float sum = 0f;
        int total = 0;
        for (Subject subject : population) {
            total++;
            sum += subject.score();
        }
        if (total == 0)
            return 0f;
        else
            return sum / (float) total;
    }

    public boolean shouldStop() {
        return stagnation > params.maxStagnation ||
                (params.stopThreshold != LaboratoryParameters.NO_THRESHOLD && Utils.compare(params.bestLowest, getCurrentBestScore(), params.stopThreshold));
    }

    //class functions

    private static Subject getBest(Subject[] population, boolean bestLowest) {
        Subject best = null;
        for (Subject subject : population)
            if (best == null || Utils.compare(!bestLowest, best.score(), subject.score())) //renew best if same value
                best = subject;
        return best;
    }

    private static Subject[] selection(Subject[] population, boolean bestLowest) {
        Subject[] output = new Subject[population.length / 2];

        //random 'fights'
        int[] indexes = Utils.indexes(population.length);
        Utils.shuffle(indexes);

        float d0;
        float d1;
        for (int i = 0; i < population.length / 2; i++) {
            d0 = population[indexes[i * 2]].score();
            d1 = population[indexes[i * 2 + 1]].score();
            output[i] = population[indexes[Utils.compare(bestLowest, d0, d1) ? i * 2 : i * 2 + 1]];
        }

        return output;
    }

    private static Subject[] breeding(Subject[] population, float mutation, boolean mutateOnlyChildren, boolean bestLowest) {
        Subject[] output = new Subject[population.length * 2];

        //random 'breeds'
        int[] indexes = Utils.indexes(population.length);
        Utils.shuffle(indexes);

        for (int i = 0; i < population.length; i += 2) {
            Subject parent1 = population[indexes[i]];
            Subject parent2 = population[indexes[i + 1]];

            Subject[] children = parent1.createChildren(parent2);

            if (mutateOnlyChildren) {
                if (Utils.randBoolean(mutation))
                    children[0].mutate(1);
                if (Utils.randBoolean(mutation))
                    children[1].mutate(1);
            }

            output[i] = parent1;
            output[i + 1] = parent2;
            output[population.length + i] = children[0];
            output[population.length + i + 1] = children[1];
        }

        if (!mutateOnlyChildren) {
            Subject best = Laboratory.getBest(output, bestLowest);
            for (Subject subject : output) {
                if (!subject.equals(best) && Utils.randBoolean(mutation)) {
                    subject.mutate(1);
                }
            }
        }

        return output;
    }
}
