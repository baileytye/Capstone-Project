package com.bowtye.decisive.Adapters;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.Requirement;
import com.bowtye.decisive.R;

import java.util.List;

import butterknife.BindAnim;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.RatingsViewHolder> {

    private Option mOption;
    private List<Requirement> mRequirements;


    public RatingsAdapter(List<Requirement> requirements, Option option) {
        mOption = option;
        mRequirements = requirements;
    }

    @NonNull
    @Override
    public RatingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_rating_details, parent, false);
        return new RatingsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingsViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return (mRequirements != null) ? mRequirements.size() : 0;
    }

    class RatingsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_requirement_name)
        TextView mRequirementNameTextView;
        @BindView(R.id.tv_expected_value)
        TextView mExpectedValueTextView;
        @BindView(R.id.tv_requirement_value)
        TextView mRequirementValueTextView;
        @BindView(R.id.label_priority)
        TextView mPriorityLabel;
        @BindView(R.id.tv_priority)
        TextView mPriorityTextView;
        @BindView(R.id.tv_points_toward_total)
        TextView mPointsTowardTotalTextView;
        @BindView(R.id.cb_expected_value)
        ImageView mExpectedValueCheckBox;
        @BindView(R.id.rb_expected_value)
        RatingBar mExpectedRatingBar;
        @BindView(R.id.rb_requirement_value)
        RatingBar mRequirementValueRatingBar;
        @BindView(R.id.cb_requirement_value)
        ImageView mRequirementValueCheckBox;

        public RatingsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind() {
            Requirement requirement = mRequirements.get(getAdapterPosition());
            setRequirementVisibilityAndValues(requirement, mOption.getRequirementValues().get(getAdapterPosition()),
                    requirement.getExpected());
            mRequirementNameTextView.setText(requirement.getName());
            //TODO set priority, and label, and points toward total
        }

        void setRequirementVisibilityAndValues(Requirement requirement, Double value, Double expected) {
            Timber.d("Requirement: %s, expected: %f", requirement.getName(), requirement.getExpected());
            switch (requirement.getType()) {
                case starRating:
                    mRequirementValueTextView.setVisibility(View.INVISIBLE);
                    mRequirementValueCheckBox.setVisibility(View.INVISIBLE);
                    mRequirementValueRatingBar.setVisibility(View.VISIBLE);
                    mExpectedValueTextView.setVisibility(View.INVISIBLE);
                    mExpectedValueCheckBox.setVisibility(View.INVISIBLE);
                    mExpectedRatingBar.setVisibility(View.VISIBLE);
                    mRequirementValueRatingBar.setRating(value.floatValue());
                    mExpectedRatingBar.setRating(expected.floatValue());
                    break;
                case checkbox:
                    mRequirementValueTextView.setVisibility(View.INVISIBLE);
                    mRequirementValueCheckBox.setVisibility(View.VISIBLE);
                    mRequirementValueRatingBar.setVisibility(View.INVISIBLE);
                    mExpectedValueTextView.setVisibility(View.INVISIBLE);
                    mExpectedValueCheckBox.setVisibility(View.VISIBLE);
                    mExpectedRatingBar.setVisibility(View.INVISIBLE);
                    mExpectedValueCheckBox.setImageResource((expected == 1)
                            ? R.drawable.ic_check_circle_24dp
                            : R.drawable.ic_cancel_24dp);
                    mRequirementValueCheckBox.setImageResource((value == 1)
                            ? R.drawable.ic_check_circle_24dp
                            : R.drawable.ic_cancel_24dp);
                    break;
                case averaging:
                    mRequirementValueTextView.setVisibility(View.VISIBLE);
                    mRequirementValueCheckBox.setVisibility(View.INVISIBLE);
                    mRequirementValueRatingBar.setVisibility(View.INVISIBLE);
                    mExpectedValueTextView.setVisibility(View.VISIBLE);
                    mExpectedValueCheckBox.setVisibility(View.INVISIBLE);
                    mExpectedRatingBar.setVisibility(View.INVISIBLE);
                    mRequirementValueTextView.setText(Requirement.getAveragingString(value, this.itemView.getContext()));
                    mExpectedValueTextView.setText(Requirement.getAveragingString(expected, this.itemView.getContext()));
                    break;
                default:
                    mRequirementValueTextView.setVisibility(View.VISIBLE);
                    mRequirementValueCheckBox.setVisibility(View.INVISIBLE);
                    mRequirementValueRatingBar.setVisibility(View.INVISIBLE);
                    mExpectedValueTextView.setVisibility(View.VISIBLE);
                    mExpectedValueCheckBox.setVisibility(View.INVISIBLE);
                    mExpectedRatingBar.setVisibility(View.INVISIBLE);
                    mExpectedValueTextView.setText(String.valueOf(expected));
                    mRequirementValueTextView.setText(String.valueOf(value));
            }
        }
    }
}
