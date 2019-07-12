package com.bowtye.decisive.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.Models.Requirement;
import com.bowtye.decisive.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RequirementsAdapter extends RecyclerView.Adapter<RequirementsAdapter.RequirementViewHolder> {

    int mRequirementCount;
    List<Requirement> mRequirements;

    RequirementsAdapter(List<Requirement> requirements){
        mRequirements = requirements;
        mRequirementCount = mRequirements.size();
    }

    @NonNull
    @Override
    public RequirementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_requirement, parent, false);
        return new RequirementViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RequirementViewHolder holder, int position) {
        holder.bind(mRequirements.get(position));
    }

    @Override
    public int getItemCount() {
        return mRequirementCount;
    }

    class RequirementViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_requirement_name) TextView mRequirementNameTextView;
        @BindView(R.id.tv_requirement_value) TextView mRequirementValueTextView;

        RequirementViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Requirement requirement){
            mRequirementNameTextView.setText(requirement.getName());
            mRequirementValueTextView.setText(String.valueOf(requirement.getValue()));
        }
    }
}
