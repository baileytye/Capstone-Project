package com.bowtye.decisive.models;

public class RequirementFirebase {

    private String name, notes;
    private Requirement.Type type;
    private Requirement.Importance importance;
    private Double expected, weight;
    private Boolean moreIsBetter;

    public RequirementFirebase() {
    }

    public RequirementFirebase(String name, String notes, Requirement.Type type, Requirement.Importance importance,
                               Double expected, Double weight, Boolean moreIsBetter) {
        this.name = name;
        this.notes = notes;
        this.type = type;
        this.importance = importance;
        this.expected = expected;
        this.weight = weight;
        this.moreIsBetter = moreIsBetter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Requirement.Type getType() {
        return type;
    }

    public void setType(Requirement.Type type) {
        this.type = type;
    }

    public Requirement.Importance getImportance() {
        return importance;
    }

    public void setImportance(Requirement.Importance importance) {
        this.importance = importance;
    }

    public Double getExpected() {
        return expected;
    }

    public void setExpected(Double expected) {
        this.expected = expected;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Boolean getMoreIsBetter() {
        return moreIsBetter;
    }

    public void setMoreIsBetter(Boolean moreIsBetter) {
        this.moreIsBetter = moreIsBetter;
    }
}
