package com.android.angrybird.fragment;


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
import com.android.angrybird.database.Admin;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.databinding.FragmentChangePasswordBinding;
import com.android.angrybird.util.Utils;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends Fragment {

    private FragmentChangePasswordBinding binding;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePassword();
            }
        });
    }

    private boolean validate() {
        if (TextUtils.isEmpty(binding.pinEt.getText().toString())) {
            Toast.makeText(getActivity(), "Pin is empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.pinEt.getText().toString().length() != 4) {
            Toast.makeText(getActivity(), "Pin must be of 4 digit", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!binding.confirmPinEt.getText().toString().equalsIgnoreCase(binding.pinEt.getText().toString())) {
            Toast.makeText(getActivity(), "Pin doesn't match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void savePassword() {
        if (validate()) {
            List<Admin> adminList = DBManager.INSTANCE.getDaoSession().getAdminDao().loadAll();
            if (Utils.listNotNull(adminList)) {
                adminList.get(0).setPin(binding.confirmPinEt.getText().toString());
            }
            DBManager.INSTANCE.getDaoSession().getAdminDao().updateInTx(adminList);
            getActivity().finish();
        }
    }
}
