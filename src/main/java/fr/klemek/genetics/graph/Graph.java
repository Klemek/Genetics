package fr.klemek.genetics.graph;

import fr.klemek.genetics.ConfigFile;
import fr.klemek.genetics.Pair;
import fr.klemek.genetics.Subject;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Graph implements Subject {

    private static final ConfigFile config = new ConfigFile("graph");

    private static final short maxX = config.getShort("MAX_X");
    private static final short maxY = config.getShort("MAX_Y");
    private static final short minDistance = config.getShort("MIN_DISTANCE");
    private static final boolean fullRandomMutation = config.getBoolean("FULL_RANDOM_MUTATION");
    private static final short mutationRange = config.getShort("MUTATION_RANGE");

    //variables

    private final short[][] positions;

    private int score;

    //constructor

    private Graph(boolean empty) {
        positions = new short[Utils.graphSize][2];
        if (!empty)
            for (int i = 0; i < positions.length; i++) {
                positions[i][0] = fr.klemek.genetics.Utils.randShort((short) 0, maxX);
                positions[i][1] = fr.klemek.genetics.Utils.randShort((short) 0, maxY);
            }
        score = -1;
    }

    public Graph() {
        this(false);
    }

    //accessors

    short[][] getPositions() {
        return positions;
    }

    private short[] getPosition(int i) {
        return positions[i];
    }

    private void setPosition(int i, short... pos) {
        this.positions[i] = pos;
    }

    //functions

    private int countCrossing() {
        short[][] positions2 = this.positions.clone();
        int crossing = 0;
        ArrayList<Pair<Pair<Integer>>> done = new ArrayList<>();
        for (int a = 0; a < positions2.length - 1; a++) {
            for (int b = a + 1; b < positions2.length; b++) {
                Pair<Integer> line1 = new Pair<>(a, b);
                if (Utils.connected(a, b))
                    for (int c = 0; c < positions2.length - 1; c++)
                        for (int d = c + 1; d < positions2.length; d++) {
                            if (Utils.connected(c, d) && fr.klemek.genetics.Utils.distinct(a, b, c, d)) {
                                Pair<Integer> line2 = new Pair<>(c, d);
                                if (!done.contains(new Pair<>(line1, line2))) {
                                    if (fr.klemek.genetics.Utils.intersect(positions2[a][0], positions2[a][1],
                                            positions2[b][0], positions2[b][1],
                                            positions2[c][0], positions2[c][1],
                                            positions2[d][0], positions2[d][1])) {
                                        crossing++;
                                    }
                                    done.add(new Pair<>(line1, line2));
                                }
                            }
                        }

            }
        }
        return crossing;
    }

    private int countTooClose() {
        int res = 0;
        for (int a = 0; a < positions.length - 1; a++)
            for (int b = a + 1; b < positions.length; b++)
                if (Utils.distance(positions[a][0], positions[a][1], positions[b][0], positions[b][1]) < minDistance)
                    res++;
        return res;
    }

    //interface methods

    @Override
    public float score() {
        if (score >= 0)
            return score;
        score = countCrossing() + countTooClose();
        return score;
    }

    @Override
    public void mutate(int level) {
        for (int i = 0; i < level; i++) {
            int n = ThreadLocalRandom.current().nextInt(positions.length);

            short[] pos = this.getPosition(n);

            short x;
            short y;
            if (fullRandomMutation) {
                x = fr.klemek.genetics.Utils.randShort((short) 0, maxX);
                y = fr.klemek.genetics.Utils.randShort((short) 0, maxY);
            } else {
                x = (short) Math.min(Math.max(pos[0] + fr.klemek.genetics.Utils.randShort((short) -mutationRange, mutationRange), 0), maxX);
                y = (short) Math.min(Math.max(pos[1] + fr.klemek.genetics.Utils.randShort((short) -mutationRange, mutationRange), 0), maxY);
            }


            this.setPosition(n, x, y);
        }
        score = -1;
    }

    @Override
    public Subject[] createChildren(Subject arg0) {
        if (!(arg0 instanceof Graph))
            return new Graph[0];

        Graph other = (Graph) arg0;

        Graph[] children = new Graph[]{new Graph(true), new Graph(true)};

        //generate cuts
        int cut1 = ThreadLocalRandom.current().nextInt(0, Utils.graphSize - 1);
        int cut2 = ThreadLocalRandom.current().nextInt(cut1 + 1, Utils.graphSize);

        for (int i = 0; i < Utils.graphSize; i++) {
            if (i < cut1 || i >= cut2) {
                children[0].setPosition(i, this.getPosition(i).clone());
                children[1].setPosition(i, other.getPosition(i).clone());
            } else {
                children[0].setPosition(i, other.getPosition(i).clone());
                children[1].setPosition(i, this.getPosition(i).clone());
            }
        }

        return children;
    }
}
