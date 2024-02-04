package com.example.glamlooksapp.fragments.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.glamlooksapp.R;


public class AboutUFragment extends Fragment {


    Button btnWhatsapp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_u, container, false);

        initViews(view);
        playActions();

        return view;

    }

    private void initViews(View view) {
        btnWhatsapp = view.findViewById(R.id.WhatsappButton);
    }

    private void playActions() {
        btnWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowMessage("This feature is not supported yet");

            }

        });

    }

    private void ShowMessage(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }



}