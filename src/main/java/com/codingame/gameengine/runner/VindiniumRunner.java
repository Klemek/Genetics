package com.codingame.gameengine.runner;

import com.codingame.gameengine.runner.dto.GameResult;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class VindiniumRunner {

    private final static int PLAYER_COUNT = 4;
    private final String[] players;
    private final int rounds;

    public VindiniumRunner(int rounds, String[] players) {
        this.players = players;
        this.rounds = rounds;
    }

    public static void main(String... args) {
        VindiniumRunner runner = new VindiniumRunner(200, new String[]{
                "node cg_bot.js",
                "node cg_bot.js",
                "node cg_bot.js",
                "node cg_bot.js",
        });
        int[] results = runner.run();
        System.out.println(Arrays.toString(results));
    }

    public int[] run() {
        try {
            MultiplayerGameRunner runner = new MultiplayerGameRunner();

            Field getGameResult = GameRunner.class.getDeclaredField("gameResult");
            getGameResult.setAccessible(true);
            GameResult result = (GameResult) getGameResult.get(runner);

            for (int i = 0; i < PLAYER_COUNT; ++i) {
                runner.addAgent(players[i]);
            }

            Properties props = new Properties();
            props.put("turns", rounds);

            Method initialize = GameRunner.class.getDeclaredMethod("initialize", Properties.class);
            initialize.setAccessible(true);
            initialize.invoke(runner, props);

            Method runAgents = GameRunner.class.getDeclaredMethod("runAgents");
            runAgents.setAccessible(true);
            runAgents.invoke(runner);

            int[] results = new int[PLAYER_COUNT];

            for (int i = 0; i < PLAYER_COUNT; ++i) {
                if (result != null && result.scores != null && result.scores.get(i) != null)
                    results[i] = result.scores.get(i);
                //debugOutput(i, result.errors.get(""+i));
            }

            // We have to clean players process properly
            Field getPlayers = GameRunner.class.getDeclaredField("players");
            getPlayers.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Agent> players = (List<Agent>) getPlayers.get(runner);

            if (players != null) {
                for (Agent player : players) {
                    Field getProcess = CommandLinePlayerAgent.class.getDeclaredField("process");
                    getProcess.setAccessible(true);
                    Process process = (Process) getProcess.get(player);
                    process.destroy();
                }
            }

            return results;
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
            return new int[PLAYER_COUNT];
        }
    }

    private void debugOutput(int player, List<String> output) {
        System.err.println("player " + (player + 1) + " :");
        for (String log : output) {
            if (log != null)
                System.err.println(log + "\n");
        }
    }

}
