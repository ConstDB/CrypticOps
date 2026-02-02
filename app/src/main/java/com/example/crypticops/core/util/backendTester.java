package com.example.crypticops.core.util;

import android.content.Context;
import android.util.Log;
import com.example.crypticops.core.repository.gameRepository;
import com.example.crypticops.core.model.clue;
import com.example.crypticops.core.model.tradeCraft;
import com.example.crypticops.core.model.userStats;
import com.example.crypticops.core.model.achievement;
import java.util.List;

public class backendTester {

    private static final String TAG = "TICKET_TEST";

    public static void runAllTests(Context context) {
        Log.d(TAG, "   STARTING ISOLATED BACKEND CHECKS    ");

        gameRepository repo = new gameRepository(context);

        // 1. Test userStats
        userStats myStats = repo.loadUserStats();
        Log.d(TAG, "[UserStats] Score: " + myStats.getScore());

        // 2. Test tradeCraft
        List<tradeCraft> crafts = repo.loadTradeCrafts();
        if (!crafts.isEmpty()) {
            Log.d(TAG, "[TradeCraft] First Loaded: " + crafts.get(0).getName());
        }

        // 3. Test clue
        List<clue> clues = repo.loadClues();
        Log.d(TAG, "[Clues] Total Loaded: " + clues.size());

        // 4. Test Achievements
        List<achievement> badges = repo.loadAchievements();
        Log.d(TAG, "[Achievements] Total Loaded: " + badges.size());

        // 5. Test Puzzle Loader & Notes (Ticket 2 Verification)
        if (!clues.isEmpty()) {
            String testId = clues.get(0).getId();

            // A. Test getClueById
            clue specificClue = repo.getClueById(testId);
            if (specificClue != null) {
                Log.d(TAG, "[PuzzleLoader] Found Clue by ID: " + specificClue.getClueText());
            }

            // B. Test Saving a Note
            repo.saveClueNote(testId, "SecretCode_v2");

            // C. Test Loading the Note
            String savedNote = repo.getClueNote(testId);
            Log.d(TAG, "[Persistence] Retrieved Note: " + savedNote);

            if (savedNote.equals("SecretCode_v2")) {
                Log.d(TAG, "success");
            } else {
                Log.e(TAG, "failed");
            }
        }

        // 6. Test Filter
        List<clue> easyClues = repo.getCluesByDifficulty("easy");
        Log.d(TAG, "[Filter] Found " + easyClues.size() + " Easy clues.");

        if (!easyClues.isEmpty()) {
            Log.d(TAG, "[Filter] Example Easy: " + easyClues.get(0).getClueText());
        }

        // 7. TICKET 3: Validation Engine Test
        Log.d(TAG, "--- Testing Validation ---");

        if (!clues.isEmpty()) {
            clue c = clues.get(0);
            String correctAns = c.getAnswer();
            String testId = c.getId();

            Log.d(TAG, "Target Answer: " + correctAns);

            // Test 1: Wrong Answer
            String result1 = repo.checkAnswer(testId, "WrongAnswer");
            Log.d(TAG, "Input: 'WrongAnswer' -> Result: " + result1);

            // Test 2: Correct Answer with Messy Input (Spaces + Wrong Case)
            String messyInput = "   " + correctAns.toLowerCase() + "   ";
            String result2 = repo.checkAnswer(testId, messyInput);
            Log.d(TAG, "Input: '" + messyInput + "' -> Result: " + result2);

            if (result2.equals("Correct")) {
                // Test 3: Check if stats updated
                userStats updatedStats = repo.loadUserStats();
                Log.d(TAG, "New Solved Count: " + updatedStats.getSolvedTotal());
                Log.d(TAG, "Solved List: " + updatedStats.getCluesSolved().toString());

                if (updatedStats.getCluesSolved().contains(testId)) {
                    Log.d(TAG, "success");
                } else {
                    Log.e(TAG, "failed");
                }
            }
        }

        Log.d(TAG, "          END BACKEND TEST           ");
    }
}