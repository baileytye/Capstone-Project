package com.bowtye.decisive.Adapters;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Rating;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.Project;
import com.bowtye.decisive.Models.ProjectWithDetails;
import com.bowtye.decisive.R;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder> {
    RecyclerView.RecycledViewPool sharedPool = new RecyclerView.RecycledViewPool();

    private ProjectWithDetails mProject;
    private OptionItemClickListener mClickCallback;

    public DetailsAdapter(ProjectWithDetails project, OptionItemClickListener clickCallback) {
        mProject = project;
        mClickCallback = clickCallback;
    }

    @NonNull
    @Override
    public DetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_option, parent, false);

        return new DetailsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return (mProject == null) || (mProject.getOptionList() == null) ? 0 : mProject.getOptionList().size();
    }

    public void setProject(ProjectWithDetails p) {
        mProject = p;
    }

    class DetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
        @BindView(R.id.bt_details)
        Button mDetailsButton;
        @BindView(R.id.rb_option_rating)
        RatingBar mOptionRatingBar;

        DetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mDetailsButton.setOnClickListener(this);

            mRequirementsRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            layoutManager.setInitialPrefetchItemCount(mProject.getRequirementList().size());
            mRequirementsRecyclerView.setLayoutManager(layoutManager);
            mRequirementsRecyclerView.setRecycledViewPool(sharedPool);
        }

        void bind() {
            Option o = mProject.getOptionList().get(getAdapterPosition());
            if(o.getImagePath().equals("")) {
                mItemHeaderImageView.setImageDrawable(new ColorDrawable(Color.rgb(0x00, 0x6D, 0xB3)));
            } else {
                Picasso.get()
                        .load(new File(mProject.getOptionList().get(getAdapterPosition()).getImagePath()))
                        .fit()
                        .centerCrop()
                        .into(mItemHeaderImageView);
            }
            mItemTitleTextView.setText(o.getName());

            if(o.getPrice() == 0){
                mItemPriceTextView.setVisibility(View.GONE);
            } else {
                mItemPriceTextView.setVisibility(View.VISIBLE);
                mItemPriceTextView.setText(String.valueOf(o.getPrice()));
            }

            mRatingTextView.setText(String.valueOf(o.getRating()));
            if(o.getRating() >= 0 && o.getRating() <= 5) {
                mOptionRatingBar.setRating((float) o.getRating());
            } else {
                mOptionRatingBar.setRating(0);
            }

            RequirementsAdapter adapter = new RequirementsAdapter(mProject.getRequirementList(),
                    mProject.getOptionList().get(getAdapterPosition()).getRequirementValues());
            mRequirementsRecyclerView.setAdapter(adapter);

        }


        @Override
        public void onClick(View view) {
            mClickCallback.onOptionItemClicked(getAdapterPosition());
        }
    }

    public interface OptionItemClickListener {
        void onOptionItemClicked(int position);
    }
}
