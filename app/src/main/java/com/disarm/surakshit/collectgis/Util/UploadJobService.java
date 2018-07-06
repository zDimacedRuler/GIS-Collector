package com.disarm.surakshit.collectgis.Util;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.disarm.surakshit.collectgis.Model.FileUploadModel;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.snatik.storage.Storage;

import org.apache.commons.io.FilenameUtils;

import java.io.File;


/**
 * Created by AmanKumar on 6/19/2018.
 */

public class UploadJobService extends JobService {
    private StorageReference mStorageRef;
    private FirebaseFirestore firestore;
    private String phoneNumber;
    public static final String FILES_CONST = "Kml_Files";
    public static final String IMAGES_CONST = "Images";

    @Override
    public boolean onStartJob(JobParameters job) {
        //fireBase changes
        mStorageRef = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        phoneNumber = preferences.getString(Constants.PHONE_NO, "null");
        Log.d("Upload Job", "Im here");
        new Thread(new Runnable() {
            @Override
            public void run() {
                uploadFiles();
            }
        }).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    private void uploadFiles() {
        File kmlDir = Environment.getExternalStoragePublicDirectory(Constants.CMS_TEMP_KML);
        if (kmlDir.listFiles().length > 0) {
            //there are files in tempKML directory that need to be uploaded
            for (File file : kmlDir.listFiles()) {
                //files not uploaded
                Log.d("Not Uploaded", "file name" + file.getName());
                saveToFirebase(file.getName());
            }
        }
    }

    private void saveToFirebase(final String file_name) {
        File fileToUpload = Environment.getExternalStoragePublicDirectory(Constants.CMS_TEMP_KML + file_name);
        final String fileBase = FilenameUtils.removeExtension(file_name);
        final File imageToUpload = Environment.getExternalStoragePublicDirectory(Constants.CMS_IMAGES + fileBase + Constants.JPEG_EXTENSION);
        StorageReference fileRef = mStorageRef.child(FILES_CONST).child(fileToUpload.getName());
        if (fileToUpload.exists()) {
            Uri fileUri = Uri.fromFile(fileToUpload);
            UploadTask uploadTask = fileRef.putFile(fileUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("Upload Test", "Upload Successful");
                    //update in firestore
                    FileUploadModel fileUploadModal;
                    if (imageToUpload.exists()) {
                        //upload image file to fireBase
                        fileUploadModal = new FileUploadModel(phoneNumber, file_name, true);
                        StorageReference imageRef = mStorageRef.child(IMAGES_CONST).child(fileBase + Constants.JPEG_EXTENSION);
                        Uri imageUri = Uri.fromFile(imageToUpload);
                        UploadTask imageUploadTask = imageRef.putFile(imageUri);
                        imageUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d("Upload Test", "Image Upload Successful");
                            }
                        });
                    } else
                        fileUploadModal = new FileUploadModel(phoneNumber, file_name, false);
                    firestore.collection(FILES_CONST).add(fileUploadModal);
                    //delete saved file from tmpKML
                    File tempFile = Environment.getExternalStoragePublicDirectory(Constants.CMS_TEMP_KML + file_name);
                    Storage storage = new Storage(getApplicationContext());
                    storage.deleteFile(tempFile.getAbsolutePath());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Upload Test", "Upload Failed:" + e.getMessage());
                }
            });
        } else
            Log.d("Upload Test", "No File to upload");
    }
}
