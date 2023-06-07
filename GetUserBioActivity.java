// Catherine
package com.idk.feetinder;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OptionalDataException;
import java.util.UUID;
import android.graphics.Bitmap;
import com.idk.feetinder.ml.Ftdetect;
import java.nio.ByteBuffer;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

public class GetUserBioActivity extends AppCompatActivity {

    private Button finishButton;
    private EditText enterName;
    private EditText enterBio;
    private Button back;
    private ImageView profilePic;
    private Uri imageUri;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    String userId;

    private boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_bio);
        getSupportActionBar().hide();

        finishButton = findViewById(R.id.finish);
        enterName = findViewById(R.id.enter_name);
        enterBio = findViewById(R.id.enter_bio);
        back = findViewById(R.id.back);
        profilePic = findViewById(R.id.profile_picture);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        userId = auth.getCurrentUser().getUid();

        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference();

        currentUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String existingName = (String) snapshot.child("Users").child(userId).child("Name").getValue();
                String existingBio = (String) snapshot.child("Users").child(userId).child("Bio").getValue();
                String existingPfp = (String) snapshot.child("Users").child(userId).child("ProfilePicture").getValue();

                if(existingName != null){
                    enterName.setText(existingName);
                    isFirstTime = false;
                } else {
                    back.setVisibility(View.INVISIBLE);
                }

                if(existingBio != null){
                    enterBio.setText(existingBio);
                }

                if(existingPfp != null){
                    Glide.with(getApplication()).load(existingPfp).into(profilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GetUserBioActivity.this, "read failed: " + error.getCode(), Toast.LENGTH_SHORT);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sounds.play(GetUserBioActivity.this, R.raw.normal_button_tap);
                finish();
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = enterName.getText().toString();
                String bio = enterBio.getText().toString();

                if(isValidNameBio(name, bio)){
                    DatabaseReference currentUserName = FirebaseDatabase.getInstance().getReference()
                            .child("Users").child(userId).child("Name");

                    currentUserName.setValue(name);

                    DatabaseReference currentUserBio = FirebaseDatabase.getInstance().getReference()
                            .child("Users").child(userId).child("Bio");

                    currentUserBio.setValue(bio);
                    Sounds.play(GetUserBioActivity.this, R.raw.continue_save_button);

                    if(!isFirstTime){
                        finish();
                    } else {
                        Intent intent = new Intent(GetUserBioActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Sounds.play(GetUserBioActivity.this, R.raw.alert_error);
                }
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sounds.play(GetUserBioActivity.this, R.raw.normal_button_tap);
                selectPicture();
            }
        });
    }

    private void uploadPicture() throws IOException {
        StorageReference path = storageReference.child("profilePics").child(userId);
        Bitmap bitmap = null;
        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), imageUri);

      /*  public void FootDetect(Bitmap bitmap){
            try {
                Ftdetect model = Ftdetect.newInstance(GetUserBioActivity.this);

                TensorBuffer image = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.UINT8);
                bitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true);
                image.loadBuffer(TensorImage.fromBitmap(bitmap).getBuffer());

                // Runs model inference and gets result.
                Ftdetect.Outputs outputs = model.process(image);
                TensorBuffer location = outputs.getLocationAsTensorBuffer();
                TensorBuffer score = outputs.getScoreAsTensorBuffer();

                Integer res = (Integer) (getMax(score.getFloatArray()));
                if (res !=

                // Releases model resources if no longer used.
                model.close();
            } catch (IOException e) {
                //To-do
            }
        } */

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = path.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GetUserBioActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUri = uri;

                        DatabaseReference currentUserPfp = FirebaseDatabase.getInstance().getReference()
                                .child("Users").child(userId).child("ProfilePicture");
                        currentUserPfp.setValue(downloadUri.toString());
                        Toast.makeText(GetUserBioActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private int getMax(float[] arr) {
        float max = arr[0];
        int ind = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                ind = i;
                max = arr[i];
            }
        }
        return ind;
    }

    private boolean isValidNameBio(String name, String bio) {
        if(name.matches(("^.*[^a-zA-Z0-9 ].*$"))){
            Toast.makeText(GetUserBioActivity.this, "invalid characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(name.length() < 1){
            Toast.makeText(GetUserBioActivity.this, "enter a name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(name.length() > 100){
            Toast.makeText(GetUserBioActivity.this, "name too long!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(bio.length() > 500){
            Toast.makeText(GetUserBioActivity.this, "bio too long!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void selectPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        imageUri = result.getData().getData();
                        profilePic.setImageURI(imageUri);
                        try {
                            uploadPicture();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

}