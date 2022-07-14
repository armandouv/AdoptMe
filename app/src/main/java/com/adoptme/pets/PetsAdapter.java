package com.adoptme.pets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adoptme.databinding.ItemPetBinding;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Handles displayed posts on the timeline's RecyclerView.
 */
public class PetsAdapter extends RecyclerView.Adapter<PetsAdapter.ViewHolder> {
    private final Context mContext;
    private final List<Pet> mPets;
    private final OnItemClickListener mClickListener;

    public PetsAdapter(Context context, List<Pet> pets,
                       OnItemClickListener clickListener) {
        this.mContext = context;
        this.mPets = pets;
        this.mClickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPetBinding itemBinding = ItemPetBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pet post = mPets.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return mPets.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPetBinding mBinding;

        public ViewHolder(@NonNull ItemPetBinding binding) {
            super(binding.getRoot());

            this.mBinding = binding;
            itemView.setOnClickListener(v ->
                    mClickListener.onItemClick(itemView, getAdapterPosition()));
        }

        public void bind(Pet pet) {
            mBinding.itemName.setText(pet.getFormattedName());

            if (pet.getPhoto() != null)
                Glide.with(mContext)
                        .load(pet.getPhoto().getUrl())
                        .into(mBinding.itemPhoto);

            mBinding.itemType.setText(pet.getFormattedType());
        }
    }
}

