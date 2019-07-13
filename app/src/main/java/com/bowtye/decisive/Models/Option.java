package com.bowtye.decisive.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.bowtye.decisive.Database.Converters;

import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "option",
        foreignKeys = @ForeignKey(entity = Project.class,
        parentColumns = "id",
        childColumns = "projectId",
        onDelete = CASCADE ))
@TypeConverters(Converters.class)
public class Option implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int optionId;
    private int projectId;
    private String name;
    private double price;
    private double rating;
    private Boolean ruledOut;
    private List<Double> requirementValues;
    private String notes;
    private List<String> imagePaths;

    @Ignore
    public Option(String name, double price, double rating, Boolean ruledOut,
                  List<Double> requirementValues, String notes, List<String> imagePaths) {
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.ruledOut = ruledOut;
        this.requirementValues = requirementValues;
        this.notes = notes;
        this.imagePaths = imagePaths;
    }

    public Option(int optionId, int projectId, String name, double price, double rating, Boolean ruledOut,
                  List<Double> requirementValues, String notes, List<String> imagePaths) {
        this.optionId = optionId;
        this.projectId = projectId;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.ruledOut = ruledOut;
        this.requirementValues = requirementValues;
        this.notes = notes;
        this.imagePaths = imagePaths;
    }

    public int getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public List<Double> getRequirementValues() {
        return requirementValues;
    }

    public void setRequirementValues(List<Double> requirementValues) {
        this.requirementValues = requirementValues;
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

    @Ignore
    private Option(Parcel in){
        name = in.readString();
        price = in.readDouble();
        rating = in.readDouble();
        ruledOut = in.readInt() == 1;
        in.readList(requirementValues, Double.class.getClassLoader());
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
        parcel.writeList(requirementValues);
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
