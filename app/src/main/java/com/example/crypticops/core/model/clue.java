package com.example.crypticops.core.model;

import java.util.List;

public class clue {
    private String id;
    private String clueText;
    private String length;
    private String answer;
    private String difficulty;
    private List<String> tradeCrafts;
    private String hint;

    public clue(String id, String clueText, String length, String answer, String difficulty, List<String> tradeCrafts, String hint) {
        this.id = id;
        this.clueText = clueText;
        this.length = length;
        this.answer = answer;
        this.difficulty = difficulty;
        this.tradeCrafts = tradeCrafts;
        this.hint = hint;
    }

    // Getters
    public String getId() { return id; }
    public String getClueText() { return clueText; }
    public String getDifficulty() { return difficulty; }
    public List<String> getTradeCrafts() { return tradeCrafts; }
}