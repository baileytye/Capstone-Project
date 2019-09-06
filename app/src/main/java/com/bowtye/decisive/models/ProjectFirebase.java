package com.bowtye.decisive.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class ProjectFirebase {

    private String name, userId, projectId;
    private Boolean hasPrice;
    private @ServerTimestamp Date dateCreated;

    private List<RequirementFirebase> requirements;
    private List<OptionFirebase> options;

    public ProjectFirebase() {
    }

    public ProjectFirebase(String name, String userId, String projectId, Boolean hasPrice,
                           Date dateCreated, List<RequirementFirebase> requirements, List<OptionFirebase> options) {
        this.name = name;
        this.userId = userId;
        this.projectId = projectId;
        this.hasPrice = hasPrice;
        this.dateCreated = dateCreated;
        this.requirements = requirements;
        this.options = options;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getHasPrice() {
        return hasPrice;
    }

    public void setHasPrice(Boolean hasPrice) {
        this.hasPrice = hasPrice;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public List<RequirementFirebase> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<RequirementFirebase> requirements) {
        this.requirements = requirements;
    }

    public List<OptionFirebase> getOptions() {
        return options;
    }

    public void setOptions(List<OptionFirebase> options) {
        this.options = options;
    }
}
