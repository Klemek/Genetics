package fr.klemek.genetics.salesman;

import fr.klemek.genetics.Pair;
import fr.klemek.genetics.Utils;

import java.awt.*;
import java.util.HashMap;

final class Data {

    private Data() {

    }

    /*
     * PARAMETERS
     */

    private final static boolean APROXIMATE = false;

    public final static int POPULATION_SIZE = 10000;
    public final static float DEFAULT_MUTATION = .3f;
    public final static int HIGH_STAGNATION = 25;
    public final static int MAX_STAGNATION = 50;
    public final static boolean MUTATE_ONLY_CHILDREN = true;

    public final static float RELOAD_THRESOLD = 3095.95f; //0 for once or set for retry on fail

    private final static int FRAME_PER_SECOND = 60; //approximate
    public final static float GENERATION_PER_SECOND = -1; //-1 for infinite

    public final static int FRAME_WAIT = 1000 / FRAME_PER_SECOND;
    public final static int THREAD_SLEEP = GENERATION_PER_SECOND <= 0 ? 0 : (int) (1000 / GENERATION_PER_SECOND);

    /*
     * CITY DATA
     */

    public final static int DATA_SIZE = 20;

    public final static String[] CITY_NAMES = {
            "NICE",            // 0
            "MARSEILLE",    // 1
            "CAEN",            // 2
            "BREST",        // 3
            "TOULOUSE",        // 4
            "BORDEAUX",        // 5
            "RENNES",        // 6
            "GRENOBLE",        // 7
            "NANTES",        // 8
            "ANGERS",        // 9
            "NANCY",        // 10
            "METZ",            // 11
            "LILLE",        // 12
            "BIARRITZ",        // 13
            "PERPIGNAN",    // 14
            "STRASBOURG",    // 15
            "LYON",            // 16
            "LE-MANS",        // 17
            "PARIS",        // 18
            "ROUEN"       // 19
    };

    private final static float[][] CITY_COORDINATES = {
            {43.42f, 7.16f},        // 1 : NICE
            {43.18f, 5.22f},        // 2 : MARSEILLE
            {49.11f, -0.62f},        // 3 : CAEN
            {48.4f, -4.48f},    // 4 : BREST
            {43.37f, 1.27f},        // 5 : TOULOUSE
            {44.50f, -0.74f},    // 6 : BORDEAUX
            {48.06f, -1.8f},        // 7 : RENNES
            {45.11f, 5.43f},        // 8 : GRENOBLE
            {47.14f, -1.75f},    // 9 : NANTES
            {47.29f, -0.72f},    // 10 : ANGERS
            {48.42f, 6.12f},        // 11 : NANCY
            {49.07f, 6.11f},        // 12 : METZ
            {50.39f, 3.05f},        // 13 : LILLE
            {43.48f, -1.56f},        // 14 : BIARRITZ
            {42.42f, 2.54f},        // 15 : PERPIGNAN
            {48.35f, 7.45f},        // 16 : STRASBOURG
            {45.46f, 4.50f},        // 17 : LYON
            {48.00f, 0.12f},        // 18 : LE-MANS
            {48.52f, 2.20f},        // 19 : PARIS
            {49.26f, 1.05f}        // 20 : ROUEN
    };

    private final static HashMap<Pair<Byte>, Float> distances = new HashMap<>();

    public static float distanceBetweenCities(byte city1, byte city2) {
        Pair<Byte> key = new Pair<>(city1, city2);
        if (!distances.containsKey(key))
            distances.put(key, Utils.geoDistance(CITY_COORDINATES[city1], CITY_COORDINATES[city2], APROXIMATE));
        return distances.get(key);
    }

    public static void loadDistances() {
        for (byte city1 = 0; city1 < Data.DATA_SIZE - 1; city1++) {
            for (byte city2 = (byte) (city1 + 1); city2 < Data.DATA_SIZE; city2++) {
                distanceBetweenCities(city1, city2);
            }
        }
    }

    private final static HashMap<Byte, float[]> positions = new HashMap<>();

    public static float[] cityPosition(byte city) {
        if (!positions.containsKey(city))
            positions.put(city, Utils.coordinatesToKm(CITY_COORDINATES[city], APROXIMATE));
        return positions.get(city);
    }

    /*
     * COLORS
     */

    public static final Color BACKGROUND_COLOR = Color.WHITE;

    public static final Color CITIES_COLOR = Utils.colorFromHex("#263238");

    public static final Color SALESMAN_COLOR = Utils.colorFromHex("#37474F");

    public static final Color TEXT_OPTIMAL_COLOR = Utils.colorFromHex("#F44336");
    public static final Color GRAPH_OPTIMAL_COLOR = Utils.colorFromHex("#EF5350");

    public static final Color TEXT_MEAN_COLOR = Utils.colorFromHex("#E91E63");
    public static final Color GRAPH_MEAN_COLOR = Utils.colorFromHex("#EC407A");

    public static final Color TEXT_MUTATION_COLOR = Utils.colorFromHex("#4CAF50");
    public static final Color GRAPH_MUTATION_COLOR = Utils.colorFromHex("#66BB6A");

    public static final Color TEXT_INFO_COLOR = Utils.colorFromHex("#9E9E9E");
}
