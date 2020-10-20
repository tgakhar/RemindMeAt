package com.example.remindmeat.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.remindmeat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser curUser;
    CircleImageView imageView;
    MaterialToolbar toolbar;
    FirebaseFirestore db;
    Button btn_dltProfile,btn_resetpass;
    TextInputLayout edt_email,edt_name;
    ImageView img_edtEmail;
    String  email,nEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        imageView=findViewById(R.id.img_profile);
        imageView.setOnClickListener(changeProfile);
        toolbar=findViewById(R.id.topAppBar_pro);
        edt_email=findViewById(R.id.edt_profileEmail);
        edt_name=findViewById(R.id.edt_profileName);
        btn_dltProfile=findViewById(R.id.btn_profileDelete);
        btn_resetpass=findViewById(R.id.btn_profileChangePass);
        img_edtEmail=findViewById(R.id.btn_editEmail);
        img_edtEmail.setOnClickListener(updateEmail);
        btn_dltProfile.setOnClickListener(DeleteProfile);
        btn_resetpass.setOnClickListener(resetPass);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProfileActivity.this,DashActivity.class);
                startActivity(intent);
            }
        });
        loadData();
    }

    private void loadData() {
        curUser=auth.getCurrentUser();
        if(curUser!=null){
            if(curUser.getPhotoUrl()!= null){
                Picasso.get().load(curUser.getPhotoUrl()).into(imageView);
            }
        }
        curUser=auth.getCurrentUser();
        db.collection("Users").document(curUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                edt_name.getEditText().setText(documentSnapshot.getString("Name"));
                edt_email.getEditText().setText(documentSnapshot.getString("Email"));
            }
        });


    }

    CircleImageView.OnClickListener changeProfile=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setTitle("Add Photo!");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if(options[item].equals("Take Photo")){
                        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(openCamera, 1000);
                    }else if(options[item].equals("Choose from Gallery")){
                        Intent openGalary=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(openGalary,1001);
                    }else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            switch (resultCode){
                case RESULT_OK:
                    Bitmap bitmap=(Bitmap)data.getExtras().get("data");
                    imageView.setImageBitmap(bitmap);
                    handleUpload(bitmap);
            }
        }else if(requestCode==1001){

            switch (resultCode){
                case RESULT_OK:
                    Uri imageUri=data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                        imageView.setImageBitmap(bitmap);
                        handleUpload(bitmap);
                    } catch (IOException e) {
                        Log.d("Image Gallary","exception"+e.getMessage());
                        e.printStackTrace();
                    }
            }
        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        curUser=auth.getCurrentUser();
        String uId=curUser.getUid();
        final StorageReference reference= FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(uId+".jpeg");
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDownloadUrl(reference);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Profile Fragment","onFailure",e.getCause());
            }
        });
    }

    private void getDownloadUrl(StorageReference reference){
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("Profile Fragment","onSuccess"+uri);
                        setUserProfile(uri);
                    }
                });
    }
    private void setUserProfile(Uri uri){
        UserProfileChangeRequest request=new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        curUser.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Updated Successfully",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Profile Update Unsuccessful",Toast.LENGTH_LONG).show();
            }
        });
    }

    View.OnClickListener DeleteProfile=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View popupview = getLayoutInflater().inflate(R.layout.popview_deleteprofile, null);
            final PopupWindow popupWindow = new PopupWindow(popupview, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            if (Build.VERSION.SDK_INT >= 21) {
                popupWindow.setElevation(5.0f);
            }
            final TextInputLayout edtPEmail = popupview.findViewById(R.id.edt_currentEmail);
            final TextInputLayout edtPPass = popupview.findViewById(R.id.edt_currentPass);
            Button btnSubmit = popupview.findViewById(R.id.btn_poplogin);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.main)));
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(edtPEmail.getEditText().getText().toString())) {
                        edtPEmail.setError("New Email cannot be blank!");
                        edtPEmail.requestFocus();
                    } else if (TextUtils.isEmpty(edtPPass.getEditText().getText().toString())) {
                        edtPPass.setError("Password cannot be blank!");
                        edtPPass.requestFocus();
                    } else {
                        if (edtPPass.getEditText().getText().toString().length() < 6) {
                            edtPPass.setError("Invalid password,Should be at least 6 characters");
                            edtPPass.requestFocus();
                        } else {
                            AuthCredential credential = EmailAuthProvider.getCredential(edtPEmail.getEditText().getText().toString(), edtPPass.getEditText().getText().toString());
                            curUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        db.collection("Users").document(curUser.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    curUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                                                startActivity(intent);
                                                                popupWindow.dismiss();
                                                            } else {
                                                                Log.d("Dashboard Fragment", "onComplete: UserDeleter" + task.getException().getMessage());
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });

                                    } else {
                                        Log.d("Setting Activity", "onFailure: Authentication" + task.getException().getMessage());
                                        edtPEmail.getEditText().getText().clear();
                                        edtPPass.getEditText().getText().clear();
                                        edtPEmail.setError("Please enter correct details");
                                    }
                                }
                            });
                        }
                    }
                }
            });


        }
    };

    View.OnClickListener resetPass= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            {
                email=edt_email.getEditText().getText().toString().trim();
                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Email sent successfully!",Toast.LENGTH_LONG).show();
                            auth.signOut();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }

        }
    };

    ImageView.OnClickListener updateEmail = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View popupview=getLayoutInflater().inflate(R.layout.layout_updateemail,null);
            final PopupWindow popupWindow=new PopupWindow(popupview, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
            if(Build.VERSION.SDK_INT>=21){
                popupWindow.setElevation(5.0f);



            }
            final TextInputLayout edtPEmail=popupview.findViewById(R.id.edt_emailOld);
            final TextInputLayout edtPPass=popupview.findViewById(R.id.edt_emailPass);
            final TextInputLayout edtNEmail=popupview.findViewById(R.id.edt_emailNew);
            Button btnupdate =popupview.findViewById(R.id.btn_poplogin);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.main)));
            popupWindow.showAtLocation(v, Gravity.CENTER,0,0);
            btnupdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.isEmpty(edtPEmail.getEditText().getText().toString())){
                        edtPEmail.setError(" New Email cannot be blank!");
                        edtPEmail.requestFocus();
                    }else if(TextUtils.isEmpty(edtNEmail.getEditText().getText().toString())) {
                        edtNEmail.setError(" Email cannot be blank!");
                        edtNEmail.requestFocus();
                    }else if(TextUtils.isEmpty(edtPPass.getEditText().getText().toString())) {
                        edtPPass.setError("Password cannot be blank!");
                        edtPPass.requestFocus();
                    }else{
                        if(edtPPass.getEditText().getText().toString().length()<6){
                            edtPPass.setError("Invalid password,Should be at least 6 characters");
                            edtPPass.requestFocus();
                        } else {
                            AuthCredential credential= EmailAuthProvider.getCredential(edtPEmail.getEditText().getText().toString(),edtPPass.getEditText().getText().toString());
                            curUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        nEmail=edtNEmail.getEditText().getText().toString();
                                        Log.d("Setting","email="+nEmail);
                                        curUser.updateEmail(nEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Log.d("Exception", "log Succ: ");
                                                    curUser=auth.getCurrentUser();
                                                    Map<String,Object> usermap=new HashMap<>();
                                                    usermap.put("Email",nEmail);
                                                    //int a=0;
                                                    //usermap.put("Level",a);
                                                    db.collection("Users").document(curUser.getUid()).update(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(getApplicationContext(),"Email Updated Successfully!",Toast.LENGTH_LONG).show();
                                                                popupWindow.dismiss();
                                                                loadData();
                                                                //Toast.makeText(getActivity().getApplicationContext(),"Please verify your Email address!",Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                }else{
                                                    try{
                                                        throw task.getException();
                                                    } catch (FirebaseAuthUserCollisionException already) {
                                                        Toast.makeText(getApplicationContext(),"Email Already Exist!",Toast.LENGTH_LONG).show();
                                                        edtNEmail.getEditText().getText().clear();
                                                        edtNEmail.requestFocus();
                                                        edtNEmail.setError("Enter Other Emil");
                                                        edtPEmail.getEditText().getText().clear();
                                                        edtPPass.getEditText().getText().clear();

                                                    }
                                                    catch (Exception e){
                                                        Log.d("Exception", "onComplete: " + e.getMessage());
                                                        Toast.makeText(getApplicationContext(),"Register Failed!",Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            }
                                        });
                                    }else {
                                        Log.d("Setting Activity","onFailure: Authentication"+task.getException().getMessage());
                                        edtPEmail.getEditText().getText().clear();
                                        edtPPass.getEditText().getText().clear();
                                        edtPEmail.setError("Please enter correct details");
                                    }
                                }
                            });
                        }
                    }
                }
            });

        }
    };

}