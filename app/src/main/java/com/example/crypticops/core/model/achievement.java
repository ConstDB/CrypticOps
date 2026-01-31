package com.example.crypticops.core.model;

public class achievement {
    private String id;
    private String title;
    private String description;
    private String type;
    private String metric; // Nullable in your Kotlin version
    private int threshold;
    private int reward;

    // Constructor
    public achievement(String id, String title, String description, String type, String metric, int threshold, int reward) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.metric = metric;
        this.threshold = threshold;
        this.reward = reward;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public String getMetric() { return metric; }
    public int getThreshold() { return threshold; }
    public int getReward() { return reward; }

    // Setter for ID (Since it was 'var' in Kotlin)
    public void setId(String id) { this.id = id; }
}