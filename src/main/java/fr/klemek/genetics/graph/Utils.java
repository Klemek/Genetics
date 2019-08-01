package fr.klemek.genetics.graph;

import fr.klemek.genetics.ConfigFile;

final class Utils extends fr.klemek.genetics.Utils {

    private static final ConfigFile config = new ConfigFile("graph");
    static int graphSize;

    /*
     * GRAPH DATA
     */
    private static int[][] graphData;

    private Utils() {

    }

    static void loadData() {
        String[][] data = Utils.readCSV(config.get("DATA_FILE"));
        Utils.graphSize = data.length;
        Utils.graphData = new int[data.length][data.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < Math.min(data[i].length, data.length); j++) {
                Utils.graphData[i][j] = Integer.parseInt(data[i][j]);
            }
        }
    }

    static boolean connected(int a, int b) {
        return graphData[a][b] > 0;
    }

}
