package fr.klemek.genetics.salesman;

import fr.klemek.genetics.ConfigFile;
import fr.klemek.genetics.Pair;

import java.util.HashMap;

final class Utils extends fr.klemek.genetics.Utils {

    private static final ConfigFile config = new ConfigFile("salesman");
    private static final boolean approximate = config.getBoolean("APPROXIMATE");
    private static final float LATITUTE_FACTOR = 110.574f;

    /*
     * CITY DATA
     */
    private static final float LONGITUTE_FACTOR = 111.320f;
    private static final float LATITUTE_FACTOR_APPROX = 918f / 11.93f;
    private static final float LONGITUTE_FACTOR_APPROX = 881f / 7.98f;

    private static final HashMap<Pair<Byte>, Float> distances = new HashMap<>();
    private static final int EARTH_RADIUS = 6371; //km
    static int dataSize;
    static String[] cityNames;
    private static float[][] cityCoords;

    private Utils() {

    }

    static float geoDistance(float[] coords1, float[] coords2) {
        if (approximate)
            return Utils.distance(coordinatesToKm(coords1, true), coordinatesToKm(coords2, true));

        double phi1 = Math.toRadians(coords1[0]);
        double phi2 = Math.toRadians(coords2[0]);
        double dPhi = Math.toRadians(coords2[0] - coords1[0]);
        double dLambda = Math.toRadians(coords2[1] - coords1[1]);
        double a = Math.sin(dPhi / 2d) * Math.sin(dPhi / 2d)
                + Math.cos(phi1) * Math.cos(phi2)
                * Math.sin(dLambda / 2d) * Math.sin(dLambda / 2d);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (float) (EARTH_RADIUS * c);
    }

    private static float[] coordinatesToKm(float[] coordinates, boolean approximate) {
        float x;
        float y;
        if (approximate) {
            x = coordinates[0] * LATITUTE_FACTOR_APPROX;
            y = coordinates[1] * LONGITUTE_FACTOR_APPROX;
        } else {
            x = coordinates[0] * LATITUTE_FACTOR;
            y = (float) (coordinates[1] * LONGITUTE_FACTOR * Math.cos(Math.toRadians(coordinates[0])));
        }
        return new float[]{x, y};
    }

    static float distanceBetweenCities(byte city1, byte city2) {
        Pair<Byte> key = new Pair<>(city1, city2);
        if (!distances.containsKey(key))
            distances.put(key, Utils.geoDistance(cityCoords[city1], cityCoords[city2]));
        return distances.get(key);
    }

    static void loadData() {
        String[][] data = Utils.readCSV(config.get("DATA_FILE"));
        Utils.dataSize = data.length;
        Utils.cityNames = new String[data.length];
        Utils.cityCoords = new float[data.length][];
        for (int i = 0; i < data.length; i++) {
            Utils.cityNames[i] = data[i][0];
            Utils.cityCoords[i] = new float[]{Float.parseFloat(data[i][1]), Float.parseFloat(data[i][2])};
        }
    }

    static void loadDistances() {
        for (byte city1 = 0; city1 < Utils.dataSize - 1; city1++) {
            for (byte city2 = (byte) (city1 + 1); city2 < Utils.dataSize; city2++) {
                distanceBetweenCities(city1, city2);
            }
        }
    }

    private static final HashMap<Byte, float[]> positions = new HashMap<>();

    static float[] cityPosition(byte city) {
        if (!positions.containsKey(city))
            positions.put(city, coordinatesToKm(cityCoords[city], approximate));
        return positions.get(city);
    }
}
