package fr.klemek.genetics.graph;

import fr.klemek.genetics.Laboratory;

import javax.swing.*;

class Window extends JFrame {

    private Panel p;

    private Window(Laboratory<Graph> lab) {
        this.setLocation(0, 0);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setTitle("Non-intersecting graph solution by genetics");

        this.setLaboratory(lab);
    }

    private void setLaboratory(Laboratory<Graph> lab) {
        if (p != null)
            this.remove(p);
        p = new Panel(lab);
        this.add(p);
        this.pack();
        this.setVisible(true);
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
