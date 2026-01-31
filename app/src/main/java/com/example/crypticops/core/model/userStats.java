package com.example.crypticops.core.model;

public class userStats {
    private int score;
    private int assistancePoints;
    private int solvedTotal;

    public userStats(int score, int assistancePoints, int solvedTotal) {
        this.score = score;
        this.assistancePoints = assistancePoints;
        this.solvedTotal = solvedTotal;
    }

    public int getScore() { return score; }
    public int getAssistancePoints() { return assistancePoints; }
}