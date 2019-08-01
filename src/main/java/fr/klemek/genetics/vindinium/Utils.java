package fr.klemek.genetics.vindinium;

import fr.klemek.genetics.ConfigFile;

import java.awt.*;
import java.util.Arrays;

final class Utils extends fr.klemek.genetics.Utils {

    private static final ConfigFile config = new ConfigFile("vindinium");
    private static final float mutation = config.getFloat("MUTATION");
    /*
     * CITY DATA
     */

    static int dataSize;
    static String[] valuesNames;
    static float[][] valuesBounds;
    static Color[] valuesColors;

    private Utils() {

    }

    static void loadData() {
        String[][] data = Utils.readCSV(config.get("DATA_FILE"));
        Utils.dataSize = data.length;
        Utils.valuesNames = new String[data.length];
        Utils.valuesBounds = new float[data.length][2];
        Utils.valuesColors = new Color[data.length];
        for (int i = 0; i < data.length; i++) {
            Utils.valuesNames[i] = data[i][0];
            Utils.valuesBounds[i] = new float[]{Float.parseFloat(data[i][1]), Float.parseFloat(data[i][2])};
            Utils.valuesColors[i] = Utils.colorFromHex(data[i][3]);
        }
    }

    static float[] randomValues() {
        float[] output = new float[Utils.dataSize];
        for (int i = 0; i < Utils.dataSize; i++) {
            output[i] = Utils.randFloat(valuesBounds[i][0], valuesBounds[i][1]);
        }
        return output;
    }

    static float mutate(int pos, float val) {
        float[] bounds = Utils.valuesBounds[pos];
        float range = mutation * (bounds[1] - bounds[0]);
        return Math.max(bounds[0], Math.min(bounds[1], val + Utils.randFloat(-range / 2, range / 2)));
    }


    public static void main(String... args) {
        loadData();
        System.out.println(Arrays.toString(randomValues()));
    }
}
