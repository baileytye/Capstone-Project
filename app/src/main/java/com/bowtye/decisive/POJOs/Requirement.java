package com.bowtye.decisive.POJOs;


public class Requirement {

    private String name;
    private Type type;
    private Importance importance;
    private double expected;
    private String notes;
    private double weight;
    private Boolean excludeFromTotal;
    private double value;

    public Requirement(String name, Type type, Importance importance, double expected,
                       String notes, double weight, Boolean excludeFromTotal, double value) {
        this.name = name;
        this.type = type;
        this.importance = importance;
        this.expected = expected;
        this.notes = notes;
        this.weight = weight;
        this.excludeFromTotal = excludeFromTotal;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Importance getImportance() {
        return importance;
    }

    public void setImportance(Importance importance) {
        this.importance = importance;
    }

    public double getExpected() {
        return expected;
    }

    public void setExpected(double expected) {
        this.expected = expected;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Boolean getExcludeFromTotal() {
        return excludeFromTotal;
    }

    public void setExcludeFromTotal(Boolean excludeFromTotal) {
        this.excludeFromTotal = excludeFromTotal;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public enum Type{
        number, starRating, checkbox, averaging
    }

    public enum Importance{
        high, normal, low
    }
}
