package com.bowtye.decisive.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.Models.Requirement;
import com.bowtye.decisive.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddProjectAdapter extends RecyclerView.Adapter<AddProjectAdapter.AddRequiremnetViewHolder> {

    private int mRequirementCount;
    private List<Requirement> mRequirements;

    public AddProjectAdapter(){
        mRequirements = new ArrayList<>();
    }

    @NonNull
    @Override
    public AddRequiremnetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_add_requirement, parent, false);
        return new AddRequiremnetViewHolder(v);
    }

    public void setRequirements(List<Requirement> list){
        mRequirements = list;
        if(mRequirements != null){
            mRequirementCount = mRequirements.size();
        } else {
            mRequirementCount = 0;
        }
    }

    public void addRequirementCard(){
        mRequirements.add(new Requirement(
           "", Requirement.Type.number, Requirement.Importance.normal,
            0.0, "", 1.0, false, 0.0
        ));
        mRequirementCount++;
    }



    @Override
    public void onBindViewHolder(@NonNull AddRequiremnetViewHolder holder, int position) {
        holder.bind(mRequirements.get(position));
    }

    @Override
    public int getItemCount() {
        return mRequirementCount;
    }

    class AddRequiremnetViewHolder extends RecyclerView.ViewHolder{

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

        @BindView(R.id.ib_delete_requirement)
        ImageButton mDeleteButton;
        @BindView(R.id.ib_edit_requirement)
        ImageButton mEditButton;
        @BindView(R.id.ib_save_requirement)
        ImageButton mSaveButton;


        AddRequiremnetViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Requirement r){

        }
    }
}
