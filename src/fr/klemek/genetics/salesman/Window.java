package fr.klemek.genetics.salesman;

import fr.klemek.genetics.LabPanel;
import fr.klemek.genetics.LabWindow;
import fr.klemek.genetics.Laboratory;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

class Window extends JFrame implements LabWindow<Salesman> {

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

    @Override
    public void paintCustom(Graphics2D g2, int w, int h, Salesman salesman) {
        float scale;
        if (w / (maxX - minX) > h / (maxY - minY)) {
            scale = (maxY - minY) / (h * (1f - 2 * Data.GRAPH_MARGIN));
        } else {
            scale = (maxX - minX) / (w * (1f - 2 * Data.GRAPH_MARGIN));
        }

        int[] pos;

        g2.setColor(Data.SALESMAN_COLOR);
        g2.setStroke(new BasicStroke(2));
        int[] lastPos = null;
        for (byte c : salesman.getPath()) {
            pos = getCityPosition(w, h, c, scale);
            if (lastPos != null)
                g2.drawLine(lastPos[0], lastPos[1], pos[0], pos[1]);
            lastPos = pos;
        }
        g2.setStroke(new BasicStroke(1));

        g2.setColor(Data.CITIES_COLOR);
        for (byte c = 0; c < Data.DATA_SIZE; c++) {
            pos = getCityPosition(w, h, c, scale);
            g2.fillOval(pos[0] - 2, pos[1] - 2, 5, 5);

            Rectangle2D stringBounds = g2.getFontMetrics().getStringBounds(Data.CITY_NAMES[c], g2);
            g2.drawString(Data.CITY_NAMES[c], (int) (pos[0] - (stringBounds.getWidth()) / 2), (int) (pos[1] - stringBounds.getHeight()));
        }
    }

    private int[] getCityPosition(int w, int h, byte city, float scale) {
        int wCenterX = w / 2;
        int wCenterY = h / 2;
        int x = Math.round(wCenterX + (Data.cityPosition(city)[1] - centerX) / scale);
        int y = Math.round(wCenterY + (Data.cityPosition(city)[0] - centerY) / -scale);
        return new int[]{x, y};
    }

    public static void main(String[] args) throws InterruptedException {

        Laboratory<Salesman> lab;
        Window w = null;

        Data.loadDistances();

        do {
            lab = new Laboratory<>(Salesman.class, Data.PARAMETERS);

            if (w == null)
                w = new Window(lab);
            else
                w.setLaboratory(lab);

            long t0 = System.currentTimeMillis();

            while (!lab.shouldStop()) {
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
        } while (Data.RELOAD_THRESHOLD > 0 && lab.getBest().score() >= Data.RELOAD_THRESHOLD);

    }
}
