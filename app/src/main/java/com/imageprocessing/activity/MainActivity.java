package com.imageprocessing.activity;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.imageprocessing.R;
import com.imageprocessing.appUtils.GalleryUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;

/**
 * Created by abhishekkumar on 12/07/17.
 */

public class MainActivity extends AppCompatActivity {
    private final String LOGTAG = "MainActivity";
    private static final int TYPE_CAMERA = 100;
    private static final int TYPE_GALLERY = 200;
    private static final int STORAGE_PERMISSIONS_GALLERY = 300;
    private static final int STORAGE_PERMISSIONS_CAMERA = 400;

    private Bitmap bitmap;
    private String selectedImagePath;
    private String selectedOutputPath;

    private static final String PHOTO_PATH = "ImageProcessing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    public void openDialogBox(View view) {
        openMedia();
    }

    private void openMedia() {
        final CharSequence[] items = {getString(R.string.camera), getString(R.string.gallery)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.Choose));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                int permissionCheck = PermissionChecker.checkCallingOrSelfPermission(MainActivity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    if (item == 0) {
                        startCameraActivity();
                    } else if (item == 1) {
                        openDocument();
                    }
                } else {
                    showPermission(item);
                }
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void startCameraActivity() {
        Intent photoPickerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                getOutputMediaFile());
        photoPickerIntent.putExtra("outputFormat",
                Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(
                Intent.createChooser(photoPickerIntent, getString(R.string.upload_picker_title)),
                TYPE_CAMERA);
    }

    private void openDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        startActivityForResult(intent, TYPE_GALLERY);
    }

    private void showPermission(final int imageType) {
        if (imageType == 0) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSIONS_CAMERA);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSIONS_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSIONS_GALLERY: {
                int permissionCheck = PermissionChecker.checkCallingOrSelfPermission(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    openDocument();
                } else {
                    Toast.makeText(this, getString(R.string.permission_denied_msg), Toast.LENGTH_SHORT).show();
                }
                return;
            }

            case STORAGE_PERMISSIONS_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                int permissionCheck = PermissionChecker.checkCallingOrSelfPermission(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    startCameraActivity();
                } else {
                    Toast.makeText(this, getString(R.string.permission_denied_msg), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_CANCELED:
                break;
            case RESULT_OK:
                if (requestCode == TYPE_CAMERA
                        || requestCode == TYPE_GALLERY) {
                    if (requestCode == TYPE_GALLERY) {
                        Uri selectedImageUri = data.getData();
                        final int takeFlags = data.getFlags()
                                & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        // Check for the freshest data.
                        if (selectedImageUri != null) {
                            getContentResolver().takePersistableUriPermission(
                                    selectedImageUri, takeFlags);
                            selectedImagePath = getPath(selectedImageUri);
                        }
                    } else {
                        selectedImagePath = selectedOutputPath;
                    }

                    if (stringIsNotEmpty(selectedImagePath)) {
                        // decode image size
                        BitmapFactory.Options o = new BitmapFactory.Options();
                        o.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(selectedImagePath, o);
                        // Find the correct scale value. It should be the power of
                        // 2.
                        int width_tmp = o.outWidth, height_tmp = o.outHeight;
                        Log.d("HomeActivity", "HomeActivity : image size : "
                                + width_tmp + " ; " + height_tmp);
                        final int MAX_SIZE = getResources().getDimensionPixelSize(
                                R.dimen.image_loader_post_width);
                        int scale = 1;
                        if (height_tmp > MAX_SIZE || width_tmp > MAX_SIZE) {
                            if (width_tmp > height_tmp) {
                                scale = Math.round((float) height_tmp
                                        / (float) MAX_SIZE);
                            } else {
                                scale = Math.round((float) width_tmp
                                        / (float) MAX_SIZE);
                            }
                        }
                        Log.d("HomeActivity", "HomeActivity : scaling image by factor : " + scale);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = scale;
                        bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
                        onPhotoTaken();
                        System.gc();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean stringIsNotEmpty(String string) {
        if (string != null && !string.equals("null")) {
            if (!string.trim().equals("")) {
                return true;
            }
        }
        return false;
    }

    private void onPhotoTaken() {
        Intent intent = new Intent(MainActivity.this, ImageProcessingActivity.class);
        intent.putExtra("selectedImagePath", selectedImagePath);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
        super.onDestroy();
    }

    private Uri getOutputMediaFile() {
        if (isSDCARDMounted()) {
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), PHOTO_PATH);
            // Create a storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("ImageProcessingActivity", getString(R.string.directory_create_fail));
                    return null;
                }
            }
            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile;
            selectedOutputPath = mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg";
            Log.d("ImageProcessingActivity", "selected camera path "
                    + selectedOutputPath);
            mediaFile = new File(selectedOutputPath);
            return Uri.fromFile(mediaFile);
        } else {
            return null;
        }
    }

    private boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    private String getPath(final Uri uri) {
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // ExternalStorageProvider
            if (GalleryUtils.isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (GalleryUtils.isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return GalleryUtils.getDataColumn(this, contentUri, null, null);
            }
            // MediaProvider
            else if (GalleryUtils.isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return GalleryUtils.getDataColumn(this, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return GalleryUtils.getDataColumn(this, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

}
