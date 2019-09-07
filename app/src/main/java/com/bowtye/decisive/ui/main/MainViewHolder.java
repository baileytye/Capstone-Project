package com.bowtye.decisive.ui.main;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.R;
import com.bowtye.decisive.models.Option;
import com.bowtye.decisive.models.ProjectWithDetails;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.iv_project_card_header)
    ImageView mProjectImageView;
    @BindView(R.id.tv_project_card_title)
    TextView mProjectTitle;
    @BindView(R.id.tv_project_choices)
    TextView mChoicesTextView;
    @BindView(R.id.tv_date)
    protected TextView mDateTextView;

    private MainAdapter.ProjectItemClickCallback mProjectItemClickCallback;

    public MainViewHolder(@NonNull View itemView, MainAdapter.ProjectItemClickCallback projectItemClickCallback) {
        super(itemView);
        itemView.setOnClickListener(this);
        mProjectItemClickCallback = projectItemClickCallback;
        ButterKnife.bind(this, itemView);
    }

    public void bind(ProjectWithDetails p) {

        String image = checkForImages(p);

        Timber.d("Image selected for main project: %s", image);

        mProjectImageView.setClipToOutline(true);

        if (image.equals("")) {
            mProjectImageView.setVisibility(View.GONE);
        } else {
            mProjectImageView.setVisibility(View.VISIBLE);
            Picasso.get().setLoggingEnabled(true);
            Picasso.get()
                    .load(image)
                    .fit()
                    .centerCrop()
                    .into(mProjectImageView);
        }

        mProjectTitle.setText(p.getProject().getName());
        mChoicesTextView.setText((p.getOptionList() == null) ? "0" : String.valueOf(p.getOptionList().size()));

        mDateTextView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        Timber.d("Project clicked: %d", getAdapterPosition());
        mProjectItemClickCallback.onProjectItemClicked(getAdapterPosition());
    }

    private String checkForImages(ProjectWithDetails project) {
        for (Option option : project.getOptionList()) {
            if (!option.getImagePath().equals("")) {
                return option.getImagePath();
            }
        }
        return "";
    }

}
