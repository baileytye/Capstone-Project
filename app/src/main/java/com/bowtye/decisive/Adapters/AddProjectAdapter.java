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
import android.widget.EditText;
import android.widget.ImageButton;
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

    public AddProjectAdapter(Context context){
        mRequirements = new ArrayList<>();
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
            0.0, "", 1.0, false, 0.0
        ));
        notifyItemInserted(mRequirements.size() - 1);
        Timber.d("Item added, Requirements length is %d, name is %s", mRequirements.size(),
                mRequirements.get(mRequirements.size() - 1).getName().toString());
    }

    public void removeAt(int position){
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

    public class AddRequirementViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.et_requirement_name)
        EditText mRequirementNameEditText;
        @BindView(R.id.et_expected)
        EditText mExpectedValueEditText;
        @BindView(R.id.text_input_et_notes)
        TextInputEditText mNotesTextInput;
        @BindView(R.id.sp_type)
        Spinner mTypeSpinner;
        @BindView(R.id.sp_importance)
        Spinner mImportanceSpinner;

        @BindView(R.id.label_saved)
        TextView mSavedLabel;

        @BindView(R.id.ib_delete_requirement)
        ImageButton mDeleteButton;
        @BindView(R.id.ib_save_edit_requirement)
        ImageButton mSaveEditButton;

        KeyListener mKeyListenerName;
        KeyListener mKeyListenerExpected;
        KeyListener mKeyListenerNotes;

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

        void bind(Requirement r){
            Timber.d("Item binding, name: %s", r.getName().toString());

            mIsSaved = false;
            saveDrawable = (AnimatedVectorDrawable) mContext.getDrawable(R.drawable.anim_check_to_edit);
            editDrawable = (AnimatedVectorDrawable) mContext.getDrawable(R.drawable.anim_edit_to_check);
            mSaveEditButton.setImageDrawable(saveDrawable);

            mRequirementNameEditText.setText(r.getName());
            mRequirementNameEditText.setError(null);
            mNotesTextInput.setText(r.getNotes());
            mExpectedValueEditText.setText("");
            mImportanceSpinner.setSelection(0);
            mTypeSpinner.setSelection(0);

            mSaveEditButton.setOnClickListener(view -> {
                saveOrEdit();
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

        void saveOrEdit(){
            if(mIsSaved){
                setEditableState();
            } else {
                String name = mRequirementNameEditText.getText().toString();
                if(name.equals("")){
                    mRequirementNameEditText.setError("Please give this requirement a name");
                    mRequirementNameEditText.requestFocus();
                    return;
                }

                mRequirements.get(getAdapterPosition()).setName(name);
                setSavedState();
            }

        }

        void setSavedState(){

            mSaveEditButton.setImageDrawable(saveDrawable);
            Objects.requireNonNull(saveDrawable).start();

            mKeyListenerName = mRequirementNameEditText.getKeyListener();
            mRequirementNameEditText.setKeyListener(null);

            mKeyListenerExpected = mExpectedValueEditText.getKeyListener();
            mExpectedValueEditText.setKeyListener(null);

            mKeyListenerNotes = mNotesTextInput.getKeyListener();
            mNotesTextInput.setKeyListener(null);

            mSavedLabel.setVisibility(View.VISIBLE);
            mSavedLabel.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scale_in));

            mIsSaved = true;
        }

        void setEditableState(){

            mSaveEditButton.setImageDrawable(editDrawable);
            Objects.requireNonNull(editDrawable).start();

            mRequirementNameEditText.setKeyListener(mKeyListenerName);

            mExpectedValueEditText.setKeyListener(mKeyListenerExpected);
            mNotesTextInput.setKeyListener(mKeyListenerNotes);

            mSavedLabel.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scale_out));
            mSavedLabel.setVisibility(View.INVISIBLE);

            mIsSaved = false;
        }
    }
}
