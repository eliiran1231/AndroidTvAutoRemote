package com.example.androidtvremote.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidtvremote.logic.MainMVP;
import com.example.androidtvremote.logic.MainPresenter;
import com.example.androidtvremote.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppsFragment extends Fragment {
    public final MainPresenter presenter = MainPresenter.getInstance((MainMVP.View)getActivity());
    public AppsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AppsFragment newInstance(String param1, String param2) {
        AppsFragment fragment = new AppsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_apps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView ry = view.findViewById(R.id.apps_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        ry.setLayoutManager(layoutManager);
        ry.setAdapter(presenter.getAppAdapter());
    }
}