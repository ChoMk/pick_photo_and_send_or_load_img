package com.mksoft.imageload;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector {
    Button select_img_button;
    Button send_img_to_server;
    Button load_img_from_server;
    ImageView from_server_img;
    ImageView selected_img;

    private Boolean isPermission = true;

    private static final int PICK_FROM_ALBUM = 1;

    Uri photoUri;
    File tempFile;
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    @Inject
    APIRepo apiRepo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.configureDagger();
        init();
    }
    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
    private void configureDagger(){
        AndroidInjection.inject(this);
    }


    private void init(){
        tedPermission();
        select_img_button = findViewById(R.id.select_img_button);
        send_img_to_server = findViewById(R.id.send_img_to_server);
        load_img_from_server = findViewById(R.id.load_img_from_server);

        select_img_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPermission){
                    goToAlbum();
                }else{
                    Toast.makeText(getApplicationContext(), "권한을 먼저 승인해 주세여.", Toast.LENGTH_LONG).show();
                }

            }
        });

        send_img_to_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("test0605", photoUri.toString());
                //apiRepo.sendFile(MultipartBody.Part.createFormData("file", photoUri.encode("test.png")));
                apiRepo.sendFile(MultipartBody.Part.createFormData("file", tempFile.getPath(), RequestBody.create(MediaType.parse("image/*"), tempFile)));
                //apiRepo.sendFile(RequestBody.create(MediaType.parse("image/*"), tempFile));
            }
        });

        from_server_img = findViewById(R.id.from_server_img);
        selected_img = findViewById(R.id.selected_img);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            if(tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {

                        tempFile = null;
                    }
                }
            }
            return;
        }
        if (requestCode == PICK_FROM_ALBUM) {
            if(data == null)
                return;
            photoUri = data.getData();
            Glide.with(this).load(photoUri).into(selected_img);
            Cursor cursor = null;

            try {

                /*
                 *  Uri 스키마를
                 *  content:/// 에서 file:/// 로  변경한다.
                 */
                String[] proj = { MediaStore.Images.Media.DATA };

                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                tempFile = new File(cursor.getString(column_index));

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }


        }
    }

    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                isPermission = true;

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
                isPermission = false;

            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한, 쓰기 권한 필요해요.")
                .setDeniedMessage("권한 거부하셨습니다. 설정에가서 변경해주세요.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }
}
