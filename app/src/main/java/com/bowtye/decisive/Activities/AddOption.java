package com.bowtye.decisive.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bowtye.decisive.Adapters.AddOptionAdapter;
import com.bowtye.decisive.BuildConfig;
import com.bowtye.decisive.Fragments.BottomSheetFragment;
import com.bowtye.decisive.Helpers.ViewUtils;
import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.ProjectWithDetails;
import com.bowtye.decisive.Models.Requirement;
import com.bowtye.decisive.R;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.bowtye.decisive.Fragments.BottomSheetFragment.CHOOSE_IMAGE;
import static com.bowtye.decisive.Fragments.BottomSheetFragment.TAKE_PHOTO;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_OPTION;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_PROJECT;

public class AddOption extends AppCompatActivity implements BottomSheetFragment.OnBottomSheetClickCallback {

    public static final int VALIDATION_OK = 55;
    public static final int VALIDATION_NAME_ERROR = -2;
    public static final int VALIDATION_HOLDER_ERROR = -1;

    public static final int GALLERY_REQUEST_CODE = 2553;
    public static final int CAMERA_REQUEST_CODE = 1234;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitleTextView;
    @BindView(R.id.rv_add_option)
    RecyclerView mRecyclerView;
    @BindView(R.id.ti_option_name)
    TextInputEditText mOptionNameEditText;
    @BindView(R.id.ti_price)
    TextInputEditText mPriceEditText;
    @BindView(R.id.iv_pictures)
    ImageView mPicturesImageView;
    @BindView(R.id.text_input_et_notes)
    EditText mNotesEditText;

    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    List<Requirement> mRequirements;
    Option mOption;

    String currentPhotoPath;

    BottomSheetFragment mSheetDialog;
    boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_option);

        ButterKnife.bind(this);

        mOption = new Option("", 0, (float) 0, false, new ArrayList<>(), "", "");
        isEdit = false;

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_PROJECT)) {
                ProjectWithDetails p = intent.getParcelableExtra(EXTRA_PROJECT);
                if (p != null) {
                    mRequirements = p.getRequirementList();
                }
            }
            if(intent.hasExtra(EXTRA_OPTION)){
                mOption = intent.getParcelableExtra(EXTRA_OPTION);
                isEdit = true;
            }
        }
        prepareViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                return true;
            case R.id.action_save:
                switch (validateAndSave()) {
                    case VALIDATION_NAME_ERROR:
                        ViewUtils.showErrorDialog("Save Option",
                                "Please give this option a name", this);
                        break;
                    case VALIDATION_HOLDER_ERROR:
                        ViewUtils.showErrorDialog("Save Option",
                                "Please fill the requirement values", this);
                        break;
                    case VALIDATION_OK:
                        Intent out = new Intent();
                        out.putExtra(EXTRA_OPTION, mOption);
                        setResult(RESULT_OK, out);
                        finishAfterTransition();
                        return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    if(selectedImage != null) {
                        try {
                            File photoFile = createImageFile();
                            InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                            FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                            // Copying
                            copyStream(Objects.requireNonNull(inputStream), fileOutputStream);
                            fileOutputStream.close();
                            inputStream.close();
                        } catch (Exception e) {
                            Timber.e("Error occurred while creating image: %s", e.getMessage());
                            break;
                        }
                    }
                    saveImageAndSetHeaderImage();
                    break;
                case CAMERA_REQUEST_CODE:
                    saveImageAndSetHeaderImage();
                    break;
            }
    }

    private void saveImageAndSetHeaderImage(){
        File f = new File(currentPhotoPath);
        Uri takenImage = Uri.fromFile(f);
        mOption.setImagePath((takenImage == null) ? "" : takenImage.toString());
        mPicturesImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mPicturesImageView.setImageURI(takenImage);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.d("Permission granted, launching camera");
                    captureFromCamera();
                } else {
                    mSheetDialog.dismiss();
                }
            }
        }
    }

    /**
     * Prepares views
     */
    private void prepareViews() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbarTitleTextView.setText("Add Option");

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        if(isEdit) {
            mOptionNameEditText.setText(mOption.getName());
            mPriceEditText.setText(String.valueOf(mOption.getPrice()));
            mNotesEditText.setText(mOption.getNotes());
            mAdapter = new AddOptionAdapter(mRequirements, mOption);

            if(!mOption.getImagePath().equals("")) {
                Picasso.get()
                        .load(mOption.getImagePath())
                        .fit()
                        .centerCrop()
                        .into(mPicturesImageView);
                mPicturesImageView.setOnClickListener(null);
            } else {
                setPlaceholderImage();
            }
        } else {
            if (!mOption.getImagePath().equals("")) {
                mPicturesImageView.setImageURI(Uri.parse(currentPhotoPath));
                mPicturesImageView.setOnClickListener(null);
            } else {
                setPlaceholderImage();
            }
            mAdapter = new AddOptionAdapter(mRequirements, null);
        }

        mRecyclerView.setAdapter(mAdapter);
    }

    private void setPlaceholderImage(){
        mPicturesImageView.setImageResource(R.drawable.ic_add_a_photo_24dp);
        mPicturesImageView.setOnClickListener(view -> {
            mSheetDialog = new BottomSheetFragment(this);
            FragmentManager fm = getSupportFragmentManager();
            mSheetDialog.show(fm, "modalSheetDialog");
        });
    }

    private int validateAndSave() {
        String name = Objects.requireNonNull(mOptionNameEditText.getText()).toString();
        if (name.equals("")) {
            return VALIDATION_NAME_ERROR;
        } else {
            mOption.setName(name);
        }

        double price;
        String priceString = Objects.requireNonNull(mPriceEditText.getText()).toString();
        if (priceString.equals("")) {
            price = 0;
        } else {
            price = Double.parseDouble(priceString);
        }
        mOption.setPrice(price);

        if(isEdit) {
            Timber.d("IsEdit, setting requirement values");
            for (int i = 0; i < mRequirements.size(); i++) {
                AddOptionAdapter.AddOptionRequirementViewHolder holder =
                        (AddOptionAdapter.AddOptionRequirementViewHolder)
                                mRecyclerView.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    mOption.getRequirementValues().set(i, holder.getRequirementValue());
                } else {
                    return VALIDATION_HOLDER_ERROR;
                }
            }
        } else {
            for (int i = 0; i < mRequirements.size(); i++) {
                AddOptionAdapter.AddOptionRequirementViewHolder holder =
                        (AddOptionAdapter.AddOptionRequirementViewHolder)
                                mRecyclerView.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    mOption.getRequirementValues().add(holder.getRequirementValue());
                } else {
                    return VALIDATION_HOLDER_ERROR;
                }
            }
        }

        mOption.setNotes(mNotesEditText.getText().toString());

        return VALIDATION_OK;
    }

    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void captureFromCamera() {
        Timber.d("Capture from camera");

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Timber.d("Permissions not granted");
                // Permission is not granted
                // Should we show an explanation?
                Timber.d("Requesting permissions");
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                return;
            }
        }
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Timber.d("Dispatching picture intent");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        Timber.d(takePictureIntent.resolveActivity(getPackageManager()).toShortString());
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Timber.d("IO Exception: %s", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                Timber.d("Launching Camera");
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    @Override
    public void onBottomSheetClicked(int id) {
        switch (id) {
            case TAKE_PHOTO:
                captureFromCamera();
                break;
            case CHOOSE_IMAGE:
                pickFromGallery();
                break;
        }
        mSheetDialog.dismiss();
    }
}
