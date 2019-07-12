package com.bowtye.decisive.Models;


import android.os.Parcel;
import android.os.Parcelable;

public class Requirement implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    private Requirement(Parcel in){
        name = in.readString();
        type = Type.values()[in.readInt()];
        importance = Importance.values()[in.readInt()];
        expected = in.readDouble();
        notes = in.readString();
        weight = in.readDouble();
        excludeFromTotal = in.readInt() == 1;
        value = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(type.ordinal());
        parcel.writeInt(importance.ordinal());
        parcel.writeDouble(expected);
        parcel.writeString(notes);
        parcel.writeDouble(weight);
        parcel.writeInt(excludeFromTotal ? 1 : 0);
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
        high, normal, low
    }
}
