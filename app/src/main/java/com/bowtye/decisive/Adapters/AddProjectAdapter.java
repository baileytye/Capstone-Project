package com.bowtye.decisive.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.Models.Requirement;
import com.bowtye.decisive.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class AddProjectAdapter extends RecyclerView.Adapter<AddProjectAdapter.AddRequirementViewHolder> {

    private List<Requirement> mRequirements;
    private Context mContext;

    public AddProjectAdapter(Context context, List<Requirement> requirements) {
        if (requirements == null) {
            mRequirements = new ArrayList<>();
        } else {
            mRequirements = requirements;
        }
        mContext = context;
    }

    @NonNull
    @Override
    public AddRequirementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_requirement_preview, parent, false);
        return new AddRequirementViewHolder(v);
    }

    public void setRequirements(List<Requirement> list) {
        mRequirements = list;
    }

    public void addRequirement(Requirement requirement) {
        mRequirements.add(requirement);
        notifyItemInserted(mRequirements.size() - 1);
        Timber.d("Item added, Requirements length is %d, name is %s", mRequirements.size(),
                mRequirements.get(mRequirements.size() - 1).getName());
    }

    private void removeAt(int position) {
        mRequirements.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mRequirements.size() - 1);
        Timber.d("Item removed at %d, Requirements length is %d", position, mRequirements.size());
    }

    @Override
    public void onBindViewHolder(@NonNull AddRequirementViewHolder holder, int position) {
        holder.bind(mRequirements.get(position));
    }

    @Override
    public int getItemCount() {
        return mRequirements.size();
    }

    public List<Requirement> getRequirements() {
        return mRequirements;
    }

    public class AddRequirementViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_requirement_name)
        TextView tvRequirementName;

        public AddRequirementViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(Requirement requirement) {
            tvRequirementName.setText(requirement.getName());
        }
    }

//    /**
//     * Viewholder of an add requirement card
//     */
//    public class AddRequirementViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.et_requirement_name)
//        EditText mRequirementNameEditText;
//        @BindView(R.id.text_input_et_notes)
//        TextInputEditText mNotesTextInput;
//        @BindView(R.id.sp_type)
//        Spinner mTypeSpinner;
//        @BindView(R.id.sp_importance)
//        Spinner mImportanceSpinner;
//        @BindView(R.id.et_weight)
//        EditText mWeightEditText;
//        @BindView(R.id.label_weight)
//        TextView mWeightLabelTextView;
//
//        @BindView(R.id.rb_star_rating)
//        RatingBar mExpectedRatingBar;
//        @BindView(R.id.cb_expected)
//        CheckBox mExpectedCheckBox;
//        @BindView(R.id.sp_expected_averages)
//        Spinner mExpectedAveragesSpinner;
//        @BindView(R.id.et_expected)
//        EditText mExpectedValueEditText;
//
//        @BindView(R.id.label_saved)
//        TextView mSavedLabel;
//
//        @BindView(R.id.ib_delete_requirement)
//        ImageButton mDeleteButton;
//        @BindView(R.id.ib_save_edit_requirement)
//        ImageButton mSaveEditButton;
//
//        KeyListener mKeyListenerName;
//        KeyListener mKeyListenerExpected;
//        KeyListener mKeyListenerNotes;
//        KeyListener mKeyListenerWeight;
//
//        Boolean mIsSaved;
//
//        AnimatedVectorDrawable saveDrawable;
//        AnimatedVectorDrawable editDrawable;
//
//        View mItemView;
//
//
//        AddRequirementViewHolder(@NonNull View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//            mItemView = itemView;
//            setListeners();
//        }
//
//        public boolean getIsSaved() {
//            return mIsSaved;
//        }
//
//        /**
//         * Binds data to requirement card
//         *
//         * @param requirement requirement to bind
//         */
//        void bind(Requirement requirement) {
//            Timber.d("Item binding, name: %s", requirement.getName());
//
//            mIsSaved = false;
//            saveDrawable = (AnimatedVectorDrawable) mContext.getDrawable(R.drawable.anim_check_to_edit);
//            editDrawable = (AnimatedVectorDrawable) mContext.getDrawable(R.drawable.anim_edit_to_check);
//            mSaveEditButton.setImageDrawable(saveDrawable);
//
//            setInitialValues(requirement);
//        }
//
//        void setListeners(){
//
//            mImportanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                    if (mImportanceSpinner.getItemAtPosition(i).toString().equals("Custom")) {
//                        mWeightEditText.setVisibility(View.VISIBLE);
//                        mWeightLabelTextView.setVisibility(View.VISIBLE);
//                    } else {
//                        mWeightEditText.setVisibility(View.GONE);
//                        mWeightLabelTextView.setVisibility(View.GONE);
//                    }
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> adapterView) {
//
//                }
//            });
//
//            mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                    switch (mTypeSpinner.getItemAtPosition(i).toString()) {
//                        case "Number":
//                            mExpectedValueEditText.setVisibility(View.VISIBLE);
//                            mExpectedRatingBar.setVisibility(View.INVISIBLE);
//                            mExpectedCheckBox.setVisibility(View.INVISIBLE);
//                            mExpectedAveragesSpinner.setVisibility(View.INVISIBLE);
//                            break;
//                        case "Star Rating":
//                            mExpectedValueEditText.setVisibility(View.INVISIBLE);
//                            mExpectedRatingBar.setVisibility(View.VISIBLE);
//                            mExpectedCheckBox.setVisibility(View.INVISIBLE);
//                            mExpectedAveragesSpinner.setVisibility(View.INVISIBLE);
//                            break;
//                        case "Checkbox":
//                            mExpectedValueEditText.setVisibility(View.INVISIBLE);
//                            mExpectedRatingBar.setVisibility(View.INVISIBLE);
//                            mExpectedCheckBox.setVisibility(View.VISIBLE);
//                            mExpectedAveragesSpinner.setVisibility(View.INVISIBLE);
//                            break;
//                        case "Above/Below Avg":
//                            mExpectedValueEditText.setVisibility(View.INVISIBLE);
//                            mExpectedRatingBar.setVisibility(View.INVISIBLE);
//                            mExpectedCheckBox.setVisibility(View.INVISIBLE);
//                            mExpectedAveragesSpinner.setVisibility(View.VISIBLE);
//                            break;
//                    }
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> adapterView) {
//
//                }
//            });
//
//            mSaveEditButton.setOnClickListener(view -> validateAndSwapState());
//
//            mDeleteButton.setOnClickListener(view -> removeAt(getAdapterPosition()));
//
//            mRequirementNameEditText.addTextChangedListener(new TextWatcher() {
//
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    mRequirements.get(getAdapterPosition()).setName(charSequence.toString());
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//
//                }
//            });
//        }
//
//        void setInitialValues(Requirement requirement){
//
//            mRequirementNameEditText.setText(requirement.getName());
//            mRequirementNameEditText.setError(null);
//
//            mTypeSpinner.setSelection(requirement.getType().ordinal());
//            mImportanceSpinner.setSelection(requirement.getImportance().ordinal());
//
//            setExpectedValue(requirement);
//
//            mWeightEditText.setText(String.valueOf(requirement.getWeight()));
//            mWeightEditText.setError(null);
//
//            mNotesTextInput.setText(requirement.getNotes());
//        }
//
//        /**
//         * Swap the state from saved to editable and vice versa
//         */
//        void validateAndSwapState() {
//            if (mIsSaved) {
//                setEditableState();
//            } else {
//                String name = mRequirementNameEditText.getText().toString();
//                if (name.equals("")) {
//                    mRequirementNameEditText.setError("Give this requirement a name");
//                    mRequirementNameEditText.requestFocus();
//                    return;
//                }
//
//                if (mWeightEditText.getVisibility() == View.VISIBLE && mWeightEditText.getText().toString().equals("")) {
//                    mWeightEditText.setError("Enter a weight");
//                    mWeightEditText.requestFocus();
//                    return;
//                }
//
//                saveState();
//                setSavedState();
//            }
//
//        }
//
//        /**
//         * Sets the state of this requirement card to saved and uneditable
//         */
//        void setSavedState() {
//
//            mSaveEditButton.setImageDrawable(saveDrawable);
//            Objects.requireNonNull(saveDrawable).start();
//
//            mKeyListenerName = mRequirementNameEditText.getKeyListener();
//            mRequirementNameEditText.setKeyListener(null);
//
//            mKeyListenerExpected = mExpectedValueEditText.getKeyListener();
//            mExpectedValueEditText.setKeyListener(null);
//            mExpectedValueEditText.setEnabled(false);
//
//            mKeyListenerNotes = mNotesTextInput.getKeyListener();
//            mNotesTextInput.setKeyListener(null);
//
//            mKeyListenerWeight = mWeightEditText.getKeyListener();
//            mWeightEditText.setKeyListener(null);
//            mWeightEditText.setEnabled(false);
//
//            mSavedLabel.setVisibility(View.VISIBLE);
//            mSavedLabel.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scale_in));
//
//            mTypeSpinner.setEnabled(false);
//            mImportanceSpinner.setEnabled(false);
//            mExpectedRatingBar.setEnabled(false);
//            mExpectedAveragesSpinner.setEnabled(false);
//            mExpectedCheckBox.setEnabled(false);
//
//            mIsSaved = true;
//            ViewUtils.hideKeyboardFrom(mContext, mItemView);
//        }
//
//        /**
//         * Sets the state of this requirement card to editable
//         */
//        void setEditableState() {
//
//            mSaveEditButton.setImageDrawable(editDrawable);
//            Objects.requireNonNull(editDrawable).start();
//
//            mRequirementNameEditText.setKeyListener(mKeyListenerName);
//            mExpectedValueEditText.setKeyListener(mKeyListenerExpected);
//            mNotesTextInput.setKeyListener(mKeyListenerNotes);
//            mWeightEditText.setKeyListener(mKeyListenerWeight);
//
//            mWeightEditText.setEnabled(true);
//            mExpectedValueEditText.setEnabled(true);
//
//            mSavedLabel.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scale_out));
//            mSavedLabel.setVisibility(View.INVISIBLE);
//
//            mTypeSpinner.setEnabled(true);
//            mImportanceSpinner.setEnabled(true);
//            mExpectedRatingBar.setEnabled(true);
//            mExpectedAveragesSpinner.setEnabled(true);
//            mExpectedCheckBox.setEnabled(true);
//
//            mIsSaved = false;
//        }
//
//        /**
//         * Retrieve expected value based on spinner value.
//         *
//         * @param type type of requirement
//         * @return expected value of requirement
//         */
//        double getExpectedValue(Requirement.Type type) {
//            switch (type) {
//                case checkbox:
//                    return (mExpectedCheckBox.isChecked()) ? 1.0 : 0;
//                case averaging:
//                    return Requirement.getAveragingValue(mExpectedAveragesSpinner.getSelectedItem().toString(), this.mItemView.getContext());
//                case starRating:
//                    return mExpectedRatingBar.getRating();
//                default:
//                    if (mExpectedValueEditText.getText().toString().equals("")) {
//                        return 0;
//                    } else {
//                        return Double.parseDouble(mExpectedValueEditText.getText().toString());
//                    }
//            }
//        }
//
//        void setExpectedValue(Requirement requirement){
//            switch (requirement.getType()) {
//                case checkbox:
//                    mExpectedCheckBox.setChecked(requirement.getExpected() == 1);
//                    break;
//                case averaging:
//                    mExpectedAveragesSpinner.setSelection(Requirement.getAveragingIndex(requirement.getExpected(), this.mItemView.getContext()));
//                    break;
//                case starRating:
//                    mExpectedRatingBar.setRating(requirement.getExpected().floatValue());
//                    break;
//                default:
//                    if(requirement.getExpected() != 0) {
//                        mExpectedValueEditText.setText(String.valueOf(requirement.getExpected()));
//                    } else {
//                        mExpectedValueEditText.setText("");
//                    }
//            }
//        }
//
//        /**
//         * Saves the values in this viewholder to mRequirements
//         */
//        void saveState() {
//            mRequirements.get(getAdapterPosition()).setName(mRequirementNameEditText.getText().toString());
//
//            double customWeight = 0;
//            if(!mWeightEditText.getText().toString().equals("")){
//                customWeight = Double.parseDouble(mWeightEditText.getText().toString());
//            }
//
//            mRequirements.get(getAdapterPosition()).setImportanceAndWeightFromString(
//                    mImportanceSpinner.getSelectedItem().toString(), customWeight);
//            mRequirements.get(getAdapterPosition()).setNotes(Objects.requireNonNull(mNotesTextInput.getText()).toString());
//            Requirement.Type type = Requirement.getTypeFromString(mTypeSpinner.getSelectedItem().toString());
//            mRequirements.get(getAdapterPosition()).setType(type);
//            mRequirements.get(getAdapterPosition()).setExpected(getExpectedValue(type));
//        }
//
//    }
}
