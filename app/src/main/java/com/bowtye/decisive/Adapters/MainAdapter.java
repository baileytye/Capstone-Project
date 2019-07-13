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

import com.bowtye.decisive.Models.Project;
import com.bowtye.decisive.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    int mProjectCount;
    List<Project> mProjects;
    final private ProjectItemClickListener mClickListener;

    public MainAdapter(List<Project> projects, ProjectItemClickListener clickListener) {
        mClickListener = clickListener;
        mProjects = projects;
        if (mProjects != null) {
            mProjectCount = mProjects.size();
        } else {
            mProjectCount = 0;
        }
    }

    public void setProjects(List<Project> projects) {
        mProjects = projects;
        if (mProjects != null) {
            mProjectCount = mProjects.size();
        } else {
            mProjectCount = 0;
        }
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

    class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_project_card_header)
        ImageView mProjectImageView;
        @BindView(R.id.tv_project_card_title)
        TextView mProjectTitle;
        @BindView(R.id.tv_project_choices)
        TextView mChoicesTextView;

        MainViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Timber.d("Project clicked: %d", getAdapterPosition());
            mClickListener.onProjectItemClicked(getAdapterPosition());
        }

        void bind(Project p) {
            mProjectImageView.setImageDrawable(new ColorDrawable(Color.rgb(0x03, 0x9B, 0xE5)));
            mProjectTitle.setText(p.getName());
            mChoicesTextView.setText((p.getOptions() == null) ? "0" : String.valueOf(p.getOptions().size()));
        }
    }

    public interface ProjectItemClickListener {
        void onProjectItemClicked(int position);
    }
}
