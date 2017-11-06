package com.jeremy.imagecompress;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


/**
 * Created by Jeremy on 2017/11/3.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int TARGET_WIDTH = 800;

    private static final int REQUEST_CODE_ALBUM = 0x01;

    /**
     * 选择图片路径
     */
    private String imgPath;

    /**
     * 图片
     */
    private ImageView imgResult;
    /**
     * 图片信息
     */
    private TextView tvImgInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgResult = (ImageView) findViewById(R.id.img_result);
        tvImgInfo = (TextView) findViewById(R.id.tv_img_info);

        findViewById(R.id.btn_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //选择图片
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_ALBUM);
            }
        });

        findViewById(R.id.btn_compress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(imgPath)) {
                    Log.e(TAG, "imgPath is null");
                    Toast.makeText(MainActivity.this, "图片路径为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bitmap bmpSelect = ImageUtils.decodeFileWithInSampleSize(imgPath, TARGET_WIDTH);

                String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + new File(imgPath).getName();
                int result = ImageUtils.compressBitmapWithNative(bmpSelect, savePath);
                Toast.makeText(MainActivity.this, result != -1 ? "压缩成功，路径：" + savePath : "压缩失败", Toast.LENGTH_LONG).show();

                if (!bmpSelect.isRecycled()) {
                    bmpSelect.recycle();
                }

                showImage(savePath);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_ALBUM) {
            handleImageSelect(data);
        }
    }

    /**
     * 处理所选图片
     *
     * @param data
     */
    private void handleImageSelect(Intent data) {
        Uri uri = data.getData();
        imgPath = uri.getPath();

        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumns, null, null, null);
        if (cursor == null) {
            Log.e(TAG, "cursor is null");
            return;
        }
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
        imgPath = cursor.getString(columnIndex);
        cursor.close();

        showImage(imgPath);
    }

    /**
     * 显示图片
     *
     * @param imgPath
     */
    private void showImage(String imgPath) {
        BitmapFactory.Options bmpOp = new BitmapFactory.Options();
        Bitmap bmpSelect;
        bmpSelect = BitmapFactory.decodeFile(imgPath, bmpOp);
        imgResult.setImageBitmap(bmpSelect);//图片太大，会无法显示，所以需要压缩
        showImageInfo(new File(imgPath), bmpOp);
    }

    /**
     * 显示图片信息
     *
     * @param file
     * @param bmpOp
     */
    public void showImageInfo(File file, BitmapFactory.Options bmpOp) {
        if (bmpOp == null) {
            return;
        }
        int bw = bmpOp.outWidth;
        int bh = bmpOp.outHeight;
        int mb = (int) (file.length() / 1024);
        tvImgInfo.setText("宽->" + bw + "\n高->" + bh + "\n大小->" + mb + "KB");

    }
}
