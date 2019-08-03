package com.bowtye.decisive.Models;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.bowtye.decisive.Database.Converters;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "requirement")
public class Requirement implements Parcelable {

    public static final double FAR_BELOW_AVERAGE = -2.0;
    public static final double BELOW_AVERAGE = -1.0;
    public static final double AVERAGE = 0;
    public static final double ABOVE_AVERAGE = 1.0;
    public static final double FAR_ABOVE_AVERAGE = 2.0;

    public static final double WEIGHT_HIGH = 1.5;
    public static final double WEIGHT_NORMAL = 1;
    public static final double WEIGHT_LOW = 0.5;
    public static final double WEIGHT_EXCLUDE = 0;

    @PrimaryKey(autoGenerate = true)
    private int reqId;
    private int projectId;
    private String name;

    @TypeConverters(Converters.class)
    private Type type;

    @TypeConverters(Converters.class)
    private Importance importance;
    private double expected;
    private String notes;
    private double weight;
    private double value;

    @Ignore
    public Requirement(String name, Type type, Importance importance, double expected,
                       String notes, double weight, double value) {
        this.name = name;
        this.type = type;
        this.importance = importance;
        this.expected = expected;
        this.notes = notes;
        this.weight = weight;
        this.value = value;
    }

    public Requirement(int reqId, int projectId, String name, Type type, Importance importance, double expected,
                       String notes, double weight, double value) {
        this.reqId = reqId;
        this.projectId = projectId;
        this.name = name;
        this.type = type;
        this.importance = importance;
        this.expected = expected;
        this.notes = notes;
        this.weight = weight;
        this.value = value;
    }

    public static double getAveragingValue(String type) {
        switch (type) {
            case "Far Above Average":
                return FAR_ABOVE_AVERAGE;
            case "Above Average":
                return ABOVE_AVERAGE;
            case "Average":
                return AVERAGE;
            case "Below Average":
                return BELOW_AVERAGE;
            case "Far Below Average":
                return FAR_BELOW_AVERAGE;
        }
        return AVERAGE;
    }

    public int getReqId() {
        return reqId;
    }

    public void setReqId(int reqId) {
        this.reqId = reqId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public static Type getTypeFromString(String typeString) {
        switch (typeString) {
            case "Number":
                return Type.number;
            case "Star Rating":
                return Type.starRating;
            case "Checkbox":
                return Type.checkbox;
            case "Above/Below Avg":
                return Type.averaging;
        }
        return Requirement.Type.number;
    }

    public void setImportanceAndWeightFromString(String importanceString, double customWeight){
        switch (importanceString) {
            case "High":
                importance =  Importance.high;
                weight = WEIGHT_HIGH;
                break;
            case "Normal":
                importance =  Importance.normal;
                weight = WEIGHT_NORMAL;
                break;
            case "Low":
                importance =  Importance.low;
                weight = WEIGHT_LOW;
                break;
            case "Custom":
                importance =  Importance.custom;
                weight = customWeight;
                break;
            case "Exclude":
                importance =  Importance.exclude;
                weight = WEIGHT_EXCLUDE;
                break;
        }
    }

    public enum Type {
        number, starRating, checkbox, averaging
    }

    public enum Importance {
        high, normal, low, custom, exclude
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.reqId);
        dest.writeInt(this.projectId);
        dest.writeString(this.name);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeInt(this.importance == null ? -1 : this.importance.ordinal());
        dest.writeDouble(this.expected);
        dest.writeString(this.notes);
        dest.writeDouble(this.weight);
        dest.writeDouble(this.value);
    }

    @Ignore
    protected Requirement(Parcel in) {
        this.reqId = in.readInt();
        this.projectId = in.readInt();
        this.name = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : Type.values()[tmpType];
        int tmpImportance = in.readInt();
        this.importance = tmpImportance == -1 ? null : Importance.values()[tmpImportance];
        this.expected = in.readDouble();
        this.notes = in.readString();
        this.weight = in.readDouble();
        this.value = in.readDouble();
    }

    public static final Creator<Requirement> CREATOR = new Creator<Requirement>() {
        @Override
        public Requirement createFromParcel(Parcel source) {
            return new Requirement(source);
        }

        @Override
        public Requirement[] newArray(int size) {
            return new Requirement[size];
        }
    };
}
