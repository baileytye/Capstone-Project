package com.bowtye.decisive.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Option implements Parcelable {

    private String name;
    private double price;
    private double rating;
    private Boolean ruledOut;
    private List<Requirement> requirements;
    private String notes;
    private List<String> imagePaths;

    public Option(String name, double price, double rating, Boolean ruledOut,
                  List<Requirement> requirements, String notes, List<String> imagePaths) {
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.ruledOut = ruledOut;
        this.requirements = requirements;
        this.notes = notes;
        this.imagePaths = imagePaths;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Boolean getRuledOut() {
        return ruledOut;
    }

    public void setRuledOut(Boolean ruledOut) {
        this.ruledOut = ruledOut;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    private Option(Parcel in){
        name = in.readString();
        price = in.readDouble();
        rating = in.readDouble();
        ruledOut = in.readInt() == 1;
        requirements = new ArrayList<>();
        in.readList(requirements, Requirement.class.getClassLoader());
        notes = in.readString();
        imagePaths = in.createStringArrayList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeDouble(price);
        parcel.writeDouble(rating);
        parcel.writeInt(ruledOut ? 1 : 0);
        parcel.writeList(requirements);
        parcel.writeString(notes);
        parcel.writeList(imagePaths);
    }

    public static final Parcelable.Creator<Option> CREATOR =
            new Parcelable.Creator<Option>(){

                @Override
                public Option createFromParcel(Parcel parcel) {
                    return new Option(parcel);
                }

                @Override
                public Option[] newArray(int i) {
                    return new Option[i];
                }
            };
}
