package com.adoptme.pets.preferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoptme.R;
import com.adoptme.databinding.FragmentPreferencesMenuBinding;

import java.util.List;

/**
 * A drag-and-drop menu where users can specify their pet preferences. Each attribute will be
 * assigned a priority that can be changed by dragging and dropping the attribute to change its
 * order.
 */
public class PreferencesMenuFragment extends Fragment {

    public static final List<PetAttribute> sAttributes = PetAttribute.getDefaultAttributes();
    protected FragmentPreferencesMenuBinding mBinding;
    protected PreferencesAdapter mAdapter;

    public PreferencesMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new PreferencesAdapter(sAttributes, R.id.attribute, true);
        mBinding.dragListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.dragListView.setAdapter(mAdapter, true);
        mBinding.dragListView.setCanDragHorizontally(false);
        mBinding.dragListView.setCanDragVertically(true);

        mBinding.dragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentPreferencesMenuBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    public static void launch(FragmentManager fragmentManager) {
        PreferencesMenuFragment fragment = new PreferencesMenuFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}