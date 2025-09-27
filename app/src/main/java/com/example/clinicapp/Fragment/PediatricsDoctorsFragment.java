package com.example.clinicapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.clinicapp.Adapter.DoctorAdapter;
import com.example.clinicapp.Database.MyDataBase;
import com.example.clinicapp.Model.Doctor;
import com.example.clinicapp.databinding.FragmentPediatricsDoctorsBinding;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PediatricsDoctorsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PediatricsDoctorsFragment extends Fragment {

    FragmentPediatricsDoctorsBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PediatricsDoctorsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PediatricsDoctorsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PediatricsDoctorsFragment newInstance(String param1, String param2) {
        PediatricsDoctorsFragment fragment = new PediatricsDoctorsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPediatricsDoctorsBinding.inflate(inflater,container,false);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        MyDataBase db = MyDataBase.getDatabase(requireContext());
        List<Doctor> doctors = db.doctorDao().getDoctorsBySpecialty("Pediatrics");

        DoctorAdapter adapter = new DoctorAdapter(doctors, doctor -> {
        });
        binding.recyclerView.setAdapter(adapter);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}