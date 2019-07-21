package com.bowtye.decisive.Adapters;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.Models.Requirement;
import com.bowtye.decisive.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class AddProjectAdapter extends RecyclerView.Adapter<AddProjectAdapter.AddRequirementViewHolder> {

    private List<Requirement> mRequirements;
    private Context mContext;

    public AddProjectAdapter(Context context, List<Requirement> requirements){
        if(requirements == null) {
            mRequirements = new ArrayList<>();
        } else {
            mRequirements = requirements;
        }
        mContext = context;
    }

    @NonNull
    @Override
    public AddRequirementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_add_requirement, parent, false);
        return new AddRequirementViewHolder(v);
    }

    public void setRequirements(List<Requirement> list){
        mRequirements = list;
    }

    public void addRequirementCard(){
        mRequirements.add(new Requirement(
           "", Requirement.Type.number, Requirement.Importance.normal,
            0.0, "", 1.0, 0.0
        ));
        notifyItemInserted(mRequirements.size() - 1);
        Timber.d("Item added, Requirements length is %d, name is %s", mRequirements.size(),
                mRequirements.get(mRequirements.size() - 1).getName().toString());
    }

    private void removeAt(int position){
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

    public List<Requirement> getRequirements(){
        return mRequirements;
    }

    /**
     * Viewholder of an add requirement card
     */
    public class AddRequirementViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.et_requirement_name)
        EditText mRequirementNameEditText;
        @BindView(R.id.text_input_et_notes)
        TextInputEditText mNotesTextInput;
        @BindView(R.id.sp_type)
        Spinner mTypeSpinner;
        @BindView(R.id.sp_importance)
        Spinner mImportanceSpinner;
        @BindView(R.id.et_weight)
        EditText mWeightEditText;
        @BindView(R.id.label_weight)
        TextView mWeightLabelTextView;

        @BindView(R.id.rb_star_rating)
        RatingBar mExpectedRatingBar;
        @BindView(R.id.cb_expected)
        CheckBox mExpectedCheckBox;
        @BindView(R.id.sp_expected_averages)
        Spinner mExpectedAverages;
        @BindView(R.id.et_expected)
        EditText mExpectedValueEditText;

        @BindView(R.id.label_saved)
        TextView mSavedLabel;

        @BindView(R.id.ib_delete_requirement)
        ImageButton mDeleteButton;
        @BindView(R.id.ib_save_edit_requirement)
        ImageButton mSaveEditButton;

        KeyListener mKeyListenerName;
        KeyListener mKeyListenerExpected;
        KeyListener mKeyListenerNotes;
        KeyListener mKeyListenerWeight;

        Boolean mIsSaved;

        AnimatedVectorDrawable saveDrawable;
        AnimatedVectorDrawable editDrawable;


        AddRequirementViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public boolean getIsSaved(){
            return mIsSaved;
        }

        /**
         * Binds data to requirement card
         * @param r requirement to bind
         */
        void bind(Requirement r){
            Timber.d("Item binding, name: %s", r.getName().toString());

            mIsSaved = false;
            saveDrawable = (AnimatedVectorDrawable) mContext.getDrawable(R.drawable.anim_check_to_edit);
            editDrawable = (AnimatedVectorDrawable) mContext.getDrawable(R.drawable.anim_edit_to_check);
            mSaveEditButton.setImageDrawable(saveDrawable);

            mRequirementNameEditText.setText(r.getName());
            mRequirementNameEditText.setError(null);
            mWeightEditText.setError(null);
            mNotesTextInput.setText(r.getNotes());
            mExpectedValueEditText.setText("");
            mImportanceSpinner.setSelection(0);
            mTypeSpinner.setSelection(0);

            mImportanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(mImportanceSpinner.getItemAtPosition(i).toString().equals("Custom")){
                        mWeightEditText.setVisibility(View.VISIBLE);
                        mWeightLabelTextView.setVisibility(View.VISIBLE);
                    } else {
                        mWeightEditText.setVisibility(View.GONE);
                        mWeightLabelTextView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    switch (mTypeSpinner.getItemAtPosition(i).toString()){
                        case "Number":
                            mExpectedValueEditText.setVisibility(View.VISIBLE);
                            mExpectedRatingBar.setVisibility(View.INVISIBLE);
                            mExpectedCheckBox.setVisibility(View.INVISIBLE);
                            mExpectedAverages.setVisibility(View.INVISIBLE);
                            break;
                        case "Star Rating":
                            mExpectedValueEditText.setVisibility(View.INVISIBLE);
                            mExpectedRatingBar.setVisibility(View.VISIBLE);
                            mExpectedCheckBox.setVisibility(View.INVISIBLE);
                            mExpectedAverages.setVisibility(View.INVISIBLE);
                            break;
                        case "Checkbox":
                            mExpectedValueEditText.setVisibility(View.INVISIBLE);
                            mExpectedRatingBar.setVisibility(View.INVISIBLE);
                            mExpectedCheckBox.setVisibility(View.VISIBLE);
                            mExpectedAverages.setVisibility(View.INVISIBLE);
                            break;
                        case "Above/Below Avg":
                            mExpectedValueEditText.setVisibility(View.INVISIBLE);
                            mExpectedRatingBar.setVisibility(View.INVISIBLE);
                            mExpectedCheckBox.setVisibility(View.INVISIBLE);
                            mExpectedAverages.setVisibility(View.VISIBLE);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            mSaveEditButton.setOnClickListener(view -> {
                swapState();
            });

            mDeleteButton.setOnClickListener(view -> {
                removeAt(getAdapterPosition());
            });

            mRequirementNameEditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    mRequirements.get(getAdapterPosition()).setName(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        /**
         * Swap the state from saved to editable and vice versa
         */
        void swapState(){
            if(mIsSaved){
                setEditableState();
            } else {
                String name = mRequirementNameEditText.getText().toString();
                if(name.equals("")){
                    mRequirementNameEditText.setError("Give this requirement a name");
                    mRequirementNameEditText.requestFocus();
                    return;
                }

                if(mWeightEditText.getVisibility() == View.VISIBLE && mWeightEditText.getText().toString().equals("")){
                    mWeightEditText.setError("Enter a weight");
                    mWeightEditText.requestFocus();
                    return;
                }

                saveState();
                setSavedState();
            }

        }

        /**
         * Sets the state of this requirement card to saved and uneditable
         */
        void setSavedState(){

            mSaveEditButton.setImageDrawable(saveDrawable);
            Objects.requireNonNull(saveDrawable).start();

            mKeyListenerName = mRequirementNameEditText.getKeyListener();
            mRequirementNameEditText.setKeyListener(null);

            mKeyListenerExpected = mExpectedValueEditText.getKeyListener();
            mExpectedValueEditText.setKeyListener(null);

            mKeyListenerNotes = mNotesTextInput.getKeyListener();
            mNotesTextInput.setKeyListener(null);

            mKeyListenerWeight = mWeightEditText.getKeyListener();
            mWeightEditText.setKeyListener(null);

            mSavedLabel.setVisibility(View.VISIBLE);
            mSavedLabel.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scale_in));

            mTypeSpinner.setEnabled(false);
            mImportanceSpinner.setEnabled(false);

            mIsSaved = true;
        }

        /**
         * Sets the state of this requirement card to editable
         */
        void setEditableState(){

            mSaveEditButton.setImageDrawable(editDrawable);
            Objects.requireNonNull(editDrawable).start();

            mRequirementNameEditText.setKeyListener(mKeyListenerName);
            mExpectedValueEditText.setKeyListener(mKeyListenerExpected);
            mNotesTextInput.setKeyListener(mKeyListenerNotes);
            mWeightEditText.setKeyListener(mKeyListenerWeight);

            mSavedLabel.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scale_out));
            mSavedLabel.setVisibility(View.INVISIBLE);

            mTypeSpinner.setEnabled(true);
            mImportanceSpinner.setEnabled(true);

            mIsSaved = false;
        }

        /**
         * Retrieve the importance based on the spinner value. Also sets the weight in mRequirements.
         * @return importance
         */
        Requirement.Importance getImportance() {
            switch (mImportanceSpinner.getSelectedItem().toString()) {
                case "High":
                    mRequirements.get(getAdapterPosition()).setWeight(Requirement.WEIGHT_HIGH);
                    return Requirement.Importance.high;
                case "Normal":
                    mRequirements.get(getAdapterPosition()).setWeight(Requirement.WEIGHT_NORMAL);
                    return Requirement.Importance.normal;
                case "Low":
                    mRequirements.get(getAdapterPosition()).setWeight(Requirement.WEIGHT_LOW);
                    return Requirement.Importance.low;
                case "Custom":
                    mRequirements.get(getAdapterPosition()).setWeight(Double.parseDouble(mWeightEditText.getText().toString()));
                    return Requirement.Importance.custom;
                case "Exclude":
                    mRequirements.get(getAdapterPosition()).setWeight(Requirement.WEIGHT_EXCLUDE);
                    return Requirement.Importance.exclude;
            }
            return Requirement.Importance.normal;
        }

        Requirement.Type getType(){
            switch (mTypeSpinner.getSelectedItem().toString()){
                case "Number":
                    return Requirement.Type.number;
                case "Star Rating":
                    return Requirement.Type.starRating;
                case "Checkbox":
                    return Requirement.Type.checkbox;
                case "Above/Below Avg":
                    return Requirement.Type.averaging;
            }
            return Requirement.Type.number;
        }

        /**
         * Retrieve expected value based on spinner value.
         * @param type type of requirement
         * @return expected value of requirement
         */
        double getExpectedValue(Requirement.Type type){
            switch(type) {
                case checkbox:
                    return (mExpectedCheckBox.isChecked()) ? 1.0 : 0;
                case averaging:
                    switch(mExpectedAverages.getSelectedItem().toString()){
                        case "Far Above Average":
                            return Requirement.FAR_ABOVE_AVERAGE;
                        case "Above Average":
                            return Requirement.ABOVE_AVERAGE;
                        case "Average":
                            return Requirement.AVERAGE;
                        case "Below Average":
                            return Requirement.BELOW_AVERAGE ;
                        case "Far Below Average":
                            return Requirement.FAR_BELOW_AVERAGE;
                    }
                case starRating:
                    return mExpectedRatingBar.getRating();
                default:
                    if(mExpectedValueEditText.getText().toString().equals("")){
                        return 0;
                    } else {
                        return Double.parseDouble(mExpectedValueEditText.getText().toString());
                    }
            }
        }

        /**
         * Saves the values in this viewholder to mRequirements
         */
        void saveState(){
            mRequirements.get(getAdapterPosition()).setName(mRequirementNameEditText.getText().toString());
            mRequirements.get(getAdapterPosition()).setImportance(getImportance());
            mRequirements.get(getAdapterPosition()).setNotes(Objects.requireNonNull(mNotesTextInput.getText()).toString());
            Requirement.Type type = getType();
            mRequirements.get(getAdapterPosition()).setType(type);
            mRequirements.get(getAdapterPosition()).setExpected(getExpectedValue(type));
        }

    }
}
