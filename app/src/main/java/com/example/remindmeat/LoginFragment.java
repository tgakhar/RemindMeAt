package com.example.remindmeat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {

    TextView txt_reg,txt_forgot;
    TextInputLayout txt_layEmail,txt_layPass;
    Button btn_login;
    String email,pass;
    FirebaseAuth auth;

    public LoginFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txt_reg=view.findViewById(R.id.txt_loginRegister);
        txt_forgot=view.findViewById(R.id.txt_loginForgot);
        auth=FirebaseAuth.getInstance();
        txt_layEmail=view.findViewById(R.id.edt_loginEmail);
        txt_layPass=view.findViewById(R.id.edt_loginPassword);
        btn_login=view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(login);
        txt_forgot.setOnClickListener(forgot);
        txt_reg.setOnClickListener(newUser);
    }
    View.OnClickListener forgot=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            NavController navController= Navigation.findNavController(getActivity(),R.id.nav_host_main);
            navController.navigate(R.id.forgotFragment);
        }
    };

    View.OnClickListener login=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            email=txt_layEmail.getEditText().getText().toString().trim();
            pass=txt_layPass.getEditText().getText().toString().trim();

            if (!checkEmptyField()){
                AuthCredential credential = EmailAuthProvider.getCredential(email,pass);

                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(getActivity().getApplicationContext(),"Login Success!",Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(getActivity(),DashActivity.class);
                            startActivity(intent);
                        }else {

                            Toast.makeText(getActivity().getApplicationContext(),"Login Unsuccessful!",Toast.LENGTH_LONG).show();

                        }
                    }
                });

            }

        }
    };


    View.OnClickListener newUser=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NavController navController= Navigation.findNavController(getActivity(),R.id.nav_host_main);
            navController.navigate(R.id.registrationFragment);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    public boolean checkEmptyField() {

        if (TextUtils.isEmpty(email)) {
            txt_layEmail.setError("Email Cannot be Empty!");
            txt_layEmail.requestFocus();
            return true;
        } else if (TextUtils.isEmpty(pass)) {
            txt_layEmail.setError(null);
            txt_layEmail.setErrorEnabled(false);
            txt_layPass.setError("Password Cannot be Empty!");
            txt_layPass.requestFocus();
            return true;
        }else if (pass.length()<6){
            txt_layPass.getEditText().getText().clear();
            txt_layPass.setError("Password Cannot less than 6 characters");
            txt_layPass.requestFocus();
            return true;
        }
        txt_layPass.setErrorEnabled(false);
        return false;
    }
}