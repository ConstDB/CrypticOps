package com.example.crypticops.core.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.crypticops.core.model.clue;
import com.example.crypticops.core.model.tradeCraft;
import com.example.crypticops.core.model.userStats;
import com.example.crypticops.core.model.achievement;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class gameRepository {
    private Context context;

    public gameRepository(Context context) {
        this.context = context;
    }

    // --- LOADERS ---

    private String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public List<clue> loadClues() {
        List<clue> list = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset("clues.json"));
            Iterator<String> keys = obj.keys();

            while(keys.hasNext()) {
                String key = keys.next();
                JSONObject item = obj.getJSONObject(key);

                List<String> tradeCrafts = new ArrayList<>();
                JSONArray arr = item.optJSONArray("tradecraft");
                if (arr != null) {
                    for (int i = 0; i < arr.length(); i++) {
                        tradeCrafts.add(arr.getString(i));
                    }
                }

                list.add(new clue(
                        key,
                        item.getString("clue"),
                        item.getString("length"),
                        item.getString("answer"),
                        item.getString("difficulty"),
                        tradeCrafts,
                        item.optString("hint", "")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<tradeCraft> loadTradeCrafts() {
        List<tradeCraft> list = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset("tradeCraft.json"));
            Iterator<String> keys = obj.keys();

            while(keys.hasNext()) {
                String key = keys.next();
                JSONObject item = obj.getJSONObject(key);

                list.add(new tradeCraft(
                        key,
                        item.getString("name"),
                        item.getString("description"),
                        item.getString("indicator"),
                        item.getString("example")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<achievement> loadAchievements() {
        List<achievement> list = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset("achievements.json"));
            Iterator<String> keys = obj.keys();

            while(keys.hasNext()) {
                String key = keys.next();
                JSONObject item = obj.getJSONObject(key);

                list.add(new achievement(
                        key,
                        item.getString("title"),
                        item.getString("description"),
                        item.getString("type"),
                        item.optString("metric", null),
                        item.optInt("threshold", 0),
                        item.getInt("reward")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // --- PUZZLE UTILITIES ---

    public clue getClueById(String targetId) {
        List<clue> allClues = loadClues();
        for (clue c : allClues) {
            if (c.getId().equals(targetId)) {
                return c;
            }
        }
        return null;
    }

    public List<clue> getCluesByDifficulty(String difficulty) {
        List<clue> allClues = loadClues();
        List<clue> filteredList = new ArrayList<>();
        for (clue c : allClues) {
            if (c.getDifficulty().equalsIgnoreCase(difficulty)) {
                filteredList.add(c);
            }
        }
        return filteredList;
    }

    // --- PERSISTENCE: NOTES ---

    public void saveClueNote(String clueId, String noteText) {
        SharedPreferences prefs = context.getSharedPreferences("CrypticOps_Notes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("NOTE_" + clueId, noteText);
        editor.apply();
    }

    public String getClueNote(String clueId) {
        SharedPreferences prefs = context.getSharedPreferences("CrypticOps_Notes", Context.MODE_PRIVATE);
        return prefs.getString("NOTE_" + clueId, "");
    }

    // --- VALIDATION ENGINE (Ticket 3) ---

    public String checkAnswer(String clueId, String userInput) {
        clue targetClue = getClueById(clueId);
        if (targetClue == null) return "Error: Clue not found";

        String cleanedInput = userInput.trim().toLowerCase();
        String cleanedAnswer = targetClue.getAnswer().trim().toLowerCase();

        if (cleanedInput.equals(cleanedAnswer)) {
            handleCorrectAnswer(clueId, targetClue.getDifficulty());
            return "Correct";
        } else {
            return "Incorrect";
        }
    }

    private void handleCorrectAnswer(String clueId, String difficulty) {
        userStats stats = loadUserStats();

        if (stats.getCluesSolved().contains(clueId)) {
            return; // Already solved
        }

        // Update Stats
        stats.setAssistancePoints(stats.getAssistancePoints() + 1);
        stats.setSolvedTotal(stats.getSolvedTotal() + 1);
        stats.addSolvedClue(clueId);
        stats.incrementDifficultyCount(difficulty);

        saveUserStats(stats);
    }

    // --- PERSISTENCE: USER STATS ---

    public void saveUserStats(userStats stats) {
        SharedPreferences prefs = context.getSharedPreferences("CrypticOps_Data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        try {
            JSONObject json = new JSONObject();
            json.put("score", stats.getScore());
            json.put("assistance_points", stats.getAssistancePoints());
            json.put("solved_total", stats.getSolvedTotal());
            json.put("solved_without_assistance", stats.getSolvedWithoutAssistance());

            json.put("clues_solved", new JSONArray(stats.getCluesSolved()));

            JSONObject diffObj = new JSONObject();
            Map<String, Integer> diffMap = stats.getSolvedByDifficulty();
            for (String key : diffMap.keySet()) {
                diffObj.put(key, diffMap.get(key));
            }
            json.put("solved_by_difficulty", diffObj);

            editor.putString("USER_STATS_JSON", json.toString());
            editor.apply();

        } catch (Exception e) { e.printStackTrace(); }
    }

    public userStats loadUserStats() {
        SharedPreferences prefs = context.getSharedPreferences("CrypticOps_Data", Context.MODE_PRIVATE);
        String savedJson = prefs.getString("USER_STATS_JSON", null);

        // 1. Try to load from SharedPreferences (Saved Progress)
        if (savedJson != null) {
            try {
                JSONObject obj = new JSONObject(savedJson);

                List<String> solvedList = new ArrayList<>();
                JSONArray arr = obj.optJSONArray("clues_solved");
                if (arr != null) {
                    for(int i=0; i<arr.length(); i++) solvedList.add(arr.getString(i));
                }

                Map<String, Integer> diffMap = new HashMap<>();
                JSONObject diffObj = obj.optJSONObject("solved_by_difficulty");
                if (diffObj != null) {
                    Iterator<String> keys = diffObj.keys();
                    while(keys.hasNext()) {
                        String k = keys.next();
                        diffMap.put(k, diffObj.getInt(k));
                    }
                }

                return new userStats(
                        obj.optInt("score", 0),
                        obj.optInt("assistance_points", 0),
                        obj.optInt("solved_total", 0),
                        obj.optInt("solved_without_assistance", 0),
                        solvedList,
                        diffMap
                );
            } catch (Exception e) { e.printStackTrace(); }
        }

        // 2. Fallback: Load from Assets (First Time User)
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset("user.json"));
            return new userStats(
                    obj.getInt("score"),
                    obj.getInt("assistance_points"),
                    obj.getInt("solved_total"),
                    0, // solved_without_assistance
                    new ArrayList<>(), // clues_solved
                    new HashMap<>() // solved_by_difficulty
            );
        } catch (Exception e) { e.printStackTrace(); }

        return new userStats(0, 0, 0, 0, new ArrayList<>(), new HashMap<>());
    }
}