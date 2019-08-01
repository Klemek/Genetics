package fr.klemek.genetics.vindinium;

import com.codingame.gameengine.runner.VindiniumRunner;
import fr.klemek.genetics.ConfigFile;
import fr.klemek.genetics.Subject;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Bot implements Subject {

    private static final ConfigFile config = new ConfigFile("vindinium");
    private static final int matchesPerBot = config.getInt("MATCHES");
    private static final int roundPerMatch = config.getInt("ROUNDS");
    private static final int multithread = config.getInt("MULTITHREAD");


    //variables

    private final float[] values;

    private float score;
    private float matches;

    //constructors

    public Bot() {
        this(false);
    }

    private Bot(boolean empty) {
        if (empty) {
            this.values = new float[Utils.dataSize];
            Arrays.fill(this.values, -1);
        } else {
            this.values = Utils.randomValues();
        }
    }

    //accessors

    public float[] getValues() {
        return values;
    }

    //functions

    String getCommand() {
        StringBuilder output = new StringBuilder("node cg_bot.js");
        for (float val : values) {
            output.append(" ");
            output.append(val);
        }
        return output.toString();
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (float val : values) {
            output.append(" ");
            output.append(String.format("%.3f", val));
        }
        return output.toString();
    }

    //interface methods

    @Override
    public float score() {
        return this.score;
    }

    @Override
    public void mutate(int level) {
        for (int i = 0; i < level; i++) {
            int pos = ThreadLocalRandom.current().nextInt(Utils.dataSize);
            this.values[pos] = Utils.mutate(pos, this.values[pos]);
        }
    }

    @Override
    public Subject[] createChildren(Subject arg0) {
        if (!(arg0 instanceof Bot))
            return new Bot[0];

        Bot other = (Bot) arg0;

        Bot[] children = new Bot[]{new Bot(true), new Bot(true)};

        //generate cuts
        int cut1 = ThreadLocalRandom.current().nextInt(0, Utils.dataSize - 1);
        int cut2 = ThreadLocalRandom.current().nextInt(cut1 + 1, Utils.dataSize);

        for (int i = 0; i < Utils.dataSize; i++) {
            if (i < cut1 || i >= cut2) {
                children[0].values[i] = this.values[i];
                children[1].values[i] = other.values[i];
            } else {
                children[0].values[i] = other.values[i];
                children[1].values[i] = this.values[i];
            }
        }

        return children;
    }


    // static functions

    static class Competition {
        static long t0;
        static int max;
        static int count;
        static int running;

        static void brawl(Bot[] population) {
            for (Bot bot : population) {
                bot.score = 0;
                bot.matches = 0;
            }
            Utils.shuffle(population);
            t0 = System.currentTimeMillis();
            max = population.length * matchesPerBot;
            List<Bot[]> matches = new CopyOnWriteArrayList<>();
            for (int n = 0; n < population.length * matchesPerBot; n++) {

                int b1 = Utils.randInt(0, population.length);
                int b2 = Utils.randInt(0, population.length, b1);
                int b3 = Utils.randInt(0, population.length, b1, b2);
                int b4 = Utils.randInt(0, population.length, b1, b2, b3);
                matches.add(new Bot[]{population[b1], population[b2], population[b3], population[b4]});
            }

            count = 0;
            System.out.print(String.format("\rBattle %03d/%03d", count, max, System.currentTimeMillis() - t0));

            running = multithread;
            for (int n = 0; n < multithread; n++) {
                runner(matches).start();
            }

            while (running > 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }

            System.out.println(String.format("\rBattle %03d/%03d (%d ms/battle)", max, max, (System.currentTimeMillis() - t0) / max));

            for (Bot bot : population) {
                if (bot.matches == 0) {
                    System.out.println("A bot didn't brawl this time");
                } else {
                    bot.score /= bot.matches;
                }
            }
        }

        private static Thread runner(List<Bot[]> matches) {
            return new Thread(() -> {
                while (matches.size() > 0) {
                    Bot[] bots = matches.remove(0);
                    match(bots);
                    count++;
                    System.out.print(String.format("\rBattle %03d/%03d (%d ms)", count, max, System.currentTimeMillis() - t0));
                }
                running--;
            });
        }

        private static void match(Bot[] bots) {
            VindiniumRunner runner = new VindiniumRunner(roundPerMatch, new String[]{
                    bots[0].getCommand(),
                    bots[1].getCommand(),
                    bots[2].getCommand(),
                    bots[3].getCommand(),
            });
            int[] score = runner.run();
            int[] places = ArrayUtils.clone(score);
            Arrays.sort(places);
            for (int i = 0; i < 4; i++) {
                bots[i].matches++;
                if(score[i] == 0) continue; // not points for you
                int place = ArrayUtils.indexOf(places, score[i]);
                switch (place) {
                    case 0:
                        bots[i].score += 4;
                        break;
                    case 1:
                        bots[i].score += 2;
                        break;
                    case 2:
                        bots[i].score += 1;
                        break;
                }
            }
        }
    }


}
