package com.bowtye.decisive.Adapters;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.Project;
import com.bowtye.decisive.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder> {

    private int mOptionsCount;
    private Project mProject;

    public DetailsAdapter(Project project) {
        mProject = project;
        if(project != null ) {
            mOptionsCount = project.getOptions().size();
        } else {
            mOptionsCount = 0;
        }
    }

    @NonNull
    @Override
    public DetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_option, parent, false);
        return new DetailsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsViewHolder holder, int position) {
        holder.bind(mProject.getOptions().get(position));
    }

    @Override
    public int getItemCount() {
        return mOptionsCount;
    }

    public void setProject(Project p){
        mProject = p;
        if(mProject.getOptions() != null) {
            mOptionsCount = mProject.getOptions().size();
        } else {
            mOptionsCount = 0;
        }
    }

    class DetailsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_item_card_title)
        TextView mItemTitleTextView;
        @BindView(R.id.iv_item_card_header)
        ImageView mItemHeaderImageView;
        @BindView(R.id.tv_item_card_price)
        TextView mItemPriceTextView;
        @BindView(R.id.rv_requirements)
        RecyclerView mRequirementsRecyclerView;
        @BindView(R.id.tv_rating)
        TextView mRatingTextView;

        DetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Option o) {
            mItemHeaderImageView.setImageDrawable(new ColorDrawable(Color.rgb(0x00, 0x6D, 0xB3)));
            mItemTitleTextView.setText(o.getName());
            mItemPriceTextView.setText(String.valueOf(o.getPrice()));

            mRequirementsRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            mRequirementsRecyclerView.setLayoutManager(layoutManager);
            RequirementsAdapter adapter = new RequirementsAdapter(mProject.getRequirements(),
                    o.getRequirementValues());
            mRequirementsRecyclerView.setAdapter(adapter);
        }

    }
}
