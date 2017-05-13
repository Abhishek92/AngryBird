package com.android.angrybird.fragment;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.angrybird.R;
import com.android.angrybird.activity.UserListActivity;
import com.android.angrybird.database.Admin;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.databinding.FragmentRegisterPinBinding;
import com.android.angrybird.prefs.PreferenceUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterPinFragment extends Fragment {


    private FragmentRegisterPinBinding binding;

    public RegisterPinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_pin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAdmin();
            }
        });
    }

    private boolean validate()
    {
        if(TextUtils.isEmpty(binding.pinEt.getText().toString()))
        {
            Toast.makeText(getActivity(), "Pin is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(binding.pinEt.getText().toString().length() != 4)
        {
            Toast.makeText(getActivity(), "Pin must be of 4 digit", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!binding.confirmPinEt.getText().toString().equalsIgnoreCase(binding.pinEt.getText().toString()))
        {
            Toast.makeText(getActivity(), "Pin doesn't match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveAdmin() {
        if(validate()) {
            Admin admin = new Admin();
            admin.setPin(binding.confirmPinEt.getText().toString());
            DBManager.INSTANCE.getDaoSession().getAdminDao().insert(admin);
            PreferenceUtil.getInstance().setPrefKeyPinRegComplete(true);
            getActivity().startActivity(new Intent(getActivity(), UserListActivity.class));
            getActivity().finish();
        }
    }
}
