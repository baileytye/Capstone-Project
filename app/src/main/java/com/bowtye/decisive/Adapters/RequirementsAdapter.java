package com.bowtye.decisive.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.Models.Requirement;
import com.bowtye.decisive.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RequirementsAdapter extends RecyclerView.Adapter<RequirementsAdapter.RequirementViewHolder> {

    private List<Requirement> mRequirements;
    private List<Double> mRequirementValues;

    RequirementsAdapter(List<Requirement> requirements, List<Double> values) {
        mRequirements = requirements;
        mRequirementValues = values;
    }

    @NonNull
    @Override
    public RequirementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_requirement, parent, false);
        return new RequirementViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RequirementViewHolder holder, int position) {
        holder.bind(mRequirements.get(position), mRequirementValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mRequirements.size();
    }

    class RequirementViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_requirement_name)
        TextView mRequirementNameTextView;
        @BindView(R.id.tv_requirement_value)
        TextView mRequirementValueTextView;
        @BindView(R.id.cb_requirement_value)
        CheckBox mRequirementValueCheckBox;
        @BindView(R.id.rb_requirement_value)
        RatingBar mRequirementValueRatingBar;
        @BindView(R.id.iv_thumb)
        ImageView mThumbImageView;

        RequirementViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Requirement requirement, Double value) {
            mRequirementNameTextView.setText(requirement.getName() + ':');
            setRequirementVisibilityAndValues(requirement, value);
        }

        void setRequirementVisibilityAndValues(Requirement requirement, Double value){
            switch(requirement.getType()){
                case starRating:
                    mRequirementValueTextView.setVisibility(View.INVISIBLE);
                    mRequirementValueCheckBox.setVisibility(View.INVISIBLE);
                    mRequirementValueRatingBar.setVisibility(View.VISIBLE);
                    mRequirementValueRatingBar.setRating(value.floatValue());
                    break;
                case checkbox:
                    mRequirementValueTextView.setVisibility(View.INVISIBLE);
                    mRequirementValueCheckBox.setVisibility(View.VISIBLE);
                    mRequirementValueRatingBar.setVisibility(View.INVISIBLE);
                    mRequirementValueCheckBox.setChecked(value == 1);
                    break;
                case averaging:
                    mRequirementValueTextView.setVisibility(View.VISIBLE);
                    mRequirementValueCheckBox.setVisibility(View.INVISIBLE);
                    mRequirementValueRatingBar.setVisibility(View.INVISIBLE);
                    mRequirementValueTextView.setText(Requirement.getAveragingString(value, this.itemView.getContext()));
                    break;
                default:
                    mRequirementValueTextView.setVisibility(View.VISIBLE);
                    mRequirementValueCheckBox.setVisibility(View.INVISIBLE);
                    mRequirementValueRatingBar.setVisibility(View.INVISIBLE);
                    mRequirementValueTextView.setText(String.valueOf(value));
            }

            if(Double.compare(value, requirement.getExpected()) >= 0){
                mThumbImageView.setImageResource(R.drawable.ic_thumb_up_24dp);
            } else {
                mThumbImageView.setImageResource(R.drawable.ic_thumb_down_24dp);
            }
        }

    }
}
