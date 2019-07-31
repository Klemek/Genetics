package fr.klemek.genetics.salesman;

import fr.klemek.genetics.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

class Window extends JFrame implements LabWindow<Salesman> {

    private static final ConfigFile config = new ConfigFile("salesman.properties");

    private static final float graphMargin = config.getFloat("GRAPH_MARGIN");
    private static final int frameWait = 1000 / config.getInt("FRAME_PER_SECOND");
    private static final int generationPerSecond = config.getInt("GENERATION_PER_SECOND");
    private static final int threadSleep = generationPerSecond <= 0 ? 0 : (1000 / generationPerSecond);
    private static final float reloadThreshold = config.getFloat("RELOAD_THRESHOLD");

    private LabPanel<Salesman> p;

    private float centerX;
    private float centerY;
    private float minX;
    private float maxX;
    private float minY;
    private float maxY;

    private Window(Laboratory<Salesman> lab) {
        this.setLocation(0, 0);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setTitle("Traveling Salesman solution by genetics");

        this.setLaboratory(lab);
    }

    private void setLaboratory(Laboratory<Salesman> lab) {
        if (p != null)
            this.remove(p);
        p = new LabPanel<>(lab, this);
        this.computeData();
        this.add(p);
        this.pack();
        this.setVisible(true);
    }

    @Override
    public String getBestDescription(Salesman best) {
        return String.format("%.2f", best.score());
    }

    public static void main(String[] args) throws InterruptedException {

        Laboratory<Salesman> lab;
        Window w = null;

        Utils.loadData();
        Utils.loadDistances();

        do {
            lab = new Laboratory<>(Salesman.class, new LaboratoryParameters(config));

            if (w == null)
                w = new Window(lab);
            else
                w.setLaboratory(lab);

            long t0 = System.currentTimeMillis();

            while (!lab.shouldStop()) {
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
        } while (reloadThreshold > 0 && lab.getBest().score() >= reloadThreshold);

    }

    private void computeData() {
        minX = Float.MAX_VALUE;
        maxX = Float.MIN_VALUE;
        minY = Float.MAX_VALUE;
        maxY = Float.MIN_VALUE;
        for (byte c = 0; c < Utils.dataSize; c++) {
            minX = Math.min(minX, Utils.cityPosition(c)[1]);
            maxX = Math.max(maxX, Utils.cityPosition(c)[1]);
            minY = Math.min(minY, Utils.cityPosition(c)[0]);
            maxY = Math.max(maxY, Utils.cityPosition(c)[0]);
        }
        centerX = (maxX + minX) / 2f;
        centerY = (maxY + minY) / 2f;
    }

    @Override
    public void paintCustom(Graphics2D g2, int w, int h, Salesman salesman) {
        float scale;
        if (w / (maxX - minX) > h / (maxY - minY)) {
            scale = (maxY - minY) / (h * (1f - 2 * graphMargin));
        } else {
            scale = (maxX - minX) / (w * (1f - 2 * graphMargin));
        }

        int[] pos;

        g2.setColor(config.getColor("SALESMAN_COLOR"));
        g2.setStroke(new BasicStroke(2));
        int[] lastPos = null;
        for (byte c : salesman.getPath()) {
            pos = getCityPosition(w, h, c, scale);
            if (lastPos != null)
                g2.drawLine(lastPos[0], lastPos[1], pos[0], pos[1]);
            lastPos = pos;
        }
        g2.setStroke(new BasicStroke(1));

        g2.setColor(config.getColor("CITIES_COLOR"));
        for (byte c = 0; c < Utils.dataSize; c++) {
            pos = getCityPosition(w, h, c, scale);
            g2.fillOval(pos[0] - 2, pos[1] - 2, 5, 5);

            Rectangle2D stringBounds = g2.getFontMetrics().getStringBounds(Utils.cityNames[c], g2);
            g2.drawString(Utils.cityNames[c], (int) (pos[0] - (stringBounds.getWidth()) / 2), (int) (pos[1] - stringBounds.getHeight()));
        }
    }

    private int[] getCityPosition(int w, int h, byte city, float scale) {
        int wCenterX = w / 2;
        int wCenterY = h / 2;
        int x = Math.round(wCenterX + (Utils.cityPosition(city)[1] - centerX) / scale);
        int y = Math.round(wCenterY + (Utils.cityPosition(city)[0] - centerY) / -scale);
        return new int[]{x, y};
    }
}
