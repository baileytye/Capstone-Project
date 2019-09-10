package com.bowtye.decisive.ui.addRequirement;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bowtye.decisive.R;
import com.bowtye.decisive.models.Requirement;
import com.bowtye.decisive.utils.RequestCode;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_EDIT_REQUIREMENT;
import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_REQUIREMENT;

public class AddRequirement extends AppCompatActivity {

    public static final int VALIDATION_OK = 1;
    public static final int VALIDATION_NAME_ERROR = -2;
    public static final int VALIDATION_WEIGHT_ERROR = -3;

    public static final int RESULT_REQ_DELETED = 2;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.et_requirement_name)
    TextInputEditText etRequirementName;
    @BindView(R.id.sp_type)
    Spinner spType;
    @BindView(R.id.frame_type)
    FrameLayout frameType;
    @BindView(R.id.sp_importance)
    Spinner spImportance;
    @BindView(R.id.frame_importance)
    FrameLayout frameImportance;
    @BindView(R.id.et_weight)
    TextInputEditText etWeight;
    @BindView(R.id.til_weight)
    TextInputLayout tilWeight;
    @BindView(R.id.sp_expected_averages)
    Spinner spExpectedAverages;
    @BindView(R.id.frame_expected_averages)
    FrameLayout frameExpectedAverages;
    @BindView(R.id.cb_expected)
    CheckBox cbExpected;
    @BindView(R.id.et_expected)
    TextInputEditText etExpected;
    @BindView(R.id.til_expected)
    TextInputLayout tilExpected;
    @BindView(R.id.frame_expected_star_rating)
    FrameLayout frameExpectedStarRating;
    @BindView(R.id.rb_expected)
    RatingBar rbExpected;
    @BindView(R.id.et_unit)
    TextInputEditText etUnit;
    @BindView(R.id.til_unit)
    TextInputLayout tilUnit;
    @BindView(R.id.sp_more_is_better)
    Spinner spMoreIsBetter;
    @BindView(R.id.frame_more_is_better)
    FrameLayout frameMoreIsBetter;


    private Requirement mRequirement;
    private Boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_requirement);
        ButterKnife.bind(this);

        mRequirement = new Requirement(
                "", Requirement.Type.number, Requirement.Importance.normal,
                0.0, "", 1.0, true, ""
        );
        isEdit = false;

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_EDIT_REQUIREMENT)) {
                mRequirement = intent.getParcelableExtra(EXTRA_EDIT_REQUIREMENT);
                isEdit = true;
            }
        }

        prepareViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_requirement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                return true;
            case R.id.action_save:
                switch (validateAndSave()) {
                    case VALIDATION_OK:
                        Timber.d("Result ok, adding requirement to intent out");
                        Intent out = new Intent();
                        out.putExtra(EXTRA_REQUIREMENT, mRequirement);
                        setResult(RESULT_OK, out);
                        finishAfterTransition();
                        break;
                    case VALIDATION_NAME_ERROR:
                        etRequirementName.setError("Give this requirement a name");
                        etRequirementName.requestFocus();
                        break;
                    case VALIDATION_WEIGHT_ERROR:
                        etWeight.setError("Enter a weight");
                        etWeight.requestFocus();
                        break;
                }
                return true;
            case R.id.action_delete_requirement:
                if (isEdit) {
                    Intent out = new Intent();
                    out.putExtra(EXTRA_REQUIREMENT, mRequirement);
                    setResult(RESULT_REQ_DELETED, out);
                }
                finishAfterTransition();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareViews() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbarLayout.setTitle((isEdit) ? "Edit requirement" : "Add requirement");

        setExpectedVisibility();
        setInitialValues(mRequirement);
        setListeners();
    }

    private void setExpectedVisibility() {
        switch (mRequirement.getType()) {
            case averaging:
                cbExpected.setVisibility(View.INVISIBLE);
                frameExpectedAverages.setVisibility(View.VISIBLE);
                tilExpected.setVisibility(View.INVISIBLE);
                frameExpectedStarRating.setVisibility(View.INVISIBLE);
                frameMoreIsBetter.setVisibility(View.INVISIBLE);
                tilUnit.setVisibility(View.INVISIBLE);
                break;
            case checkbox:
                cbExpected.setVisibility(View.VISIBLE);
                frameExpectedAverages.setVisibility(View.INVISIBLE);
                tilExpected.setVisibility(View.INVISIBLE);
                frameExpectedStarRating.setVisibility(View.INVISIBLE);
                frameMoreIsBetter.setVisibility(View.INVISIBLE);
                tilUnit.setVisibility(View.INVISIBLE);
                break;
            case starRating:
                cbExpected.setVisibility(View.INVISIBLE);
                frameExpectedAverages.setVisibility(View.INVISIBLE);
                tilExpected.setVisibility(View.INVISIBLE);
                frameExpectedStarRating.setVisibility(View.VISIBLE);
                frameMoreIsBetter.setVisibility(View.INVISIBLE);
                tilUnit.setVisibility(View.INVISIBLE);
                break;
            case number:
                cbExpected.setVisibility(View.INVISIBLE);
                frameExpectedAverages.setVisibility(View.INVISIBLE);
                tilExpected.setVisibility(View.VISIBLE);
                frameExpectedStarRating.setVisibility(View.INVISIBLE);
                frameMoreIsBetter.setVisibility(View.VISIBLE);
                tilUnit.setVisibility(View.VISIBLE);
                break;
        }
    }

    private int validateAndSave() {
        String name = Objects.requireNonNull(etRequirementName.getText()).toString();

        if (name.equals("")) {
            return VALIDATION_NAME_ERROR;
        }

        if (etWeight.getVisibility() == View.VISIBLE &&
                Objects.requireNonNull(etWeight.getText()).toString().equals("")) {
            return VALIDATION_WEIGHT_ERROR;
        }

        saveState(name);

        return VALIDATION_OK;
    }

    /**
     * Saves the values in this viewholder to mRequirements
     */
    void saveState(String name) {
        mRequirement.setName(name);

        double customWeight = 0;
        if (!Objects.requireNonNull(etWeight.getText()).toString().equals("")) {
            customWeight = Double.parseDouble(etWeight.getText().toString());
        }

        mRequirement.setImportanceAndWeightFromString(spImportance.getSelectedItem().toString(), customWeight);
        Requirement.Type type = Requirement.getTypeFromString(spType.getSelectedItem().toString());
        mRequirement.setType(type);
        mRequirement.setExpected(getExpectedValue(type));
        if(type == Requirement.Type.number){
            mRequirement.setUnit(Objects.requireNonNull(etUnit.getText()).toString());
            mRequirement.setMoreIsBetter(spMoreIsBetter.getSelectedItemPosition() == 0);
        }
    }

    /**
     * Retrieve expected value based on spinner value.
     *
     * @param type type of requirement
     * @return expected value of requirement
     */
    double getExpectedValue(Requirement.Type type) {
        switch (type) {
            case checkbox:
                return (cbExpected.isChecked()) ? 1.0 : 0;
            case averaging:
                return Requirement.getAveragingValue(spExpectedAverages.getSelectedItem().toString(), this);
            case starRating:
                return rbExpected.getRating();
            default:
                if (Objects.requireNonNull(etExpected.getText()).toString().equals("")) {
                    return 0;
                } else {
                    return Double.parseDouble(etExpected.getText().toString());
                }
        }
    }

    void setExpectedValue(Requirement requirement) {
        switch (requirement.getType()) {
            case checkbox:
                cbExpected.setChecked(requirement.getExpected() == 1);
                break;
            case averaging:
                spExpectedAverages.setSelection(Requirement.getAveragingIndex(requirement.getExpected(), this));
                break;
            case starRating:
                rbExpected.setRating(requirement.getExpected().floatValue());
                break;
            default:
                if (requirement.getExpected() != 0) {
                    etExpected.setText(String.valueOf(requirement.getExpected()));
                } else {
                    etExpected.setText("");
                }
                etUnit.setText(requirement.getUnit());
                spMoreIsBetter.setSelection((requirement.getMoreIsBetter()) ? 0 : 1);
        }
    }

    void setInitialValues(Requirement requirement) {

        etRequirementName.setText(requirement.getName());
        etRequirementName.setError(null);

        spType.setSelection(requirement.getType().ordinal());
        spImportance.setSelection(requirement.getImportance().ordinal());

        setExpectedValue(requirement);

        etWeight.setText(String.valueOf(requirement.getWeight()));
        etWeight.setError(null);

    }

    void setListeners() {

        spImportance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spImportance.getItemAtPosition(i).toString().equals("Custom")) {
                    etWeight.setVisibility(View.VISIBLE);
                    tilWeight.setVisibility(View.VISIBLE);
                } else {
                    etWeight.setVisibility(View.GONE);
                    tilWeight.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mRequirement.setType(Requirement.getTypeFromString(spType.getItemAtPosition(i).toString()));
                setExpectedVisibility();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        etRequirementName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mRequirement.setName(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
