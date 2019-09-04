package com.bowtye.decisive.ui.projectDetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.models.Option;
import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.R;
import com.bowtye.decisive.ui.common.VerticalSpaceItemDecoration;
import com.bowtye.decisive.ui.common.RequirementsAdapter;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ProjectDetailsAdapter extends RecyclerView.Adapter<ProjectDetailsAdapter.ViewHolder> {
    RecyclerView.RecycledViewPool sharedPool = new RecyclerView.RecycledViewPool();

    private static final int TYPE_SUMMARY = 0;
    private static final int TYPE_OPTION = 1;

    private ProjectWithDetails mProject;
    private OptionItemClickListener mClickCallback;
    private boolean displaySummary;

    public ProjectDetailsAdapter(ProjectWithDetails project, OptionItemClickListener clickCallback) {
        mProject = project;
        mClickCallback = clickCallback;

        setDisplaySummary();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        switch (viewType){
            case TYPE_SUMMARY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_project_summary, parent, false);
                return new SummaryViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_option, parent, false);
                return new DetailsViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        if(mProject == null || mProject.getOptionList() == null){
            return 0;
        } else {
            if(displaySummary){
                return mProject.getOptionList().size() + 1;
            } else {
                return mProject.getOptionList().size();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 && displaySummary){
            return TYPE_SUMMARY;
        } else {
            return TYPE_OPTION;
        }
    }

    public void setProject(ProjectWithDetails p) {
        mProject = p;
        setDisplaySummary();
    }

    public void clearRecyclerPool(){
        sharedPool.clear();
    }

    private void setDisplaySummary(){
        if(mProject != null && mProject.getOptionList() != null){
            displaySummary = mProject.getOptionList().size() > 1;
        }
    }

    private int getHighestRatedIndex(){
        int index = 0;
        Float highest = (float) 0;
        for(int i = 0; i < mProject.getOptionList().size() ; i++){
            Option option = mProject.getOptionList().get(i);
            if(option.getRating() > highest){
                index = i;
            }
        }
        return index;
    }

    private int getLowestRatedIndex(){
        int index = 0;
        Float lowest = (float) 5;
        for(int i = 0; i < mProject.getOptionList().size() ; i++){
            Option option = mProject.getOptionList().get(i);
            if(option.getRating() < lowest){
                index = i;
            }
        }
        return index;
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bind();
    }

    class DetailsViewHolder extends ViewHolder implements View.OnClickListener {

        @BindView(R.id.cardView)
        CardView mCardView;
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
        @BindView(R.id.rb_option_rating)
        RatingBar mOptionRatingBar;

        DetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mCardView.setOnClickListener(this);

            mRequirementsRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            layoutManager.setInitialPrefetchItemCount(mProject.getRequirementList().size());
            mRequirementsRecyclerView.setLayoutManager(layoutManager);
            mRequirementsRecyclerView.setRecycledViewPool(sharedPool);
            mRequirementsRecyclerView.setOnClickListener(null);
            VerticalSpaceItemDecoration dividerItemDecoration = new VerticalSpaceItemDecoration(
                    (int)itemView.getContext().getResources().getDimension(R.dimen.requirement_item_separation));
            mRequirementsRecyclerView.addItemDecoration(dividerItemDecoration);
        }

        void bind() {

            int index = (displaySummary) ? getAdapterPosition() - 1 : getAdapterPosition();
            Option o = mProject.getOptionList().get(index);
            if (o.getImagePath().equals("")) {
                mItemHeaderImageView.setVisibility(View.GONE);
            } else {
                mItemHeaderImageView.setVisibility(View.VISIBLE);
                Timber.d("Image selected for card project: %s", o.getImagePath());
                Picasso.get()
                        .load(o.getImagePath())
                        .fit()
                        .centerCrop()
                        .into(mItemHeaderImageView);
            }
            mItemTitleTextView.setText(o.getName());

            if (o.getPrice() == 0) {
                mItemPriceTextView.setVisibility(View.GONE);
            } else {
                mItemPriceTextView.setVisibility(View.VISIBLE);
                mItemPriceTextView.setText("$" + String.valueOf(o.getPrice()));
            }


            mRatingTextView.setText(String.format(Locale.getDefault(), "%.2f", o.getRating()));
            if (o.getRating() >= 0 && o.getRating() <= 5) {
                mOptionRatingBar.setRating(o.getRating());
            } else {
                mOptionRatingBar.setRating(0);
            }

            RequirementsAdapter adapter = new RequirementsAdapter(mProject.getRequirementList(),
                    o.getRequirementValues(), false);
            mRequirementsRecyclerView.setAdapter(adapter);
        }

        @Override
        public void onClick(View view) {
            mClickCallback.onOptionItemClicked(displaySummary ? getAdapterPosition() - 1 : getAdapterPosition());
        }
    }

    class SummaryViewHolder extends ViewHolder {

        @BindView(R.id.tv_highest_name)
        TextView mHighestNameTextView;
        @BindView(R.id.tv_lowest_name)
        TextView mLowestNameTextView;
        @BindView(R.id.include_highest_rating)
        View mHighestRatedInclude;
        @BindView(R.id.include_lowest_rating)
        View mLowestRatedInclude;

        TextView mHighestRatingTextView;
        RatingBar mHighestOptionRatingBar;
        TextView mLowestRatingTextView;
        RatingBar mLowestOptionRatingBar;

        public SummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mHighestOptionRatingBar = mHighestRatedInclude.findViewById(R.id.rb_option_rating);
            mHighestRatingTextView = mHighestRatedInclude.findViewById(R.id.tv_rating);
            mLowestOptionRatingBar = mLowestRatedInclude.findViewById(R.id.rb_option_rating);
            mLowestRatingTextView = mLowestRatedInclude.findViewById(R.id.tv_rating);
        }

        public void bind(){
            int highestIndex = getHighestRatedIndex();
            int lowestIndex = getLowestRatedIndex();
            Option highestOption = mProject.getOptionList().get(highestIndex);
            Option lowestOption = mProject.getOptionList().get(lowestIndex);

            mHighestNameTextView.setText(highestOption.getName());
            mHighestRatingTextView.setText(String.format(Locale.getDefault(), "%.2f", highestOption.getRating()));
            mHighestOptionRatingBar.setRating(highestOption.getRating());

            mLowestNameTextView.setText(lowestOption.getName());
            mLowestRatingTextView.setText(String.format(Locale.getDefault(), "%.2f", lowestOption.getRating()));
            mLowestOptionRatingBar.setRating(lowestOption.getRating());

        }
    }

    public interface OptionItemClickListener {
        void onOptionItemClicked(int position);
    }
}
