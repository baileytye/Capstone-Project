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

import com.bowtye.decisive.POJOs.Project;
import com.bowtye.decisive.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder>{

    int mProjectCount;
    List<Project> mProjects;

    public MainAdapter(List<Project> projects){
        mProjects = projects;
        mProjectCount = mProjects.size();
    }


    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_project, parent, false);
        return new MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        holder.bind(mProjects.get(position));
    }

    @Override
    public int getItemCount() {
        return mProjectCount;
    }

    class MainViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.iv_project_card_header) ImageView mProjectImageView;
        @BindView(R.id.tv_project_card_title) TextView mProjectTitle;
        @BindView(R.id.tv_project_choices) TextView mChoicesTextView;

        MainViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Project p){
            mProjectImageView.setImageDrawable(new ColorDrawable(Color.CYAN));
            mProjectTitle.setText(p.getName());
            mChoicesTextView.setText(String.valueOf(p.getRequirements().size()));
        }
    }
}
