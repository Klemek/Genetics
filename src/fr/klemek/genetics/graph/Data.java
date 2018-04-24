package fr.klemek.genetics.graph;

import fr.klemek.genetics.LaboratoryParameters;
import fr.klemek.genetics.Utils;

import java.awt.*;

final class Data {

    private Data() {

    }

    /*
     * PARAMETERS
     */

    static final LaboratoryParameters PARAMETERS = new LaboratoryParameters(
            32, //Population size
            true, //Best lowest
            true, //Mutate only children
            .3f, //Default mutation
            50, //High stagnation
            100, //Max stagnation
            0, //Stop threshold
            false); //Verbose

    private static final int FRAME_PER_SECOND = 60; //approximate
    static final float GENERATION_PER_SECOND = 20; //-1 for infinite

    static final int FRAME_WAIT = 1000 / FRAME_PER_SECOND;
    static final int THREAD_SLEEP = GENERATION_PER_SECOND <= 0 ? 0 : (int) (1000 / GENERATION_PER_SECOND);

    /*
     * GRAPH DATA
     */

    static final int GRAPH_SIZE = 8;

    private static final int[][] GRAPH_DATA = {
            //0 1  2  3  4  5  6  7
            {0, 1, 1, 0, 0, 0, 1, 0},    // 0
            {1, 0, 0, 1, 0, 0, 0, 1},    // 1
            {1, 0, 0, 1, 1, 0, 0, 0},    // 2
            {0, 1, 1, 0, 1, 1, 0, 0},    // 3
            {0, 0, 1, 1, 0, 1, 1, 0},    // 4
            {0, 0, 0, 1, 1, 0, 0, 1},    // 5
            {1, 0, 0, 0, 1, 0, 0, 1},    // 6
            {0, 1, 0, 0, 0, 1, 1, 0}};    // 7

    static boolean connected(int a, int b) {
        return GRAPH_DATA[a][b] > 0;
    }

    static final short MUTATION_RANGE = 300;

    static final short MIN_DISTANCE = 200;

    static final boolean FULL_RANDOM_MUTATION = true;

    static final short MAX_X = 1000;
    static final short MAX_Y = 1000;

    /*
     * PANEL
     */

    static final int DEFAULT_WIDTH = 800;
    static final int DEFAULT_HEIGHT = 600;

    static final float GRAPH_MARGIN = 0.1f;

    /*
     * COLORS
     */

    static final Color BACKGROUND_COLOR = Color.WHITE;

    static final Color POINTS_COLOR = Utils.colorFromHex("#263238");

    static final Color GRAPH_COLOR = Utils.colorFromHex("#37474F");

    static final Color TEXT_OPTIMAL_COLOR = Utils.colorFromHex("#F44336");
    static final Color GRAPH_OPTIMAL_COLOR = Utils.colorFromHex("#EF5350");

    static final Color TEXT_MEAN_COLOR = Utils.colorFromHex("#E91E63");
    static final Color GRAPH_MEAN_COLOR = Utils.colorFromHex("#EC407A");

    static final Color TEXT_MUTATION_COLOR = Utils.colorFromHex("#4CAF50");
    static final Color GRAPH_MUTATION_COLOR = Utils.colorFromHex("#66BB6A");

    static final Color TEXT_INFO_COLOR = Utils.colorFromHex("#9E9E9E");
}
