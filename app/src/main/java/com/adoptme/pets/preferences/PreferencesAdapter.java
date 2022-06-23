package com.adoptme.pets.preferences;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.adoptme.databinding.ItemPetAttributeBinding;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.List;

/**
 * Drag-and-drop to reorder adapter. Manages the menu preferences manipulating the PetAttributes
 * in the list.
 */
public class PreferencesAdapter extends DragItemAdapter<PetAttribute, DragItemAdapter.ViewHolder> {
    private final int mGrabHandleId;
    private final boolean mDragOnLongPress;

    public PreferencesAdapter(List<PetAttribute> attributes, int grabHandleId,
                              boolean dragOnLongPress) {
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        setItemList(attributes);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPetAttributeBinding itemBinding = ItemPetAttributeBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding.getRoot(), itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DragItemAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        PetAttribute attribute = mItemList.get(position);
        ((PreferencesAdapter.ViewHolder) holder).bind(attribute);
    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).getName().hashCode();
    }

    public class ViewHolder extends DragItemAdapter.ViewHolder {
        private final ItemPetAttributeBinding mBinding;

        public ViewHolder(View itemView, ItemPetAttributeBinding binding) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            this.mBinding = binding;
        }

        public void bind(PetAttribute attribute) {
            mBinding.attribute.setHint(attribute.getName());
        }
    }
}
