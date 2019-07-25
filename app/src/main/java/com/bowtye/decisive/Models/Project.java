package com.bowtye.decisive.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;


@Entity(tableName = "project")
public class Project implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @Ignore
    private List<Requirement> requirements;
    @Ignore
    private List<Option> options;

    private String name;
    private Boolean hasPrice;

    public Project(int id, String name, Boolean hasPrice) {
        this.id = id;
        this.name = name;
        this.hasPrice = hasPrice;
    }

    @Ignore
    public Project(List<Requirement> requirements, List<Option> options, String name, Boolean hasPrice) {
        this.requirements = requirements;
        this.options = options;
        this.name = name;
        this.hasPrice = hasPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeTypedList(this.requirements);
        dest.writeTypedList(this.options);
        dest.writeString(this.name);
        dest.writeValue(this.hasPrice);
    }

    @Ignore
    protected Project(Parcel in) {
        this.id = in.readInt();
        this.requirements = in.createTypedArrayList(Requirement.CREATOR);
        this.options = in.createTypedArrayList(Option.CREATOR);
        this.name = in.readString();
        this.hasPrice = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel source) {
            return new Project(source);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };
}
