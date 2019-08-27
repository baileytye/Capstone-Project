package com.bowtye.decisive.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.List;

public class ProjectWithDetails implements Parcelable {

    @Embedded
    Project project;

    @Relation(parentColumn = "id", entityColumn = "projectId")
    List<Option> optionList;

    @Relation(parentColumn = "id", entityColumn = "projectId")
    List<Requirement> requirementList;

    @Ignore
    public ProjectWithDetails(){}

    public ProjectWithDetails(Project project, List<Option> optionList, List<Requirement> requirementList) {
        this.project = project;
        this.optionList = optionList;
        this.requirementList = requirementList;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Option> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<Option> optionList) {
        this.optionList = optionList;
    }

    public List<Requirement> getRequirementList() {
        return requirementList;
    }

    public void setRequirementList(List<Requirement> requirementList) {
        this.requirementList = requirementList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.project, flags);
        dest.writeTypedList(this.optionList);
        dest.writeTypedList(this.requirementList);
    }

    protected ProjectWithDetails(Parcel in) {
        this.project = in.readParcelable(Project.class.getClassLoader());
        this.optionList = in.createTypedArrayList(Option.CREATOR);
        this.requirementList = in.createTypedArrayList(Requirement.CREATOR);
    }

    public static final Parcelable.Creator<ProjectWithDetails> CREATOR = new Parcelable.Creator<ProjectWithDetails>() {
        @Override
        public ProjectWithDetails createFromParcel(Parcel source) {
            return new ProjectWithDetails(source);
        }

        @Override
        public ProjectWithDetails[] newArray(int size) {
            return new ProjectWithDetails[size];
        }
    };
}
