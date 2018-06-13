package simple.live.abcpen.com.livesdk;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

/**
 * Created by zhaocheng on 2018/6/13.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText etName, etUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ac_login);
        etName = findViewById(R.id.et_uname);
        etUid = findViewById(R.id.et_uid);

        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {

                    }
                });
    }

    public void login(View view) {

        App.uid = etName.getText().toString();
        App.uname = etUid.getText().toString();
        startActivity(new Intent(this, MainActivity.class));

    }
}
