package com.neko.quileiamedic.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.neko.quileiamedic.R;
import com.neko.quileiamedic.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private Button doctorButton;
    private Button patientButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        doctorButton = root.findViewById(R.id.doctorHomeButton);
        patientButton = root.findViewById(R.id.patientHomeButton);

        doctorButton.setOnClickListener(this);
        patientButton.setOnClickListener(this);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.doctorHomeButton:
                Navigation.findNavController(v).navigate(R.id.doctorFragment);
                break;

            case R.id.patientHomeButton:
                Navigation.findNavController(v).navigate(R.id.patientFragment);
                break;
        }
    }
}