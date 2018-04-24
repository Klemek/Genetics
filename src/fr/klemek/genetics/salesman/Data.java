package fr.klemek.genetics.salesman;

import fr.klemek.genetics.LaboratoryParameters;
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

    private static final boolean APPROXIMATE = false;

    static final LaboratoryParameters PARAMETERS = new LaboratoryParameters(
            10000, //Population size
            true, //Best lowest
            true, //Mutate only children
            3.f, //Default mutation
            25, //High stagnation
            50, //Max stagnation
            LaboratoryParameters.NO_THRESHOLD, //Stop threshold
            false); //Verbose

    static final float RELOAD_THRESHOLD = 3095.95f; //0 for once or set for retry on fail

    private static final int FRAME_PER_SECOND = 60; //approximate
    static final float GENERATION_PER_SECOND = -1; //-1 for infinite

    static final int FRAME_WAIT = 1000 / FRAME_PER_SECOND;
    static final int THREAD_SLEEP = GENERATION_PER_SECOND <= 0 ? 0 : (int) (1000 / GENERATION_PER_SECOND);

    /*
     * CITY DATA
     */

    static final int DATA_SIZE = 20;

    static final String[] CITY_NAMES = {
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

    private static final float[][] CITY_COORDINATES = {
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

    private static final HashMap<Pair<Byte>, Float> distances = new HashMap<>();

    static float distanceBetweenCities(byte city1, byte city2) {
        Pair<Byte> key = new Pair<>(city1, city2);
        if (!distances.containsKey(key))
            distances.put(key, Utils.geoDistance(CITY_COORDINATES[city1], CITY_COORDINATES[city2], APPROXIMATE));
        return distances.get(key);
    }

    static void loadDistances() {
        for (byte city1 = 0; city1 < Data.DATA_SIZE - 1; city1++) {
            for (byte city2 = (byte) (city1 + 1); city2 < Data.DATA_SIZE; city2++) {
                distanceBetweenCities(city1, city2);
            }
        }
    }

    private static final HashMap<Byte, float[]> positions = new HashMap<>();

    static float[] cityPosition(byte city) {
        if (!positions.containsKey(city))
            positions.put(city, Utils.coordinatesToKm(CITY_COORDINATES[city], APPROXIMATE));
        return positions.get(city);
    }

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

    static final Color CITIES_COLOR = Utils.colorFromHex("#263238");

    static final Color SALESMAN_COLOR = Utils.colorFromHex("#37474F");

    static final Color TEXT_OPTIMAL_COLOR = Utils.colorFromHex("#F44336");
    static final Color GRAPH_OPTIMAL_COLOR = Utils.colorFromHex("#EF5350");

    static final Color TEXT_MEAN_COLOR = Utils.colorFromHex("#E91E63");
    static final Color GRAPH_MEAN_COLOR = Utils.colorFromHex("#EC407A");

    static final Color TEXT_MUTATION_COLOR = Utils.colorFromHex("#4CAF50");
    static final Color GRAPH_MUTATION_COLOR = Utils.colorFromHex("#66BB6A");

    static final Color TEXT_INFO_COLOR = Utils.colorFromHex("#9E9E9E");
}
