package com.bowtye.decisive.ui.projectDetails;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Outline;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.models.Option;
import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.R;
import com.bowtye.decisive.ui.common.VerticalSpaceItemDecoration;
import com.bowtye.decisive.ui.common.RequirementsAdapter;
import com.squareup.picasso.Picasso;

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

        if (viewType == TYPE_SUMMARY) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_project_summary, parent, false);
            return new SummaryViewHolder(view);
        }

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_option, parent, false);
        return new DetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        if (mProject == null || mProject.getOptionList() == null) {
            return 0;
        } else {
            if (displaySummary) {
                return mProject.getOptionList().size() + 1;
            } else {
                return mProject.getOptionList().size();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && displaySummary) {
            return TYPE_SUMMARY;
        } else {
            return TYPE_OPTION;
        }
    }

    public void setProject(ProjectWithDetails p) {
        mProject = p;
        setDisplaySummary();
    }

    void clearRecyclerPool() {
        sharedPool.clear();
    }

    private void setDisplaySummary() {
        if (mProject != null && mProject.getOptionList() != null) {
            displaySummary = mProject.getOptionList().size() > 1;
        }
    }

    private int getHighestRatedIndex() {
        int index = 0;
        Float highest = (float) 0;
        for (int i = 0; i < mProject.getOptionList().size(); i++) {
            Option option = mProject.getOptionList().get(i);
            if (option.getRating() > highest) {
                highest = option.getRating();
                index = i;
            }
        }
        return index;
    }

    private int getLowestRatedIndex() {
        int index = 0;
        Float lowest = (float) 5;
        for (int i = 0; i < mProject.getOptionList().size(); i++) {
            Option option = mProject.getOptionList().get(i);
            if (option.getRating() < lowest) {
                lowest = option.getRating();
                index = i;
            }
        }
        return index;
    }

    abstract class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(@NonNull View itemView) {
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
        @BindView(R.id.iv_requirements_expand)
        ImageView mExpandRequirementsImageView;

        int maxHeight;
        int recyclerHeight;
        boolean isExpanded;

        DetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mCardView.setOnClickListener(this);

            Timber.d("Constructor called");

            mRequirementsRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            layoutManager.setInitialPrefetchItemCount(mProject.getRequirementList().size());
            mRequirementsRecyclerView.setLayoutManager(layoutManager);
            mRequirementsRecyclerView.setRecycledViewPool(sharedPool);
            mRequirementsRecyclerView.setOnClickListener(null);
            VerticalSpaceItemDecoration dividerItemDecoration = new VerticalSpaceItemDecoration(
                    (int) itemView.getContext().getResources().getDimension(R.dimen.requirement_item_separation));
            mRequirementsRecyclerView.addItemDecoration(dividerItemDecoration);
            updateHeights();
        }

        void updateHeights() {
            //Need to set visibility of recycler to get measured before shrinking it
            mRequirementsRecyclerView.setVisibility(View.INVISIBLE);

            mCardView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    mCardView.getViewTreeObserver().removeOnPreDrawListener(this);

                    recyclerHeight = mRequirementsRecyclerView.getHeight();
                    Timber.d("Recycler height: %d", recyclerHeight);
                    if (isExpanded) {
                        mRequirementsRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        mRequirementsRecyclerView.setVisibility(View.GONE);
                    }
                    maxHeight = mCardView.getHeight();
                    Timber.d("maxHeight: %d", maxHeight);
                    Timber.d("isExpanded: %b", isExpanded);

                    return true;
                }
            });
        }

        void bind() {

            int index = (displaySummary) ? getAdapterPosition() - 1 : getAdapterPosition();
            Option o = mProject.getOptionList().get(index);

            Timber.d("Bind called for: %s", o.getName());

            mItemHeaderImageView.setClipToOutline(true);
            if (o.getImagePath().equals("")) {
                mItemHeaderImageView.setVisibility(View.GONE);
            } else {
                mItemHeaderImageView.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        float cornerRadius = view.getContext().getResources().getDimension(R.dimen.card_corner_radius);
                        outline.setRoundRect(0, 0, view.getWidth(), (int) (view.getHeight() + cornerRadius), cornerRadius);
                    }
                });
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
                mItemPriceTextView.setText("$" + o.getPrice());
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

            mExpandRequirementsImageView.setOnClickListener(view -> toggleRequirements());

        }

        void toggleRequirements() {

            if (!isExpanded) {
                mRequirementsRecyclerView.setVisibility(View.VISIBLE);
                Timber.d("Expanding, maxHeight: %d, rec height: %d", maxHeight, recyclerHeight);
                expandView(maxHeight);
                isExpanded = true;
                mExpandRequirementsImageView.setImageResource(R.drawable.ic_keyboard_arrow_up_24dp);
                mExpandRequirementsImageView.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.grey500));
            } else {
                Timber.d("Collapsing maxHeight: %d, rec height: %d", maxHeight, mRequirementsRecyclerView.getHeight());
                mRequirementsRecyclerView.setVisibility(View.GONE);
                collapseView();
                isExpanded = false;
                mExpandRequirementsImageView.setImageResource(R.drawable.ic_keyboard_arrow_down_24dp);
                mExpandRequirementsImageView.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.grey500));
            }
        }

        private void collapseView() {

            ValueAnimator animCollapse = ValueAnimator.ofInt(mCardView.getMeasuredHeightAndState(),
                    maxHeight - recyclerHeight);
            animCollapse.addUpdateListener(valueAnimator -> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mCardView.getLayoutParams();
                layoutParams.height = val;
                mCardView.setLayoutParams(layoutParams);
            });
            animCollapse.start();
        }

        private void expandView(int height) {

            ValueAnimator animExpand = ValueAnimator.ofInt(mCardView.getMeasuredHeightAndState(),
                    height);
            ObjectAnimator animAlpha = ObjectAnimator.ofFloat(mRequirementsRecyclerView, "alpha", 0f, 1f);
            animAlpha.setInterpolator(new AccelerateInterpolator());

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(300);
            animatorSet.playTogether(animExpand, animAlpha);

            animExpand.addUpdateListener(valueAnimator -> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mCardView.getLayoutParams();
                layoutParams.height = val;
                mCardView.setLayoutParams(layoutParams);
            });

            animatorSet.start();

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

        SummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mHighestOptionRatingBar = mHighestRatedInclude.findViewById(R.id.rb_option_rating);
            mHighestRatingTextView = mHighestRatedInclude.findViewById(R.id.tv_rating);
            mLowestOptionRatingBar = mLowestRatedInclude.findViewById(R.id.rb_option_rating);
            mLowestRatingTextView = mLowestRatedInclude.findViewById(R.id.tv_rating);
        }

        public void bind() {
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
