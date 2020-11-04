package com.example.remindmeat.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.transition.TransitionInflater;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.remindmeat.R;

/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dhankara Chintan
 * @author Karthik Modubowna
 * Java class for Splash Screen activity {@link SplashFragment}
 */

public class SplashFragment extends Fragment {
    /**
     * Variable for animation
     */
    Animation topAnim,bottomAnim;
    /**
     * Variables for text views
     */
    TextView txt_title,txt_tag;
    /**
     * Variable for image view
     */
    ImageView img_main;
    /**
     * Constant int of 3000ms showing the time of the splash screen
     */
    private static int SPLASH_SCREEN=3000;
    public SplashFragment() {
        // Required empty public constructor
    }

    /**
     * onCreate Method
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * onViewCreated Method
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topAnim= AnimationUtils.loadAnimation(getActivity(), R.anim.top_animation);
        bottomAnim=AnimationUtils.loadAnimation(getActivity(),R.anim.bottom_animation);
        txt_title=view.findViewById(R.id.txt_title);
        img_main=view.findViewById(R.id.img_splashMain);
        txt_tag=view.findViewById(R.id.txt_tag);
        img_main.setAnimation(topAnim);
        txt_title.setAnimation(bottomAnim);
        txt_tag.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            /**
             * Run Method to show Splash Screen with Animations
              */
            @Override
            public void run() {
                NavController navController= Navigation.findNavController(getActivity(),R.id.nav_host_main);
                FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                        .addSharedElement(img_main, "logo_image")
                        .addSharedElement(txt_title, "logo_text")
                        .addSharedElement(txt_tag, "logo_tag")
                        .build();
                navController.navigate(R.id.loginFragment,null,null,extras);
            }
        },SPLASH_SCREEN);
    }

    /**
     * onViewCreated Method
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }
}