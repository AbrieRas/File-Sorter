package org.example.components;

public class ScannedFile {
    private final String name;
    private final String type;

    public ScannedFile(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
