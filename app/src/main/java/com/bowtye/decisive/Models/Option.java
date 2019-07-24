package com.bowtye.decisive.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.bowtye.decisive.Database.Converters;

import java.util.ArrayList;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.optionId);
        dest.writeInt(this.projectId);
        dest.writeString(this.name);
        dest.writeDouble(this.price);
        dest.writeDouble(this.rating);
        dest.writeValue(this.ruledOut);
        dest.writeList(this.requirementValues);
        dest.writeString(this.notes);
        dest.writeStringList(this.imagePaths);
    }

    protected Option(Parcel in) {
        this.optionId = in.readInt();
        this.projectId = in.readInt();
        this.name = in.readString();
        this.price = in.readDouble();
        this.rating = in.readDouble();
        this.ruledOut = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.requirementValues = new ArrayList<Double>();
        in.readList(this.requirementValues, Double.class.getClassLoader());
        this.notes = in.readString();
        this.imagePaths = in.createStringArrayList();
    }

    public static final Creator<Option> CREATOR = new Creator<Option>() {
        @Override
        public Option createFromParcel(Parcel source) {
            return new Option(source);
        }

        @Override
        public Option[] newArray(int size) {
            return new Option[size];
        }
    };
}
