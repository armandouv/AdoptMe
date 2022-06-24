package com.adoptme.pets.preferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoptme.R;
import com.adoptme.databinding.FragmentPreferencesMenuBinding;
import com.woxthebox.draglistview.DragListView;

import java.util.List;

/**
 * A drag-and-drop menu where users can specify their pet preferences. Each attribute will be
 * assigned a priority that can be changed by dragging and dropping the attribute to change its
 * order.
 */
public class PreferencesMenuFragment extends Fragment {

    private static final List<PetAttribute> mAttributes = PetAttribute.getDefaultAttributes();
    protected FragmentPreferencesMenuBinding mBinding;
    protected PreferencesAdapter mAdapter;

    public PreferencesMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new PreferencesAdapter(mAttributes, R.id.attribute, true);
        mBinding.dragListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.dragListView.setAdapter(mAdapter, true);
        mBinding.dragListView.setCanDragHorizontally(false);
        mBinding.dragListView.setCanDragVertically(true);

        mBinding.dragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        mBinding.dragListView.setDragListListener(new DragListView.DragListListenerAdapter() {
            @Override
            public void onItemDragStarted(int position) {
                Toast.makeText(mBinding.dragListView.getContext(), "Start - position: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                if (fromPosition != toPosition) {
                    Toast.makeText(mBinding.dragListView.getContext(), "End - position: " + toPosition, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentPreferencesMenuBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }
}