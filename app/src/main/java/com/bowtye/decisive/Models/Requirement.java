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

@Entity(tableName = "requirement",
        foreignKeys = @ForeignKey(entity = Project.class,
                parentColumns = "id",
                childColumns = "projectId",
                onDelete = CASCADE ))
public class Requirement implements Parcelable {

    public static final double FAR_BELOW_AVERAGE = -2.0;
    public static final double BELOW_AVERAGE = -1.0;
    public static final double AVERAGE = 0;
    public static final double ABOVE_AVERAGE = 1.0;
    public static final double FAR_ABOVE_AVERAGE = 2.0;

    @PrimaryKey (autoGenerate = true)
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    private Requirement(Parcel in){
        reqId = in.readInt();
        projectId = in.readInt();
        name = in.readString();
        type = Type.values()[in.readInt()];
        importance = Importance.values()[in.readInt()];
        expected = in.readDouble();
        notes = in.readString();
        weight = in.readDouble();
        value = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(reqId);
        parcel.writeInt(projectId);
        parcel.writeString(name);
        parcel.writeInt(type.ordinal());
        parcel.writeInt(importance.ordinal());
        parcel.writeDouble(expected);
        parcel.writeString(notes);
        parcel.writeDouble(weight);
        parcel.writeDouble(value);
    }

    public static final Parcelable.Creator<Requirement> CREATOR =
            new Parcelable.Creator<Requirement>(){

                @Override
                public Requirement createFromParcel(Parcel parcel) {
                    return new Requirement(parcel);
                }

                @Override
                public Requirement[] newArray(int i) {
                    return new Requirement[i];
                }
            };

    public enum Type{
        number, starRating, checkbox, averaging
    }

    public enum Importance{
        high, normal, low, custom, exclude
    }
}
