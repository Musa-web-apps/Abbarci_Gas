package com.zoctu.abbarcigas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.zoctu.abbarcigas.Prevalent.Prevalent;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private EditText fullNameEditText,userPhoneEditText, addressEditText,passwordEditText;
    private TextView profileChangeTextBtn,closeTextBtn,saveTextButton;

    private Uri imageUri;
    private String myUrl= "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private  String checker= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureRef= FirebaseStorage.getInstance().getReference().child("Profile pictures");


        profileImageView=findViewById(R.id.setting_profile_image);
        fullNameEditText=findViewById(R.id.settings_full_name);
        userPhoneEditText=findViewById(R.id.settings_phone_number);
        addressEditText=findViewById(R.id.settings_address);
        profileChangeTextBtn=findViewById(R.id.profile_image_change);
        closeTextBtn=findViewById(R.id.close_settings_btn);
        saveTextButton=findViewById(R.id.update_account_settings_btn);
        passwordEditText=findViewById(R.id.settings_password);



        userInfoDisplay(profileImageView,fullNameEditText,userPhoneEditText,addressEditText);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,MainActivity.class));
                finish();
            }
        });


        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")){
                    userInfoSaved();
                }
                else {
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                checker="clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });

    }

    private void updateOnlyUserInfo()
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", fullNameEditText.getText().toString());
        userMap.put("address", addressEditText.getText().toString());
        userMap.put("phoneOrder", userPhoneEditText.getText().toString());
        userMap.put("password", passwordEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUsers.getPhone()).updateChildren(userMap);


        startActivity(new Intent(SettingsActivity.this,MainActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile Info Updated Successfully....", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && requestCode==RESULT_OK && data!=null)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();

            profileImageView.setImageURI(imageUri);
        }
        else {
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
            Toast.makeText(this, "Error, Try Again....", Toast.LENGTH_SHORT).show();


        }
    }


    private void userInfoSaved() {

        if (TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            Toast.makeText(this, "Name is Mandatory....", Toast.LENGTH_SHORT).show();
        }
        else
        if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Address is Mandatory....", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Phone is Mandatory....", Toast.LENGTH_SHORT).show();
        }
        else if (checker.equals("clicked"))
        {
            uploadImage();
        }
    }

    private void uploadImage()
    {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        if (imageUri !=null)
        {
            final StorageReference fileRef=storageProfilePictureRef
                    .child(Prevalent.currentOnlineUsers.getPhone() + ".jpeg/png");

            uploadTask=fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful()){
                        Uri downloadUrl=task.getResult();
                        myUrl=downloadUrl.toString();

                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", fullNameEditText.getText().toString());
                        userMap.put("address", addressEditText.getText().toString());
                        userMap.put("phoneOrder", userPhoneEditText.getText().toString());
                        userMap.put("password", passwordEditText.getText().toString());
                        userMap.put("image", myUrl);
                        ref.child(Prevalent.currentOnlineUsers.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(SettingsActivity.this,MainActivity.class));
                        Toast.makeText(SettingsActivity.this, "Profile Info Updated Successfully....", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    else 
                    {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(this, "Image is not Selected....", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditext, final EditText userPhoneEditText, final EditText addressEditText)
    {
        DatabaseReference UsersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUsers.getPhone());

       UsersRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot)
           {
               if (dataSnapshot.exists())
               {
                   if (dataSnapshot.child("image").exists())
                   {
                       String image=dataSnapshot.child("image").getValue().toString();
                       String name=dataSnapshot.child("name").getValue().toString();
                       String phone=dataSnapshot.child("phone").getValue().toString();
                       String address=dataSnapshot.child("address").getValue().toString();
                       String password=dataSnapshot.child("password").getValue().toString();


                       Picasso.get().load(image).into(profileImageView);
                       fullNameEditext.setText(name);
                       userPhoneEditText.setText(phone);
                       addressEditText.setText(address);
                       passwordEditText.setText(password);

                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(SettingsActivity.this,HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        }
}