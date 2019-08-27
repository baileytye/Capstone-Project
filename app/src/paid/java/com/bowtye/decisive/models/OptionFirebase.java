package com.bowtye.decisive.models;

import java.util.Date;
import java.util.List;

public class OptionFirebase {

    private String name, notes, imagePath;
    private double price;
    private Date dateCreated;
    private Float rating;
    private Boolean ruledOut;
    private List<Double> requirementValues;

    public OptionFirebase() {
    }

    public OptionFirebase(String name, String notes, String imagePath, double price, Date dateCreated, Float rating,
                          Boolean ruledOut, List<Double> requirementValues) {
        this.name = name;
        this.notes = notes;
        this.imagePath = imagePath;
        this.price = price;
        this.dateCreated = dateCreated;
        this.rating = rating;
        this.ruledOut = ruledOut;
        this.requirementValues = requirementValues;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Boolean getRuledOut() {
        return ruledOut;
    }

    public void setRuledOut(Boolean ruledOut) {
        this.ruledOut = ruledOut;
    }

    public List<Double> getRequirementValues() {
        return requirementValues;
    }

    public void setRequirementValues(List<Double> requirementValues) {
        this.requirementValues = requirementValues;
    }
}
