package com.example.uploadimagetoserver;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //psfs
    public static final String TAG = MainActivity.class.getName();  //TAG = tên của class

    private static final int MY_REQUEST_CODE = 30;
    private EditText edtUserName, edtPassword;
    private ImageView imgFromGallery, imgFromAPI;       //ImageView là cha của CircleImageView nên ta để là ImageView vẫn được
    private Button btnSelectImage, btnUploadServer;
    private TextView tvUserName, tvPassword;

    //Uri mUri;

    //sử dụng registerForActivityResult() thay cho startActivityForResult()
    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //
                    Log.e(TAG,"onActivityResult");
                    //
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (data == null){
                            return;
                        }
                        //Dữ liệu của ảnh chọn từ gallery
                        Uri uri = data.getData();
                        //mUri = uri;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imgFromGallery.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();
        
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });
    }
    private void initUi(){
        edtUserName = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        imgFromGallery = findViewById(R.id.img_from_gallery);
        imgFromAPI = findViewById(R.id.img_from_api);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnUploadServer = findViewById(R.id.btn_select_upload_image);
        tvPassword = findViewById(R.id.tv_password);
        tvUserName = findViewById(R.id.tv_user_name);
    }

    //click vào button lấy ảnh từ gallery lên app
    private void onClickRequestPermission() {
        //check permission
        //Nếu như version của nó nhỏ hơn 23 thì sẽ không dùng được chức năng gallery
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }
        //Chỉ từ android 6 trở lên mới thực hiện request permission runtime
        //Nếu phiên bản nó bằng nhau, thì xin cấp phép thành công truy cập thư viện, tức có quyền truy cập
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            //nếu cho phép rồi thì mở gallery
            openGallery();
            return;
        }
        //nếu chưa có quền thì xin quyền
        else {
            //Một mảng permission
            String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            //xin cấp phép gallery
            requestPermissions(permission, MY_REQUEST_CODE);
            //lắng nghe xem có cấp quyền ko
        }
    }

    //lắng nghe permission người dùng từ chối hay cho phép quyền gallery
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Nếu requestCode đúng là của Gallery
        if (requestCode == MY_REQUEST_CODE){
            //khi đồng ý cho phép permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }

    }

    //Mở gallery
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //new
        mActivityResultLauncher.launch(Intent.createChooser(intent,"Select Picture"));

        //Code cũ là ta sử dụng startActivityForResult()
        //nhưng nó đã bị deprecated use, android studio khuyên bạn nên sử dụng thằng khác
        //thằng khác là registerForActivityResult
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),1);
    }
}