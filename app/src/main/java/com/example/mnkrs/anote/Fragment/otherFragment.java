package com.example.mnkrs.anote.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mnkrs.anote.Activity.LoginActivity;
import com.example.mnkrs.anote.Activity.MainActivity;
import com.example.mnkrs.anote.R;

/**
 * Created by piaol on 2017/4/22 0022.
 */

public class otherFragment extends Fragment {
    private Button LogoutButton;
    public static otherFragment newInstance(int sectionNumber) {
        otherFragment fragment = new otherFragment();
        //??

        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_other, container, false);
        LogoutButton = (Button) rootView.findViewById(R.id.logoutButton);
        LogoutButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intentToLogin = new Intent(getActivity(),LoginActivity.class);
                startActivity(intentToLogin);                ;
            }
        });
        return rootView;
    }
}
