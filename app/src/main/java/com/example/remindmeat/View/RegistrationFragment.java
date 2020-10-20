package com.example.remindmeat.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.transition.TransitionInflater;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindmeat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegistrationFragment extends Fragment {
    TextView txt_login,txt_title,txt_tag;
    Button btn_register;
    TextInputLayout txt_layEmail,txt_layPass,txt_layName,txt_layCPass;
    FirebaseAuth auth;
    FirebaseFirestore db;
    String name,email,pass,cPass;
    LinearLayout layout_bottom;
    ImageView img_logo;
    public RegistrationFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setSharedElementEnterTransition(TransitionInflater.from(getContext())
                .inflateTransition(android.R.transition.move));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        txt_login=view.findViewById(R.id.txt_registerLogin);
        txt_title=view.findViewById(R.id.txt_registerTitle);
        txt_tag=view.findViewById(R.id.txt_registerHello);
        img_logo=view.findViewById(R.id.img_registerLogo);
        txt_layEmail=view.findViewById(R.id.edt_registerEmail);
        txt_layPass=view.findViewById(R.id.edt_registerPass);
        txt_layCPass=view.findViewById(R.id.edt_registerCPass);
        txt_layName=view.findViewById(R.id.edt_registerName);
        btn_register=view.findViewById(R.id.btn_register);
        layout_bottom=view.findViewById(R.id.layout_registerBottom);
        txt_login.setOnClickListener(oldUser);
        btn_register.setOnClickListener(registerUser);
    }


    View.OnClickListener oldUser=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NavController navController= Navigation.findNavController(getActivity(),R.id.nav_host_main);
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(img_logo, "logo_image")
                    .addSharedElement(txt_title, "logo_text")
                    .addSharedElement(txt_tag, "logo_tag")
                    .addSharedElement(txt_layEmail, "logo_email")
                    .addSharedElement(txt_layPass, "logo_pass")
                    .addSharedElement(btn_register, "logo_btn")
                    .addSharedElement(layout_bottom, "login_signup")
                    .build();

            navController.navigate(R.id.loginFragment,null,null,extras);
        }
    };


    View.OnClickListener registerUser=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            name=txt_layName.getEditText().getText().toString().trim();
            email=txt_layEmail.getEditText().getText().toString().trim();
            pass=txt_layPass.getEditText().getText().toString().trim();
            cPass=txt_layCPass.getEditText().getText().toString().trim();



            final Map<String,Object> usermap=new HashMap<>();
            usermap.put("Name",name);
            usermap.put("Email",email);
            usermap.put("Disabled",0);
            if (!checkEmptyField()){
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            db.collection("Users").document(user.getUid()).set(usermap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Successfully Registered!", Toast.LENGTH_LONG).show();
                                    NavController navController= Navigation.findNavController(getActivity(),R.id.nav_host_main);
                                    FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                                            .addSharedElement(img_logo, "logo_image")
                                            .addSharedElement(txt_title, "logo_text")
                                            .addSharedElement(txt_tag, "logo_tag")
                                            .addSharedElement(txt_layEmail, "logo_email")
                                            .addSharedElement(txt_layPass, "logo_pass")
                                            .addSharedElement(btn_register, "logo_btn")
                                            .addSharedElement(layout_bottom, "login_signup")
                                            .build();

                                    navController.navigate(R.id.loginFragment,null,null,extras);
                                }
                            });
                        }else{
                            try {
                                throw  task.getException();
                            }catch (FirebaseAuthUserCollisionException already) {
                                Toast.makeText(getActivity().getApplicationContext(),"User Already Exist!Please login",Toast.LENGTH_LONG).show();
                                NavController navController=Navigation.findNavController(getActivity(),R.id.nav_host_main);
                                navController.navigate(R.id.loginFragment);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }else{
                return;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    public boolean checkEmptyField() {

        if (TextUtils.isEmpty(name)) {
            txt_layName.setError("Name Cannot be Empty!");
            txt_layName.requestFocus();
            return true;
        }else if (TextUtils.isEmpty(email)) {
            txt_layName.setError(null);
            txt_layName.setErrorEnabled(false);
            txt_layEmail.setError("Email Cannot be Empty!");
            txt_layEmail.requestFocus();
            return true;
        } else if (TextUtils.isEmpty(pass)) {
            txt_layEmail.setError(null);
            txt_layEmail.setErrorEnabled(false);
            txt_layPass.setError("Password Cannot be Empty!");
            txt_layPass.requestFocus();
            return true;
        }else if (TextUtils.isEmpty(cPass)){
            txt_layPass.setError(null);
            txt_layPass.setErrorEnabled(false);
            txt_layCPass.setError("Confirm Password Cannot be Empty!");
            txt_layCPass.requestFocus();
            return true;
        }else if (pass.length()<6){
            txt_layPass.getEditText().getText().clear();
            txt_layPass.setError("Password Cannot less than 6 characters");
            txt_layPass.requestFocus();
            return true;
        }else if (!pass.equals(cPass)){
            txt_layPass.getEditText().getText().clear();
            txt_layCPass.getEditText().getText().clear();
            txt_layPass.setError("Password not matched");
            txt_layPass.requestFocus();
            return true;
        }
        txt_layPass.setErrorEnabled(false);
        txt_layCPass.setErrorEnabled(false);
        return false;
    }


}