package fr.klemek.genetics.salesman;

import fr.klemek.genetics.Laboratory;

import javax.swing.*;

class Window extends JFrame {

    private Window(Laboratory<Salesman> lab) {
        this.setLocation(0, 0);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setTitle("Traveling Salesman solution by genetics");

        Panel panel = new Panel(lab);

        this.add(panel);
        this.pack();

        this.setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException {

        Laboratory<Salesman> lab;
        Window w = null;

        Data.loadDistances();

        do {
            if (w != null) {
                w.setVisible(false);
                w.dispose();
            }

            lab = new Laboratory<>(Salesman.class,
                    Data.POPULATION_SIZE, Data.DEFAULT_MUTATION,
                    Data.HIGH_STAGNATION, Data.MAX_STAGNATION,
                    Data.MUTATE_ONLY_CHILDREN);

            w = new Window(lab);

            w.repaint();

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
        } while (Data.RELOAD_THRESOLD > 0 && lab.getBest().score() >= Data.RELOAD_THRESOLD);

    }
}
