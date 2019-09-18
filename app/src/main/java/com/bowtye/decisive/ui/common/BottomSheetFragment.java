package com.bowtye.decisive.ui.common;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bowtye.decisive.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_IMAGE = 2;
    public static final int REMOVE_IMAGE = 3;

    private OnBottomSheetClickCallback callback;

    @BindView(R.id.bt_take_photo)
    Button mTakePhotoButton;
    @BindView(R.id.bt_choose_image)
    Button mChooseImageButton;
    @BindView(R.id.bt_remove_image)
    Button mRemoveImageButton;

    private boolean mImagePresent;

    public BottomSheetFragment(OnBottomSheetClickCallback onClickCallback, boolean imagePresent) {
        callback = onClickCallback;
        mImagePresent = imagePresent;
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(), getTheme());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_image_select, container, false);
        ButterKnife.bind(this, view);

        if(!mImagePresent){
            mRemoveImageButton.setVisibility(View.GONE);
        }

        return view;
    }

    @OnClick(R.id.bt_take_photo)
    void takePhoto() {
        callback.onBottomSheetClicked(TAKE_PHOTO);
    }

    @OnClick(R.id.bt_choose_image)
    void chooseImage() {
        callback.onBottomSheetClicked(CHOOSE_IMAGE);
    }

    @OnClick(R.id.bt_remove_image)
    void removeImage() { callback.onBottomSheetClicked(REMOVE_IMAGE); }

    public interface OnBottomSheetClickCallback {
        void onBottomSheetClicked(int id);
    }

}
