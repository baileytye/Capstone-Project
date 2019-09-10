package com.bowtye.decisive.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.models.Requirement;
import com.bowtye.decisive.R;
import com.bowtye.decisive.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RequirementsAdapter extends RecyclerView.Adapter<RequirementsAdapter.RequirementViewHolder> {

    private List<Requirement> mRequirements;
    private List<Double> mRequirementValues;
    private boolean mShowThumb;

    public RequirementsAdapter(List<Requirement> requirements, List<Double> values, boolean showThumb) {
        mRequirements = requirements;
        mRequirementValues = values;
        mShowThumb = showThumb;
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
        return (mRequirements == null || mRequirementValues == null) ? 0 :mRequirements.size();
    }

    public void setRequirementValues(List<Double> values){
        mRequirementValues = values;
    }

    class RequirementViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_requirement_name)
        TextView mRequirementNameTextView;
        @BindView(R.id.tv_requirement_value)
        TextView mRequirementValueTextView;
        @BindView(R.id.iv_requirement_value)
        ImageView mRequirementValueCheckBox;
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
                    mRequirementValueCheckBox.setImageResource((value == 1)
                            ? R.drawable.ic_check_circle_24dp
                            : R.drawable.ic_cancel_24dp);
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
                    mRequirementValueTextView.setText(itemView.getResources()
                            .getString(R.string.concatenation_value_unit,
                                    (value % 1 == 0) ? String.valueOf(value.intValue()): StringUtils.convertToTwoDecimals(value),
                                    requirement.getUnit()));
            }

            if(mShowThumb) {
                if(requirement.getMoreIsBetter()){
                    if (Double.compare(value, requirement.getExpected()) >= 0) {
                        mThumbImageView.setImageResource(R.drawable.ic_arrow_drop_up_24dp);
                    } else {
                        mThumbImageView.setImageResource(R.drawable.ic_arrow_drop_down_24dp);
                    }
                } else {
                    if (Double.compare(value, requirement.getExpected()) <= 0) {
                        mThumbImageView.setImageResource(R.drawable.ic_arrow_drop_up_24dp);
                    } else {
                        mThumbImageView.setImageResource(R.drawable.ic_arrow_drop_down_24dp);
                    }
                }

            } else {
                mThumbImageView.setVisibility(View.GONE);
            }
        }

    }
}
