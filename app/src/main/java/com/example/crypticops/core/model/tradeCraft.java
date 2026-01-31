package com.example.crypticops.core.model;

public class tradeCraft {
    private String id;
    private String name;
    private String description;
    private String indicator;
    private String example;

    public tradeCraft(String id, String name, String description, String indicator, String example) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.indicator = indicator;
        this.example = example;
    }

    public String getName() { return name; }
    public String getId() { return id; }
}