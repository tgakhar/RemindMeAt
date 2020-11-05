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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindmeat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;


/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dhankara Chintan
 * @author Karthik Modubowna
 * Java class for ForgotFragment {@link forgotFragment}
 */
public class forgotFragment extends Fragment {

    /**
     * Variables of TextView
     */
    TextView txt_login,txt_title,txt_tag;

    /**
     * variables of material TextInputLayout
     */
    TextInputLayout txt_layEmail;
    /**
     * Variable of forgot button
     */
    Button btn_forgot;

    /**
     * Variable of String type for storing email.
     */
    String email;

    /**
     * Object of {@link FirebaseAuth}
     */
    FirebaseAuth auth;
    /**
     * Variable of LinearLayout
     */
    LinearLayout layout_bottom;

    /**
     * Variable of ImageView for logo
     */
    ImageView img_logo;

    /**
     * Default constructor
     */
    public forgotFragment() {
        // Required empty public constructor
    }


    /**
     * onCreate
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext())
                .inflateTransition(android.R.transition.move));

    }

    /**
     * onViewCreated
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txt_layEmail=view.findViewById(R.id.edt_forgotEmail);
        img_logo=view.findViewById(R.id.img_forgotLogo);
        txt_login=view.findViewById(R.id.txt_forgotLogin);
        btn_forgot=view.findViewById(R.id.btn_forgot);
        txt_title=view.findViewById(R.id.txt_forgotTitle);
        txt_tag=view.findViewById(R.id.txt_forgotHello);
        layout_bottom=view.findViewById(R.id.layout_forgotBottom);
        btn_forgot.setOnClickListener(forgot);
        txt_login.setOnClickListener(login);
    }


    /**
     * Click listener for forgot button for sending password reset link to user's email
     */
    View.OnClickListener forgot=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            email=txt_layEmail.getEditText().getText().toString().trim();

            if (!checkEmptyField()){
                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity().getApplicationContext(), "Password reset link sent successfully", Toast.LENGTH_SHORT).show();
                            NavController navController= Navigation.findNavController(getActivity(),R.id.nav_host_main);

                            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                                    .addSharedElement(img_logo, "logo_image")
                                    .addSharedElement(txt_title, "logo_text")
                                    .addSharedElement(txt_tag, "logo_tag")
                                    .addSharedElement(txt_layEmail, "logo_email")
                                    .addSharedElement(btn_forgot, "logo_btn")
                                    .addSharedElement(layout_bottom, "login_signup")
                                    .build();
                            navController.navigate(R.id.loginFragment,null,null,extras);
                        }
                    }
                });
            }

        }
    };


    /**
     * Click listener for navigation to login page
     */
    View.OnClickListener login=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            NavController navController= Navigation.findNavController(getActivity(),R.id.nav_host_main);
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(img_logo, "logo_image")
                    .addSharedElement(txt_title, "logo_text")
                    .addSharedElement(txt_tag, "logo_tag")
                    .addSharedElement(txt_layEmail, "logo_email")
                    .addSharedElement(btn_forgot, "logo_btn")
                    .addSharedElement(layout_bottom, "login_signup")
                    .build();
            navController.navigate(R.id.loginFragment,null,null,extras);
        }
    };


    /**
     * onCreateView
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot, container, false);
    }

    /**
     * method for checking validations
     * @return
     */
    public boolean checkEmptyField() {

        if (TextUtils.isEmpty(email)) {
            txt_layEmail.setError("Email Cannot be Empty!");
            txt_layEmail.requestFocus();
            return true;
        }
        txt_layEmail.setErrorEnabled(false);
        return false;
    }
}