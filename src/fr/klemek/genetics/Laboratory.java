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

    private final float defaultMutation;
    private final int highStagnation;
    private final int maxStagnation;
    private final boolean mutateOnlyChildren;

    //constructor

    public Laboratory(Class<T> type, int populationSize, float defaultMutation, int highStagnation, int maxStagnation, boolean mutateOnlyChildren) {
        this.defaultMutation = defaultMutation;
        this.highStagnation = highStagnation;
        this.maxStagnation = maxStagnation;
        this.mutateOnlyChildren = mutateOnlyChildren;

        population = new Subject[populationSize];
        generation = 0;
        for (int i = 0; i < population.length; i++)
            population[i] = Utils.instantiate(type);

        this.bestScoreHistory = new CopyOnWriteArrayList<>();
        this.meanScoreHistory = new CopyOnWriteArrayList<>();
        this.mutationHistory = new CopyOnWriteArrayList<>();

        this.mutationHistory.add(defaultMutation);
        this.bestScoreHistory.add(this.getBest().score());
        this.meanScoreHistory.add(this.getMeanScore());
    }

    //accessors

    public int getGeneration() {
        return generation;
    }

    public int getBestGeneration() {
        return generation - stagnation;
    }

    public List<Float> getBestScoreHistory() {
        return bestScoreHistory;
    }

    float getCurrentBestScore() {
        return bestScoreHistory.get(bestScoreHistory.size() - 1);
    }

    public List<Float> getMeanScoreHistory() {
        return meanScoreHistory;
    }

    public float getCurrentMeanScore() {
        return meanScoreHistory.get(meanScoreHistory.size() - 1);
    }

    public List<Float> getMutationHistory() {
        return mutationHistory;
    }

    public float getCurrentMutation() {
        return mutationHistory.get(mutationHistory.size() - 1);
    }

    public int getHighStagnation() {
        return highStagnation;
    }

    public int getMaxStagnation() {
        return maxStagnation;
    }

    public int getPopulationSize() {
        return this.population.length;
    }

    public boolean mutateOnlyChildren() {
        return mutateOnlyChildren;
    }

    //functions

    public void nextGeneration() {
        generation++;
        float mutation = defaultMutation;
        if (stagnation > highStagnation)
            mutation = defaultMutation + (1f - defaultMutation) * ((stagnation - highStagnation) / (float) (maxStagnation - highStagnation));

        this.population = selection(population.clone());
        this.population = breeding(population.clone(), mutation, mutateOnlyChildren);

        if (this.getCurrentBestScore() > this.getBest().score())
            stagnation = 0;
        else
            stagnation++;

        this.mutationHistory.add(mutation);
        this.bestScoreHistory.add(this.getBest().score());
        this.meanScoreHistory.add(this.getMeanScore());
    }

    public T getBest() {
        return (T) Laboratory.getBest(population);
    }

    float getMeanScore() {
        Subject[] population2 = population.clone();
        float sum = 0f;
        int total = 0;
        for (Subject subject : population2)
            if (subject.valid()) {
                total++;
                sum += subject.score();
            }
        if (total == 0)
            return 0f;
        else
            return sum / (float) total;
    }

    public boolean shouldStop() {
        return stagnation > maxStagnation;
    }

    //class functions

    private static Subject getBest(Subject[] population) {
        Subject[] population2 = population.clone();
        Subject best = null;
        for (Subject subject : population2)
            if (best == null || (subject.valid() && subject.score() < best.score()))
                best = subject;
        return best;
    }

    private static Subject[] selection(Subject[] population) {
        Subject[] output = new Subject[population.length / 2];

        //random 'fights'
        int[] indexes = Utils.indexes(population.length);
        Utils.shuffle(indexes);

        float d0;
        float d1;
        for (int i = 0; i < population.length / 2; i++) {
            d0 = population[indexes[i * 2]].score();
            d1 = population[indexes[i * 2 + 1]].score();
            output[i] = population[indexes[d0 <= d1 ? i * 2 : i * 2 + 1]];
        }

        return output;
    }

    private static Subject[] breeding(Subject[] population, float mutation, boolean mutateOnlyChildren) {
        Subject[] output = new Subject[population.length * 2];

        //random 'breeds'
        int[] indexes = Utils.indexes(population.length);
        Utils.shuffle(indexes);

        for (int i = 0; i < population.length - 1; i += 2) {
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
            Subject best = Laboratory.getBest(output);
            for (Subject subject : output) {
                if (!subject.equals(best) && Utils.randBoolean(mutation)) {
                    subject.mutate(1);
                }
            }
        }

        return output;
    }
}
