package com.example.crypticops.ui.activity;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.crypticops.R;
import com.example.crypticops.core.repository.gameRepository;
import com.example.crypticops.core.model.clue;
import com.example.crypticops.core.model.tradeCraft;
import com.example.crypticops.core.model.userStats;
import com.example.crypticops.core.model.achievement;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String TAG = "TICKET_TEST";
        
        gameRepository repo = new gameRepository(this);

        Log.d(TAG, "--- START JAVA CAMELCASE TEST ---");

        // 1. Test userStats
        userStats myStats = repo.loadUserStats();
        Log.d(TAG, "User Score: " + myStats.getScore());

        // 2. Test tradeCraft
        List<tradeCraft> crafts = repo.loadTradeCrafts();
        if (!crafts.isEmpty()) {
            Log.d(TAG, "First Craft: " + crafts.get(0).getName());
        }

        // 3. Test clue
        List<clue> clues = repo.loadClues();
        Log.d(TAG, "Clues Loaded: " + clues.size());
        
        for (clue c : clues) {
            if (c.getDifficulty().equalsIgnoreCase("easy")) {
                Log.d(TAG, "Found Easy Clue: " + c.getClueText());
            }

            List<achievement> badges = repo.loadAchievements();
            Log.d(TAG, "Achievements Loaded: " + badges.size());

            if (!badges.isEmpty()) {
                Log.d(TAG, "First Badge: " + badges.get(0).getTitle());
        }
    }
}
}