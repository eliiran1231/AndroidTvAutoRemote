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


public class AutoFragment extends Fragment {
    public final MainPresenter presenter = MainPresenter.getInstance((MainMVP.View)getActivity());
    public AutoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auto, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView ry = view.findViewById(R.id.auto_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        ry.setLayoutManager(layoutManager);
        ry.setAdapter(presenter.getTaskAdapter());
    }
}