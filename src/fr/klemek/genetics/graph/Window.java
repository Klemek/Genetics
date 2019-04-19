package fr.klemek.genetics.graph;

import fr.klemek.genetics.LabPanel;
import fr.klemek.genetics.LabWindow;
import fr.klemek.genetics.Laboratory;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

class Window extends JFrame implements LabWindow<Graph> {

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

    @Override
    public void paintCustom(Graphics2D g2, int w, int h, Graph graph) {
        float scale = Math.max(Data.MAX_Y / (h * (1f - 2 * Data.GRAPH_MARGIN)), Data.MAX_X / (w * (1f - 2 * Data.GRAPH_MARGIN)));

        short[][] positions = graph.getPositions().clone();

        g2.setColor(Data.GRAPH_COLOR);

        int[] pos;
        int[] pos2;

        for (int i = 0; i < positions.length - 1; i++) {
            pos = getPosition(w, h, positions[i], scale);
            for (int j = i + 1; j < positions.length; j++) {
                pos2 = getPosition(w, h, positions[j], scale);
                if (Data.connected(i, j))
                    g2.drawLine(pos[0], pos[1], pos2[0], pos2[1]);
            }
        }

        g2.setColor(Data.POINTS_COLOR);

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
        int x = (int) Math.round(wCenterX + (position[0] - Data.MAX_X / 2d) / scale);
        int y = (int) Math.round(wCenterY + (position[1] - Data.MAX_Y / 2d) / scale);
        return new int[]{x, y};
    }

    public static void main(String[] args) throws InterruptedException {

        Laboratory<Graph> lab = new Laboratory<>(Graph.class, Data.PARAMETERS);

        Window w = new Window(lab);

        long t0 = System.currentTimeMillis();

        while (!lab.shouldStop() && lab.getGeneration() < 1000) {
            lab.nextGeneration();
            if (System.currentTimeMillis() > t0 + Data.FRAME_WAIT) {
                t0 = System.currentTimeMillis();
                w.repaint();
                Thread.sleep(3); //avoid visual glitches
            }

            if (Data.GENERATION_PER_SECOND > 0) {
                Thread.sleep(Data.THREAD_SLEEP);
            }
        }
    }
}
