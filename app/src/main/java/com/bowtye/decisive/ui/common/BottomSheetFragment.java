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

    private OnBottomSheetClickCallback callback;

    @BindView(R.id.bt_take_photo)
    Button mTakePhotoButton;
    @BindView(R.id.bt_choose_image)
    Button mChooseImageButton;

    public BottomSheetFragment(OnBottomSheetClickCallback onClickCallback) {
        callback = onClickCallback;
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

    public interface OnBottomSheetClickCallback {
        void onBottomSheetClicked(int id);
    }

}
