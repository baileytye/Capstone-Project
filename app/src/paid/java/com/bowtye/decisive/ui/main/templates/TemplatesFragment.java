package com.bowtye.decisive.ui.main.templates;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bowtye.decisive.R;
import com.bowtye.decisive.ui.login.LoginActivity;
import com.bowtye.decisive.utils.PicassoMenuLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import timber.log.Timber;

import static com.bowtye.decisive.ui.main.home.HomeFragment.EXTRA_SIGN_OUT;

public class TemplatesFragment extends BaseTemplatesFragment{

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);

        if (FirebaseAuth.getInstance().getCurrentUser() != null && !FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
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
}
