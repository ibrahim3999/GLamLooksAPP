package com.example.glamlooksapp.fragments.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.glamlooksapp.R;


public class HomeFragment extends Fragment {



    ImageView hair_cut , hair_color, nails, laser, shaving;

    public HomeFragment() {
        // Required empty public constructor
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_c, container, false);
        findViews(view);
//        initVars();
        return view;

    }



    private void findViews(View view) {
        hair_cut = view.findViewById(R.id.haircut);
        hair_color = view.findViewById(R.id.haircolor);
        laser = view.findViewById(R.id.laser);
        shaving = view.findViewById(R.id.shaving);
        nails = view.findViewById(R.id.nails);

    }

//    private void initVars() {
//    }


}