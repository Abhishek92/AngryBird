package com.android.angrybird.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.android.angrybird.R;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.database.User;
import com.android.angrybird.databinding.ActivityAddEditUserBinding;
import com.android.angrybird.fragment.DatePickerFragment;
import com.android.angrybird.util.DateTimeUtil;
import com.android.angrybird.util.FileUtils;
import com.bumptech.glide.Glide;

import org.parceler.Parcels;

public class AddEditUserActivity extends BaseActivity<ActivityAddEditUserBinding> implements DatePickerFragment.IDateSetListener {

    public static final String KEY_USER_DATA = "KEY_USER_DATA";
    private ActivityAddEditUserBinding viewBinding;
    private String mFirstName;
    private String mLastName;
    private String mMiddleName;
    private String mContactOne;
    private String mContactTwo;
    private String mDateOfBirth;
    private String mAddress;
    private String gender;
    private String mImageFilePath;
    private User user;
    private ActionBar mActionBar;

    @Override
    protected void onCreateCustom(ActivityAddEditUserBinding viewBinding) {
        this.viewBinding = viewBinding;
        user = Parcels.unwrap(getIntent().getParcelableExtra(KEY_USER_DATA));
        mActionBar = getSupportActionBar();
        viewBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != user)
                    updateUserToDatabase();
                else
                    addUserToDatabase();
            }
        });

        viewBinding.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker();
            }
        });

        viewBinding.dobEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.setDateListener(AddEditUserActivity.this);
                fragment.show(getSupportFragmentManager(),DatePickerFragment.TAG);
            }
        });

        if(null != user)
        {
            if(null != mActionBar)
                mActionBar.setTitle("Edit user");
            setUserDataForEdit();
        }
        else
        {
            if(null != mActionBar)
                mActionBar.setTitle("Add user");
        }
    }

    private void setUserDataForEdit()
    {
        viewBinding.firstNameEt.setText(user.getFirstName());
        viewBinding.lastNameEt.setText(user.getLastName());
        viewBinding.middleNameEt.setText(user.getMiddleName());
        viewBinding.contactOneEt.setText(user.getContactOne());
        viewBinding.contactTwoEt.setText(user.getContactTwo());
        viewBinding.dobEt.setText(user.getDateOfBirth());
        viewBinding.addressEt.setText(user.getAddress());
        viewBinding.maleRb.setChecked(user.getGender().equalsIgnoreCase("1"));
        viewBinding.femaleRb.setChecked(user.getGender().equalsIgnoreCase("0"));
        Glide.with(this).load(user.getUserImagePath()).centerCrop().placeholder(R.drawable.ic_account_circle_black_24dp).into(viewBinding.userImage);

    }

    private void addUserToDatabase() {
        if(validate()) {
            User user = new User();
            user.setAddress(mAddress);
            user.setContactOne(mContactOne);
            user.setContactTwo(mContactTwo);
            user.setCreatedDate(DateTimeUtil.getCurrentDateTime());
            user.setDateOfBirth(mDateOfBirth);
            user.setFirstName(mFirstName);
            user.setLastName(mLastName);
            user.setMiddleName(mMiddleName);
            user.setGender(gender);
            user.setModifiedDate("");
            user.setStatus(false);
            user.setUserImagePath(mImageFilePath);

            DBManager.INSTANCE.getDaoSession().getUserDao().insert(user);
            finish();
        }
    }

    private void updateUserToDatabase()
    {
        if(validate()) {
            user.setAddress(mAddress);
            user.setContactOne(mContactOne);
            user.setContactTwo(mContactTwo);
            user.setDateOfBirth(mDateOfBirth);
            user.setFirstName(mFirstName);
            user.setLastName(mLastName);
            user.setMiddleName(mMiddleName);
            user.setGender(gender);
            user.setModifiedDate(DateTimeUtil.getCurrentDateTime());
            user.setStatus(false);
            user.setUserImagePath(mImageFilePath);

            DBManager.INSTANCE.getDaoSession().getUserDao().update(user);
            finish();
        }
    }

    private boolean validate()
    {
        mFirstName = viewBinding.firstNameEt.getText().toString();
        mLastName = viewBinding.lastNameEt.getText().toString();
        mMiddleName = viewBinding.middleNameEt.getText().toString();
        mContactOne = viewBinding.contactOneEt.getText().toString();
        mContactTwo = viewBinding.contactTwoEt.getText().toString();
        mDateOfBirth = viewBinding.dobEt.getText().toString();
        mAddress = viewBinding.addressEt.getText().toString();
        gender = viewBinding.genderRg.getCheckedRadioButtonId() == R.id.maleRb ? "1" : "0";
        if(TextUtils.isEmpty(mFirstName))
        {
            Toast.makeText(this, "First name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(TextUtils.isEmpty(mLastName))
        {
            Toast.makeText(this, "Last name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(TextUtils.isEmpty(mContactOne))
        {
            Toast.makeText(this, "Contact one is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(mContactOne.length() != 10)
        {
            Toast.makeText(this, "Contact one must be of atleast 10 digit", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(TextUtils.isEmpty(mDateOfBirth))
        {
            Toast.makeText(this, "Date of birth is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(TextUtils.isEmpty(mAddress))
        {
            Toast.makeText(this, "Address is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected int getActivityContentView() {
        return R.layout.activity_add_edit_user;
    }

    @Override
    protected void onLoadImage(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        mImageFilePath = FileUtils.storeImage(this, bitmap);
        Glide.with(this).load(filePath).centerCrop().placeholder(R.drawable.ic_account_circle_black_24dp).into(viewBinding.userImage);
    }


    @Override
    public void onDateSet(int year, int month, int day) {
        mDateOfBirth = DateTimeUtil.getFormattedDate(year, month, day);
        viewBinding.dobEt.setText(mDateOfBirth);
    }
}
