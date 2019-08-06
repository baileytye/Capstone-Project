package com.bowtye.decisive.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.Requirement;
import com.bowtye.decisive.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class AddOptionAdapter extends RecyclerView.Adapter<AddOptionAdapter.AddOptionRequirementViewHolder> {


    private List<Requirement> mRequirements;
    private Option mOption;

    public AddOptionAdapter(List<Requirement> requirements, Option option) {
        mRequirements = requirements;
        mOption = option;
    }

    @NonNull
    @Override
    public AddOptionRequirementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_add_option, parent, false);
        return new AddOptionRequirementViewHolder(v, (mOption != null));
    }

    @Override
    public void onBindViewHolder(@NonNull AddOptionRequirementViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mRequirements.size();
    }

    public class AddOptionRequirementViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.et_requirement_value)
        EditText mRequirementValueEditText;
        @BindView(R.id.cb_requirement_value)
        CheckBox mRequirementValueCheckBox;
        @BindView(R.id.sp_requirement_value)
        Spinner mRequirementValueSpinner;
        @BindView(R.id.rb_requirement_value)
        RatingBar mRequirementValueRatingBar;

        @BindView(R.id.tv_requirement_name)
        TextView mRequirementName;

        boolean mIsEdit;

        AddOptionRequirementViewHolder(@NonNull View itemView, boolean isEdit) {
            super(itemView);
            mIsEdit = isEdit;
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            Requirement.Type type = mRequirements.get(position).getType();
            setVisibleType(type);
            mRequirementName.setText(mRequirements.get(getAdapterPosition()).getName());
            if(mIsEdit){
                switch(type){
                    case starRating:
                        mRequirementValueRatingBar.setRating(mOption.getRequirementValues().get(position).floatValue());
                        break;
                    case checkbox:
                        mRequirementValueCheckBox.setChecked(mOption.getRequirementValues().get(position) == 1);
                        break;
                    case averaging:
                        mRequirementValueSpinner.setSelection(Requirement.getAveragingIndex(
                                mOption.getRequirementValues().get(position), itemView.getContext()));
                        break;
                    case number:
                        mRequirementValueEditText.setText(mOption.getRequirementValues().get(position).toString());
                        break;
                }
            }
        }

        void setVisibleType(Requirement.Type type) {
            Timber.d("Type: %s", type.toString());
            switch (type) {
                case starRating:
                    mRequirementValueCheckBox.setVisibility(View.INVISIBLE);
                    mRequirementValueEditText.setVisibility(View.INVISIBLE);
                    mRequirementValueSpinner.setVisibility(View.INVISIBLE);
                    mRequirementValueRatingBar.setVisibility(View.VISIBLE);
                    return;
                case averaging:
                    mRequirementValueCheckBox.setVisibility(View.INVISIBLE);
                    mRequirementValueEditText.setVisibility(View.INVISIBLE);
                    mRequirementValueSpinner.setVisibility(View.VISIBLE);
                    mRequirementValueRatingBar.setVisibility(View.INVISIBLE);
                    return;
                case checkbox:
                    mRequirementValueCheckBox.setVisibility(View.VISIBLE);
                    mRequirementValueEditText.setVisibility(View.INVISIBLE);
                    mRequirementValueSpinner.setVisibility(View.INVISIBLE);
                    mRequirementValueRatingBar.setVisibility(View.INVISIBLE);
                    return;
                default:
                    mRequirementValueCheckBox.setVisibility(View.INVISIBLE);
                    mRequirementValueEditText.setVisibility(View.VISIBLE);
                    mRequirementValueSpinner.setVisibility(View.INVISIBLE);
                    mRequirementValueRatingBar.setVisibility(View.INVISIBLE);
            }
        }

        public double getRequirementValue() {
            switch (mRequirements.get(getAdapterPosition()).getType()) {
                case checkbox:
                    return (mRequirementValueCheckBox.isChecked()) ? 1 : 0;
                case averaging:
                    return Requirement.getAveragingValue(mRequirementValueSpinner.getSelectedItem().toString(), this.itemView.getContext());
                case starRating:
                    return mRequirementValueRatingBar.getRating();
                default:
                    return mRequirementValueEditText.getText().toString().equals("") ? 0 :
                            Double.parseDouble(mRequirementValueEditText.getText().toString());
            }
        }
    }
}
