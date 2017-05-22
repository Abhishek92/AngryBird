package com.android.angrybird.fragment;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.angrybird.R;
import com.android.angrybird.activity.UserListActivity;
import com.android.angrybird.database.Admin;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.databinding.FragmentValidatePinBinding;
import com.android.angrybird.util.Utils;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ValidatePinFragment extends Fragment {


    public static final String KEY_FROM_AUTH = "KEY_FROM_AUTH";
    private FragmentValidatePinBinding binding;
    private boolean isFromAuth;

    public ValidatePinFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance(boolean fromAuth)
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_FROM_AUTH, fromAuth);
        ValidatePinFragment fragment = new ValidatePinFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_validate_pin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        isFromAuth = getArguments().getBoolean(KEY_FROM_AUTH);
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPinAndRedirect();
            }
        });
    }

    private void checkPinAndRedirect() {
        List<Admin> adminList = DBManager.INSTANCE.getDaoSession().getAdminDao().loadAll();
        if(Utils.listNotNull(adminList)) {
            Admin admin = adminList.get(0);
            if (binding.pinEntrySimple.getText().toString().equalsIgnoreCase(admin.getPin())) {
                if(!isFromAuth)
                    getActivity().startActivity(new Intent(getActivity(), UserListActivity.class));
                getActivity().finish();
            }
            else
            {
                Toast.makeText(getActivity(), "Pin not valid", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
