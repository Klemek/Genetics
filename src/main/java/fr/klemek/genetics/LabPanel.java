package fr.klemek.genetics;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LabPanel<T extends Subject> extends JPanel {

    //variables

    private int w;
    private int h;

    private final transient Laboratory<T> lab;
    private final transient LabWindow<T> window;

    private final ConfigFile config = new ConfigFile("config.properties");

    //constructor

    public LabPanel(Laboratory<T> lab, LabWindow<T> window) {
        this.lab = lab;
        this.window = window;
        this.setPreferredSize(new Dimension(config.getInt("DEFAULT_WIDTH"), config.getInt("DEFAULT_HEIGHT")));
        this.setBackground(config.getColor("BACKGROUND_COLOR"));
    }

    //functions

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        this.w = this.getWidth();
        if (this.w == 0)
            this.w = config.getInt("DEFAULT_WIDTH");
        this.h = this.getHeight();
        if (this.h == 0)
            this.h = config.getInt("DEFAULT_HEIGHT");

        g2.setColor(config.getColor("GRAPH_MUTATION_COLOR"));
        paintGraph(g2, lab.getMutationHistory(), 1f);
        g2.setColor(config.getColor("GRAPH_MEAN_COLOR"));
        paintGraph(g2, lab.getMeanScoreHistory(), lab.getMeanScoreHistory().get(0));
        g2.setColor(config.getColor("GRAPH_OPTIMAL_COLOR"));
        paintGraph(g2, lab.getBestScoreHistory(), lab.getMeanScoreHistory().get(0));

        Font font = new Font("Arial", Font.BOLD, 12);
        g2.setFont(font);

        window.paintCustom(g2, w, h, lab.getBest());

        int f = g2.getFont().getSize();

        int k = 0;

        g2.drawString(String.format("Generation %d", lab.getGeneration()), 10, 10 + (k++) * f);

        g2.setColor(config.getColor("TEXT_MUTATION_COLOR"));
        g2.drawString(String.format("Mutation : %.2f%%", lab.getCurrentMutation() * 100), 10, 10 + (k++) * f);

        g2.setColor(config.getColor("TEXT_MEAN_COLOR"));
        g2.drawString(String.format("Mean : %.2f", lab.getCurrentMeanScore()), 10, 10 + (k++) * f);

        g2.setColor(config.getColor("TEXT_OPTIMAL_COLOR"));
        g2.drawString(String.format("Optimal : %s (generation %d)", window.getBestDescription(lab.getBest()), lab.getBestGeneration()), 10, 10 + (k++) * f);

        g2.setColor(config.getColor("TEXT_INFO_COLOR"));

        g2.drawString(String.format("Population : %d", lab.getParams().populationSize), 10, 10 + (k++) * f);
        g2.drawString(String.format("High mutation after %d same gen.", lab.getParams().highStagnation), 10, 10 + (k++) * f);
        g2.drawString(String.format("Stop after %d same gen.", lab.getParams().maxStagnation), 10, 10 + (k++) * f);
        g2.drawString(lab.getParams().mutateOnlyChildren ? "Mutate only children" : "Mutate everyone but best", 10, 10 + (k) * f);
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

}
