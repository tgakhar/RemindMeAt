package com.example.remindmeat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class SplashFragment extends Fragment {

    Animation topAnim,bottomAnim;
    TextView txt_title,txt_tag;
    ImageView img_main;
    public SplashFragment() {
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
        topAnim= AnimationUtils.loadAnimation(getActivity(),R.anim.top_animation);
        bottomAnim=AnimationUtils.loadAnimation(getActivity(),R.anim.bottom_animation);
        txt_title=view.findViewById(R.id.txt_title);
        img_main=view.findViewById(R.id.img_splashMain);
        txt_tag=view.findViewById(R.id.txt_tag);
        img_main.setAnimation(topAnim);
        txt_title.setAnimation(bottomAnim);
        txt_tag.setAnimation(bottomAnim);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }
}