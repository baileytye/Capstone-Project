package com.bowtye.decisive.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.R;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainViewHolder> {

    protected List<ProjectWithDetails> mProjects;
    protected ProjectItemClickCallback mProjectClickedCallback;

    public MainAdapter(List<ProjectWithDetails> projects, ProjectItemClickCallback clickCallback) {
        mProjects = projects;
        mProjectClickedCallback = clickCallback;
    }

    public void setProjects(List<ProjectWithDetails> projects) {
        mProjects = projects;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_project, parent, false);
        return new MainViewHolder(v, mProjectClickedCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        holder.bind(mProjects.get(position));
    }

    @Override
    public int getItemCount() {
        return (mProjects == null)? 0 : mProjects.size();
    }

    public interface ProjectItemClickCallback {
        void onProjectItemClicked(int position);
    }
}
