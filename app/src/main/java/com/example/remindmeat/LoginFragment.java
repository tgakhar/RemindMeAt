package com.example.remindmeat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


public class LoginFragment extends Fragment {

    TextView txt_reg,txt_forgot;

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
}