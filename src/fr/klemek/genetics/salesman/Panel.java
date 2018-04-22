package fr.klemek.genetics.salesman;

import fr.klemek.genetics.Laboratory;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

class Panel extends JPanel {

    //variables

    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;

    private static final float GRAPH_MARGIN = 0.1f;

    private int w;
    private int h;

    private float centerX;
    private float centerY;
    private float minX;
    private float maxX;
    private float minY;
    private float maxY;

    private final Laboratory<Salesman> lab;

    //constructor

    public Panel(Laboratory<Salesman> lab) {
        this.lab = lab;
        this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        this.setBackground(Data.BACKGROUND_COLOR);
        this.computeData();
    }

    //functions

    private void computeData() {
        minX = Float.MAX_VALUE;
        maxX = Float.MIN_VALUE;
        minY = Float.MAX_VALUE;
        maxY = Float.MIN_VALUE;
        for (byte c = 0; c < Data.DATA_SIZE; c++) {
            minX = Math.min(minX, Data.cityPosition(c)[1]);
            maxX = Math.max(maxX, Data.cityPosition(c)[1]);
            minY = Math.min(minY, Data.cityPosition(c)[0]);
            maxY = Math.max(maxY, Data.cityPosition(c)[0]);
        }
        centerX = (maxX + minX) / 2f;
        centerY = (maxY + minY) / 2f;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        this.w = this.getWidth();
        if (this.w == 0)
            this.w = DEFAULT_WIDTH;
        this.h = this.getHeight();
        if (this.h == 0)
            this.h = DEFAULT_HEIGHT;

        g2.setColor(Data.GRAPH_MUTATION_COLOR);
        paintGraph(g2, lab.getMutationHistory(), 1f);
        g2.setColor(Data.GRAPH_MEAN_COLOR);
        paintGraph(g2, lab.getMeanScoreHistory(), lab.getMeanScoreHistory().get(0));
        g2.setColor(Data.GRAPH_OPTIMAL_COLOR);
        paintGraph(g2, lab.getBestScoreHistory(), lab.getMeanScoreHistory().get(0));

        Font font = new Font("Arial", Font.BOLD, 12);
        g2.setFont(font);

        this.paintCitiesAndPath(g2);
    }

    private void paintGraph(Graphics2D g2, List<Float> graph, double max) {
        int size = graph.size();
        double xDiv = (size - 1) / (double) w;
        double yDiv = max / (double) h;

        int lastX = 0;
        int lastY = (int) Math.round(h - graph.get(0) / yDiv);
        int newX;
        int newY;

        for (int i = 1; i < size; i += 1) {
            newX = (int) (i / xDiv);
            newY = (int) Math.round(h - graph.get(i) / yDiv);
            g2.drawLine(lastX, lastY, newX, newY);
            lastX = newX;
            lastY = newY;
        }
    }

    private void paintCitiesAndPath(Graphics2D g2) {
        float scale;
        if (w / (maxX - minX) > h / (maxY - minY)) {
            scale = (maxY - minY) / (h * (1f - 2 * GRAPH_MARGIN));
        } else {
            scale = (maxX - minX) / (w * (1f - 2 * GRAPH_MARGIN));
        }


        int[] pos;

        int f = g2.getFont().getSize();

        Salesman salesman = lab.getBest();

        g2.setColor(Data.SALESMAN_COLOR);
        g2.setStroke(new BasicStroke(2));
        int[] lastPos = null;
        for (byte c : salesman.getPath()) {
            pos = getCityPosition(c, scale);
            if (lastPos != null)
                g2.drawLine(lastPos[0], lastPos[1], pos[0], pos[1]);
            lastPos = pos;
        }
        g2.setStroke(new BasicStroke(1));

        int k = 0;

        g2.drawString(String.format("Generation %d", lab.getGeneration()), 10, 10 + (k++) * f);

        g2.setColor(Data.TEXT_MUTATION_COLOR);
        g2.drawString(String.format("Mutation : %.2f%%", lab.getCurrentMutation() * 100), 10, 10 + (k++) * f);

        g2.setColor(Data.TEXT_MEAN_COLOR);
        g2.drawString(String.format("Mean : %.2f km", lab.getCurrentMeanScore()), 10, 10 + (k++) * f);

        g2.setColor(Data.TEXT_OPTIMAL_COLOR);
        g2.drawString(String.format("Optimal : %.2f km (generation %d)", salesman.score(), lab.getBestGeneration()), 10, 10 + (k++) * f);

        g2.setColor(Data.TEXT_INFO_COLOR);

        g2.drawString(String.format("Population : %d", lab.getPopulationSize()), 10, 10 + (k++) * f);
        g2.drawString(String.format("High mutation after %d same gen.", lab.getHighStagnation()), 10, 10 + (k++) * f);
        g2.drawString(String.format("Stop after %d same gen.", lab.getMaxStagnation()), 10, 10 + (k++) * f);
        g2.drawString(lab.mutateOnlyChildren() ? "Mutate only children" : "Mutate everyone but best", 10, 10 + (k++) * f);

        g2.setColor(Data.CITIES_COLOR);
        for (byte c = 0; c < Data.DATA_SIZE; c++) {
            pos = getCityPosition(c, scale);
            g2.fillOval(pos[0] - 2, pos[1] - 2, 5, 5);

            Rectangle2D stringBounds = g2.getFontMetrics().getStringBounds(Data.CITY_NAMES[c], g2);
            g2.drawString(Data.CITY_NAMES[c], (int) (pos[0] - (stringBounds.getWidth()) / 2), (int) (pos[1] - stringBounds.getHeight()));
        }


    }

    private int[] getCityPosition(byte city, float scale) {
        int wCenterX = w / 2;
        int wCenterY = h / 2;
        int x = Math.round(wCenterX + (Data.cityPosition(city)[1] - centerX) / scale);
        int y = Math.round(wCenterY + (Data.cityPosition(city)[0] - centerY) / -scale);
        return new int[]{x, y};
    }


}
