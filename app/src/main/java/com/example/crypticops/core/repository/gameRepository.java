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

public class gameRepository {
    private Context context;
    public gameRepository(Context context) {
        this.context = context;
    }

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

    public userStats loadUserStats() {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset("user.json"));
            return new userStats(
                    obj.getInt("score"),
                    obj.getInt("assistance_points"),
                    obj.getInt("solved_total")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new userStats(0, 0, 0);
    }
        public clue getClueById(String targetId) {
            // We need to reload or cache the list.
            // For efficiency, usually we load once, but for now let's load and search.
            List<clue> allClues = loadClues();

            for (clue c : allClues) {
                if (c.getId().equals(targetId)) {
                    return c;
                }
            }
            return null; // Not found
        }

        public void saveClueNote(String clueId, String noteText) {
            SharedPreferences prefs = context.getSharedPreferences("CrypticOps_Notes", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("NOTE_" + clueId, noteText);
            editor.apply(); // Saves in background
        }

        public String getClueNote(String clueId) {
            SharedPreferences prefs = context.getSharedPreferences("CrypticOps_Notes", Context.MODE_PRIVATE);
            return prefs.getString("NOTE_" + clueId, ""); // Returns empty string if no note exists
        }
        public List<clue> getCluesByDifficulty(String difficulty) {
            List<clue> allClues = loadClues();
            List<clue> filteredList = new ArrayList<>();

            for (clue c : allClues) {
                // Check if the difficulty matches (ignoring capital letters)
                if (c.getDifficulty().equalsIgnoreCase(difficulty)) {
                    filteredList.add(c);
                }
            }
            return filteredList;
        }
    }