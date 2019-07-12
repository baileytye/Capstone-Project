package com.bowtye.decisive.Adapters;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.Project;
import com.bowtye.decisive.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder>{

    int mOptionsCount;
    Project mProject;

    public DetailsAdapter(Project project){
        mProject = project;
        mOptionsCount = project.getOptions().size();
    }


    @NonNull
    @Override
    public DetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_project_item, parent, false);
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

    class DetailsViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_item_card_title) TextView mItemTitle;
        @BindView(R.id.iv_item_card_header) ImageView mItemHeaderImage;

        DetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Option o){
            mItemHeaderImage.setImageDrawable(new ColorDrawable(Color.BLUE));
            mItemTitle.setText(o.getName());
        }
    }
}
