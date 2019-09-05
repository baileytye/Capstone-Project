package com.bowtye.decisive.ui.main.home;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bowtye.decisive.ui.login.LoginActivity;
import com.bowtye.decisive.ui.projectDetails.ProjectDetailsActivity;
import com.bowtye.decisive.utils.PicassoMenuLoader;
import com.bowtye.decisive.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import timber.log.Timber;

public class HomeFragment extends BaseHomeFragment {

    public static final String EXTRA_FIREBASE_ID = "extra_firebase_id";
    public static final String EXTRA_SIGN_OUT = "extra_sign_out";

    private FirebaseUser mUser;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);

        //TODO: fix bug where picture does not load sometimes
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Timber.d("Setting user image");
            PicassoMenuLoader menuLoader = new PicassoMenuLoader(menu.getItem(0), getActivity());
            Picasso.get().load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(menuLoader);
        } else {
            MenuItem item = menu.findItem(R.id.action_sign_out);
            item.setTitle(R.string.menu_sign_in_title);

            item = menu.findItem(R.id.action_profile);
            item.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_sign_out) {
            Intent intent = new Intent(this.getActivity(), LoginActivity.class);
            intent.putExtra(EXTRA_SIGN_OUT, true);
            Objects.requireNonNull(getActivity()).startActivity(intent);
            getActivity().finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProjectItemClicked(int position) {
        if (mUser == null) {
            super.onProjectItemClicked(position);
        } else {
            Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), ProjectDetailsActivity.class);
            intent.putExtra(EXTRA_FIREBASE_ID, mProjects.get(position).getProject().getFirebaseId());

            getActivity().getWindow().setExitTransition(new Slide(Gravity.START));
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        }
    }
}
