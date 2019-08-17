package com.bowtye.decisive.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bowtye.decisive.Helpers.PicassoMenuLoader;
import com.bowtye.decisive.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

import static com.bowtye.decisive.Activities.LoginActivity.SIGN_OUT;

public class MainActivity extends BaseMainActivity{

    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);

        //TODO: fix bug where picture does not load sometimes
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            Timber.d("Setting user image");
            PicassoMenuLoader menuLoader = new PicassoMenuLoader(menu.getItem(0), this);
            Picasso.get().load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(menuLoader);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_sign_out){
            Intent intent = new Intent();
            setResult(SIGN_OUT, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
