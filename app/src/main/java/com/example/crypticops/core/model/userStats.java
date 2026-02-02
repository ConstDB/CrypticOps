package com.example.crypticops.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class userStats {
    private int score;
    private int assistancePoints;
    private int solvedTotal;
    private int solvedWithoutAssistance;
    private List<String> cluesSolved; // IDs of solved clues
    private Map<String, Integer> solvedByDifficulty; // "easy" -> 3

    // Constructor
    public userStats(int score, int assistancePoints, int solvedTotal, int solvedWithoutAssistance,
                     List<String> cluesSolved, Map<String, Integer> solvedByDifficulty) {
        this.score = score;
        this.assistancePoints = assistancePoints;
        this.solvedTotal = solvedTotal;
        this.solvedWithoutAssistance = solvedWithoutAssistance;
        this.cluesSolved = cluesSolved != null ? cluesSolved : new ArrayList<>();
        this.solvedByDifficulty = solvedByDifficulty != null ? solvedByDifficulty : new HashMap<>();
    }

    // Getters
    public int getScore() { return score; }
    public int getAssistancePoints() { return assistancePoints; }
    public int getSolvedTotal() { return solvedTotal; }
    public int getSolvedWithoutAssistance() { return solvedWithoutAssistance; }
    public List<String> getCluesSolved() { return cluesSolved; }
    public Map<String, Integer> getSolvedByDifficulty() { return solvedByDifficulty; }

    // Setters
    public void setAssistancePoints(int points) { this.assistancePoints = points; }
    public void setSolvedTotal(int total) { this.solvedTotal = total; }

    public void addSolvedClue(String clueId) {
        if (!this.cluesSolved.contains(clueId)) {
            this.cluesSolved.add(clueId);
        }
    }

    public void incrementDifficultyCount(String difficulty) {
        String key = difficulty.toLowerCase();
        this.solvedByDifficulty.put(key, this.solvedByDifficulty.getOrDefault(key, 0) + 1);
    }
}