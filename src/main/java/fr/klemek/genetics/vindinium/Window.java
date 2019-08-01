package fr.klemek.genetics.vindinium;

import fr.klemek.genetics.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class Window extends JFrame implements LabWindow<Bot> {

    private static final ConfigFile config = new ConfigFile("vindinium");

    private static final int frameWait = 1000 / config.getInt("FRAME_PER_SECOND");
    private static final int generationPerSecond = config.getInt("GENERATION_PER_SECOND");
    private static final int threadSleep = generationPerSecond <= 0 ? 0 : (1000 / generationPerSecond);

    private LabPanel<Bot> p;
    private Laboratory<Bot> lab;
    private HashMap<Integer, List<Float>> history = new HashMap<>();
    private Bot best = null;

    private Window(Laboratory<Bot> lab) {
        this.setLocation(0, 0);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setTitle("Vindinium bot evolution solution by genetics");

        this.setLaboratory(lab);

        for (int i = 0; i < Utils.dataSize; i++) {
            history.put(i, new ArrayList<>());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Utils.loadData();

        Laboratory<Bot> lab = new Laboratory<>(Bot.class, new LaboratoryParameters(config));
        Window w = new Window(lab);

        long t0 = System.currentTimeMillis();

        while (!lab.shouldStop()) {
            Subject[] pop = lab.getPopulation();
            Bot.Competition.brawl(Arrays.copyOf(pop, pop.length, Bot[].class));
            System.out.println(w.getBestDescription(lab.getBest()));
            w.updateHistory();
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
        System.out.println("lab stopped");
    }

    private void setLaboratory(Laboratory<Bot> lab) {
        this.lab = lab;
        if (p != null)
            this.remove(p);
        p = new LabPanel<>(lab, this);
        this.add(p);
        this.pack();
        this.setVisible(true);
    }

    private void updateHistory() {
        best = lab.getBest();
        for (int i = 0; i < Utils.dataSize; i++) {
            history.get(i).add(best.getValues()[i]);
        }
    }

    @Override
    public String getBestDescription(Bot best) {
        return String.format("score:%.2f values:%s", best.score(), best);
    }

    @Override
    public void paintCustom(Graphics2D g2, int w, int h, Bot bot) {
        int f = g2.getFont().getSize();
        int k = 10;
        if (best != null) {
            for (int i = 0; i < Utils.dataSize; i++) {
                g2.setColor(Utils.valuesColors[i]);
                paintGraph(g2, history.get(i), Utils.valuesBounds[i][1], w, h);
            }
            for (int i = 0; i < Utils.dataSize; i++) {
                g2.setColor(Utils.valuesColors[i]);
                g2.drawString(String.format("%s = %.2f", Utils.valuesNames[i], best.getValues()[i]), 20, 10 + (k++) * f);
            }
        }
    }

    private void paintGraph(Graphics2D g2, List<Float> graph, double max, int w, int h) {
        int size = graph.size();
        if (size < 2)
            return;
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
