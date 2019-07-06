package com.bowtye.decisive.POJOs;

import java.util.List;

public class Project {

    private List<Requirement> requirements;
    private List<Option> options;
    private String name;
    private Boolean hasPrice;

    public Project(List<Requirement> requirements, List<Option> options, String name, Boolean hasPrice) {
        this.requirements = requirements;
        this.options = options;
        this.name = name;
        this.hasPrice = hasPrice;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getHasPrice() {
        return hasPrice;
    }

    public void setHasPrice(Boolean hasPrice) {
        this.hasPrice = hasPrice;
    }
}
