package fr.klemek.genetics.salesman;

import fr.klemek.genetics.Laboratory;

import javax.swing.*;

class Window extends JFrame {

    private Panel p;

    private Window(Laboratory<Salesman> lab) {
        this.setLocation(0, 0);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setTitle("Traveling Salesman solution by genetics");

        this.setLaboratory(lab);
    }

    private void setLaboratory(Laboratory<Salesman> lab) {
        if (p != null)
            this.remove(p);
        p = new Panel(lab);
        this.add(p);
        this.pack();
        this.setVisible(true);
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
