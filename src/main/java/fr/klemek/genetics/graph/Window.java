package fr.klemek.genetics.graph;

import fr.klemek.genetics.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

class Window extends JFrame implements LabWindow<Graph> {

    private static final ConfigFile config = new ConfigFile("graph.properties");

    private static final float graphMargin = config.getFloat("GRAPH_MARGIN");
    private static final int frameWait = 1000 / config.getInt("FRAME_PER_SECOND");
    private static final int generationPerSecond = config.getInt("GENERATION_PER_SECOND");
    private static final int threadSleep = generationPerSecond <= 0 ? 0 : (1000 / generationPerSecond);
    private static final float maxX = config.getFloat("MAX_X");
    private static final float maxY = config.getFloat("MAX_Y");

    private LabPanel<Graph> p;

    private Window(Laboratory<Graph> lab) {
        this.setLocation(0, 0);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setTitle("Non-intersecting graph solution by genetics");

        this.setLaboratory(lab);
    }

    private void setLaboratory(Laboratory<Graph> lab) {
        if (p != null)
            this.remove(p);
        p = new LabPanel<>(lab, this);
        this.add(p);
        this.pack();
        this.setVisible(true);
    }

    @Override
    public String getBestDescription(Graph best) {
        return String.format("%.2f", best.score());
    }

    public static void main(String[] args) throws InterruptedException {

        Utils.loadData();

        Laboratory<Graph> lab = new Laboratory<>(Graph.class, new LaboratoryParameters(config));

        Window w = new Window(lab);

        long t0 = System.currentTimeMillis();

        while (!lab.shouldStop() && lab.getGeneration() < 1000) {
            lab.nextGeneration();
            if (System.currentTimeMillis() > t0 + frameWait) {
                t0 = System.currentTimeMillis();
                w.repaint();
                Thread.sleep(3); //avoid visual glitches
            }

            if (generationPerSecond > 0) {
                Thread.sleep(threadSleep);
            }
        }
    }

    @Override
    public void paintCustom(Graphics2D g2, int w, int h, Graph graph) {
        float scale = Math.max(maxY / (h * (1f - 2 * graphMargin)), maxX / (w * (1f - 2 * graphMargin)));

        short[][] positions = graph.getPositions().clone();

        g2.setColor(config.getColor("GRAPH_COLOR"));

        int[] pos;
        int[] pos2;

        for (int i = 0; i < positions.length - 1; i++) {
            pos = getPosition(w, h, positions[i], scale);
            for (int j = i + 1; j < positions.length; j++) {
                pos2 = getPosition(w, h, positions[j], scale);
                if (Utils.connected(i, j))
                    g2.drawLine(pos[0], pos[1], pos2[0], pos2[1]);
            }
        }

        g2.setColor(config.getColor("POINTS_COLOR"));

        for (int i = 0; i < positions.length; i++) {
            pos = getPosition(w, h, positions[i], scale);
            g2.fillOval(pos[0] - 2, pos[1] - 2, 5, 5);

            Rectangle2D stringBounds = g2.getFontMetrics().getStringBounds(i + "", g2);
            g2.drawString(i + "", (int) (pos[0] - (stringBounds.getWidth()) / 2), (int) (pos[1] - stringBounds.getHeight()));
        }
    }

    private int[] getPosition(int w, int h, short[] position, float scale) {
        int wCenterX = w / 2;
        int wCenterY = h / 2;
        int x = (int) Math.round(wCenterX + (position[0] - maxX / 2d) / scale);
        int y = (int) Math.round(wCenterY + (position[1] - maxY / 2d) / scale);
        return new int[]{x, y};
    }
}
