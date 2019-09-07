package com.bowtye.decisive.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.bowtye.decisive.database.Converters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "option")
@TypeConverters(Converters.class)
public class Option implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int optionId;
    private int projectId;
    private String name;
    private double price;
    private Float rating;
    private Boolean ruledOut;
    private List<Double> requirementValues;
    private String notes;
    private String imagePath;

    @TypeConverters(Converters.class)
    private Date dateCreated;

    @Ignore
    public Option(String name, double price, Float rating, Boolean ruledOut,
                  List<Double> requirementValues, String notes, String imagePath) {
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.ruledOut = ruledOut;
        this.requirementValues = requirementValues;
        this.notes = notes;
        this.imagePath = imagePath;
    }

    public Option(int optionId, int projectId, String name, double price, Float rating, Boolean ruledOut,
                  List<Double> requirementValues, String notes, String imagePath, Date dateCreated) {
        this.optionId = optionId;
        this.projectId = projectId;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.ruledOut = ruledOut;
        this.requirementValues = requirementValues;
        this.notes = notes;
        this.imagePath = imagePath;
        this.dateCreated = dateCreated;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
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

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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
        dest.writeValue(this.rating);
        dest.writeValue(this.ruledOut);
        dest.writeList(this.requirementValues);
        dest.writeString(this.notes);
        dest.writeString(this.imagePath);
        dest.writeLong(this.dateCreated != null ? this.dateCreated.getTime() : -1);
    }

    @Ignore
    protected Option(Parcel in) {
        this.optionId = in.readInt();
        this.projectId = in.readInt();
        this.name = in.readString();
        this.price = in.readDouble();
        this.rating = (Float) in.readValue(Float.class.getClassLoader());
        this.ruledOut = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.requirementValues = new ArrayList<>();
        in.readList(this.requirementValues, Double.class.getClassLoader());
        this.notes = in.readString();
        this.imagePath = in.readString();
        long tmpDateCreated = in.readLong();
        this.dateCreated = tmpDateCreated == -1 ? null : new Date(tmpDateCreated);
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
