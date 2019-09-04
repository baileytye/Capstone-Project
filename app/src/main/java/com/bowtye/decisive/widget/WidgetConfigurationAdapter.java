package com.bowtye.decisive.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.R;
import com.bowtye.decisive.models.ProjectWithDetails;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WidgetConfigurationAdapter extends RecyclerView.Adapter<WidgetConfigurationAdapter.WidgetViewHolder> {

    private List<ProjectWithDetails> mProjects;
    private List<ProjectWithDetails> mCompatibleProjects;
    private ProjectClickedInterfaceCallback mCallback;

    public WidgetConfigurationAdapter(ProjectClickedInterfaceCallback callback){
        mCallback = callback;
    }

    @NonNull
    @Override
    public WidgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_project_minimized, parent, false);
        return new WidgetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WidgetViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return (mCompatibleProjects == null) ? 0 : mCompatibleProjects.size();
    }



    public void setProjects(List<ProjectWithDetails> projects) {
        mProjects = projects;
        mCompatibleProjects = new ArrayList<>();

        for(ProjectWithDetails projectWithDetails: mProjects){
            if(projectWithDetails.getOptionList().size() >= 2){
                mCompatibleProjects.add(projectWithDetails);
            }
        }
    }

    public class WidgetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.tv_project_name)
        TextView mProjectNameTextView;

        public WidgetViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(){
            mProjectNameTextView.setText(mProjects.get(getAdapterPosition()).getProject().getName());
        }

        @Override
        public void onClick(View view) {
            mCallback.onProjectClicked(getAdapterPosition());
        }
    }

    interface ProjectClickedInterfaceCallback{
        void onProjectClicked(int position);
    }
}
