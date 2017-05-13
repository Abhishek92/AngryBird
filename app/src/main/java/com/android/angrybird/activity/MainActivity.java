package com.android.angrybird.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.angrybird.R;
import com.android.angrybird.database.Admin;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.fragment.RegisterPinFragment;
import com.android.angrybird.fragment.ValidatePinFragment;
import com.android.angrybird.prefs.PreferenceUtil;
import com.android.angrybird.util.Utils;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_PERMISSION_STORAGE = 2001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean writePermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        boolean readPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        if (!(writePermission && readPermission)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_STORAGE);
        }
        else
        {
            initDatabase();
        }

    }

    private void openFragment(Fragment fragment)
    {
        if(!isFinishing()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commitAllowingStateLoss();
        }
    }

    private void initDatabase()
    {
        new AsyncTask<Void, Void, Void>()
        {
            private ProgressDialog mDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mDialog = new ProgressDialog(MainActivity.this);
                mDialog.setMessage("Please wait...");
                mDialog.setCancelable(false);
                mDialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                DBManager.INSTANCE.initDatabase(getApplicationContext());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mDialog.cancel();
                List<Admin> adminList = DBManager.INSTANCE.getDaoSession().getAdminDao().loadAll();
                Fragment fragment = PreferenceUtil.getInstance().isRegComplete() || Utils.listNotNull(adminList) ? ValidatePinFragment.getInstance(false) : new RegisterPinFragment();
                openFragment(fragment);
            }
        }.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_PERMISSION_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                    initDatabase();

                } else
                {
                    Toast.makeText(this, "Please consider granting it all permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
