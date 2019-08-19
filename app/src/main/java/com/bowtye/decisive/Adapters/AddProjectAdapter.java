package com.bowtye.decisive.Adapters;

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
    private OnRequirementClickedCallback mCallback;

    public AddProjectAdapter(OnRequirementClickedCallback callback, List<Requirement> requirements) {
        if (requirements == null) {
            mRequirements = new ArrayList<>();
        } else {
            mRequirements = requirements;
        }
        mCallback = callback;
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

    public void overideRequirement(Requirement requirement, int position) {
        mRequirements.set(position, requirement);
        notifyItemChanged(position);
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

    public class AddRequirementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_requirement_name)
        TextView tvRequirementName;

        public AddRequirementViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        private void bind(Requirement requirement) {
            tvRequirementName.setText(requirement.getName());
        }

        @Override
        public void onClick(View view) {
            mCallback.onRequirementClicked(mRequirements.get(getAdapterPosition()), getAdapterPosition());
        }
    }

    public interface OnRequirementClickedCallback {
        void onRequirementClicked(Requirement requirement, int position);
    }

}
