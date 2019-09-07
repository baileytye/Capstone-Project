package com.bowtye.decisive.ui.main.home;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bowtye.decisive.R;
import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.ui.main.MainAdapter;
import com.bowtye.decisive.ui.main.MainViewHolder;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

public class HomeAdapter extends MainAdapter{

    private ContextMenuClickCallback mContextMenuClickCallback;

    HomeAdapter(List<ProjectWithDetails> projects, ContextMenuClickCallback contextMenuClickCallback, ProjectItemClickCallback projectItemClickCallback) {
        super(projects, projectItemClickCallback);
        mContextMenuClickCallback = contextMenuClickCallback;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_project, parent, false);
        return new HomeViewHolder(v, mProjectClickedCallback);
    }

    public class HomeViewHolder extends MainViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        HomeViewHolder(@NonNull View itemView, ProjectItemClickCallback projectItemClickCallback) {
            super(itemView, projectItemClickCallback);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void bind(ProjectWithDetails p) {
            super.bind(p);

            mDateTextView.setVisibility(View.VISIBLE);
            String date = new SimpleDateFormat("E, dd MMM yyyy", Locale.ENGLISH).format(p.getProject().getDateCreated());
            mDateTextView.setText(date);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle(mProjects.get(getAdapterPosition()).getProject().getName());
            MenuItem deleteActionItem = contextMenu.add("Delete");
            deleteActionItem.setOnMenuItemClickListener(menuItem -> {
                mContextMenuClickCallback.onProjectDeleteMenuClicked(getAdapterPosition());
                return true;
            });
            MenuItem editActionItem = contextMenu.add("Edit");
            editActionItem.setOnMenuItemClickListener(menuItem -> {
                mContextMenuClickCallback.onProjectEditMenuClicked(getAdapterPosition());
                return true;
            });

        }

    }

    public interface ContextMenuClickCallback{
        void onProjectDeleteMenuClicked(int position);
        void onProjectEditMenuClicked(int position);
    }


}