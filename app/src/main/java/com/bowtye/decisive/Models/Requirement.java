package com.bowtye.decisive.Models;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.bowtye.decisive.Database.Converters;
import com.bowtye.decisive.R;

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
    private Double expected;
    private String notes;
    private Double weight;
    private Boolean moreIsBetter;

    @Ignore
    public Requirement(String name, Type type, Importance importance, Double expected,
                       String notes, Double weight, Boolean moreIsBetter) {
        this.name = name;
        this.type = type;
        this.importance = importance;
        this.expected = expected;
        this.notes = notes;
        this.weight = weight;
        this.moreIsBetter = moreIsBetter;
    }

    public Requirement(int reqId, int projectId, String name, Type type, Importance importance, Double expected,
                       String notes, Double weight, Boolean moreIsBetter) {
        this.reqId = reqId;
        this.projectId = projectId;
        this.name = name;
        this.type = type;
        this.importance = importance;
        this.expected = expected;
        this.notes = notes;
        this.weight = weight;
        this.moreIsBetter = moreIsBetter;
    }

    public static double getAveragingValue(String type, Context context) {

        String[] averageLabels = context.getResources().getStringArray(R.array.averages);

        if(type.equals(averageLabels[0])){
            return FAR_ABOVE_AVERAGE;
        } else if(type.equals(averageLabels[1])){
            return ABOVE_AVERAGE;
        } else if(type.equals(averageLabels[2])){
            return AVERAGE;
        } else if(type.equals(averageLabels[3])){
            return BELOW_AVERAGE;
        } else if(type.equals(averageLabels[4])){
            return FAR_BELOW_AVERAGE;
        }

        return AVERAGE;
    }

    /**
     * Converts the numerical value to a string representation of averaging labels
     * @param value will be converted to int, which is save since values are only -2 to 2
     * @return string of value
     */
    public static String getAveragingString(double value, Context context){

        String[] averageLabels = context.getResources().getStringArray(R.array.averages);

        switch ((int) value){
            case (int) FAR_ABOVE_AVERAGE:
                return averageLabels[0];
            case (int) ABOVE_AVERAGE:
                return averageLabels[1];
            case (int) AVERAGE:
                return averageLabels[2];
            case (int) BELOW_AVERAGE:
                return averageLabels[3];
            case (int) FAR_BELOW_AVERAGE:
                return averageLabels[4];
        }
        return(averageLabels[2]);
    }

    public static int getAveragingIndex(double value, Context context){
        switch((int) value){
            case (int) FAR_ABOVE_AVERAGE:
                return 0;
            case (int) ABOVE_AVERAGE:
                return 1;
            case (int) AVERAGE:
                return 2;
            case (int) BELOW_AVERAGE:
                return 3;
            case (int) FAR_BELOW_AVERAGE:
                return 4;
            default:
                return 2;
        }
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

    public Double getExpected() {
        return expected;
    }

    public void setExpected(Double expected) {
        this.expected = expected;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public void setImportanceAndWeightFromString(String importanceString, Double customWeight){
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
        dest.writeValue(this.moreIsBetter);
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
        this.moreIsBetter = (Boolean) in.readValue(Boolean.class.getClassLoader());
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
