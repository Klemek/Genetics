package fr.klemek.genetics.graph;

import fr.klemek.genetics.Laboratory;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

class Panel extends JPanel {

    //variables


    private int w;
    private int h;

    private final transient Laboratory<Graph> lab;

    //constructor

    Panel(Laboratory<Graph> lab) {
        this.lab = lab;
        this.setPreferredSize(new Dimension(Data.DEFAULT_WIDTH, Data.DEFAULT_HEIGHT));
        this.setBackground(Data.BACKGROUND_COLOR);
    }

    //functions

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        this.w = this.getWidth();
        if (this.w == 0)
            this.w = Data.DEFAULT_WIDTH;
        this.h = this.getHeight();
        if (this.h == 0)
            this.h = Data.DEFAULT_HEIGHT;

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
        float scale = Math.max(Data.MAX_Y / (h * (1f - 2 * Data.GRAPH_MARGIN)), Data.MAX_X / (w * (1f - 2 * Data.GRAPH_MARGIN)));

        Graph graph = lab.getBest();
        short[][] positions = graph.getPositions().clone();

        int f = g2.getFont().getSize();

        int k = 0;

        g2.drawString(String.format("Generation %d", lab.getGeneration()), 10, 10 + (k++) * f);

        g2.setColor(Data.TEXT_MUTATION_COLOR);
        g2.drawString(String.format("Mutation : %.2f%%", lab.getCurrentMutation() * 100), 10, 10 + (k++) * f);

        g2.setColor(Data.TEXT_MEAN_COLOR);
        g2.drawString(String.format("Mean : %.2f", lab.getCurrentMeanScore()), 10, 10 + (k++) * f);

        g2.setColor(Data.TEXT_OPTIMAL_COLOR);
        g2.drawString(String.format("Optimal : %.2f (generation %d)", graph.score(), lab.getBestGeneration()), 10, 10 + (k++) * f);

        g2.setColor(Data.TEXT_INFO_COLOR);

        g2.drawString(String.format("Population : %d", lab.getParams().populationSize), 10, 10 + (k++) * f);
        g2.drawString(String.format("High mutation after %d same gen.", lab.getParams().highStagnation), 10, 10 + (k++) * f);
        g2.drawString(String.format("Stop after %d same gen.", lab.getParams().maxStagnation), 10, 10 + (k++) * f);
        g2.drawString(lab.getParams().mutateOnlyChildren ? "Mutate only children" : "Mutate everyone but best", 10, 10 + (k++) * f);

        g2.setColor(Data.GRAPH_COLOR);

        int[] pos;
        int[] pos2;

        for (int i = 0; i < positions.length - 1; i++) {
            pos = getPosition(positions[i], scale);
            for (int j = i + 1; j < positions.length; j++) {
                pos2 = getPosition(positions[j], scale);
                if (Data.connected(i, j))
                    g2.drawLine(pos[0], pos[1], pos2[0], pos2[1]);
            }
        }

        g2.setColor(Data.POINTS_COLOR);

        for (int i = 0; i < positions.length; i++) {
            pos = getPosition(positions[i], scale);
            g2.fillOval(pos[0] - 2, pos[1] - 2, 5, 5);

            Rectangle2D stringBounds = g2.getFontMetrics().getStringBounds(i + "", g2);
            g2.drawString(i + "", (int) (pos[0] - (stringBounds.getWidth()) / 2), (int) (pos[1] - stringBounds.getHeight()));
        }


    }

    private int[] getPosition(short[] position, float scale) {
        int wCenterX = w / 2;
        int wCenterY = h / 2;
        int x = (int) Math.round(wCenterX + (position[0] - Data.MAX_X / 2d) / scale);
        int y = (int) Math.round(wCenterY + (position[1] - Data.MAX_Y / 2d) / scale);
        return new int[]{x, y};
    }

}
