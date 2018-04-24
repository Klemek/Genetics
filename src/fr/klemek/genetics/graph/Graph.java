package fr.klemek.genetics.graph;

import fr.klemek.genetics.Pair;
import fr.klemek.genetics.Subject;
import fr.klemek.genetics.Utils;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Graph implements Subject {

    //variables

    private final short[][] positions;

    private int score;

    //constructor

    Graph(boolean empty) {
        positions = new short[Data.GRAPH_SIZE][2];
        if (!empty)
            for (int i = 0; i < positions.length; i++) {
                positions[i][0] = Utils.randShort((short) 0, Data.MAX_X);
                positions[i][1] = Utils.randShort((short) 0, Data.MAX_Y);
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
                if (Data.connected(a, b))
                    for (int c = 0; c < positions2.length - 1; c++)
                        for (int d = c + 1; d < positions2.length; d++) {
                            if (Data.connected(c, d) && Utils.distinct(a, b, c, d)) {
                                Pair<Integer> line2 = new Pair<>(c, d);
                                if (!done.contains(new Pair<>(line1, line2))) {
                                    if (Utils.intersect(positions2[a][0], positions2[a][1],
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
                if (Utils.distance(positions[a][0], positions[a][1], positions[b][0], positions[b][1]) < Data.MIN_DISTANCE)
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
            if (Data.FULL_RANDOM_MUTATION) {
                x = Utils.randShort((short) 0, Data.MAX_X);
                y = Utils.randShort((short) 0, Data.MAX_Y);
            } else {
                x = (short) Math.min(Math.max(pos[0] + Utils.randShort((short) -Data.MUTATION_RANGE, Data.MUTATION_RANGE), 0), Data.MAX_X);
                y = (short) Math.min(Math.max(pos[1] + Utils.randShort((short) -Data.MUTATION_RANGE, Data.MUTATION_RANGE), 0), Data.MAX_Y);
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
        int cut1 = ThreadLocalRandom.current().nextInt(0, Data.GRAPH_SIZE - 1);
        int cut2 = ThreadLocalRandom.current().nextInt(cut1 + 1, Data.GRAPH_SIZE);

        for (int i = 0; i < Data.GRAPH_SIZE; i++) {
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
