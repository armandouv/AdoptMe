package com.adoptme.pets;

import android.util.Log;

import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Displays Pets posted by the logged in user. Pets will be sorted from the most to the least recent.
 * The preferences menu will be hidden and ranking will not be performed.
 */
public class MyPetsFragment extends PetsTimelineFragment {

    public MyPetsFragment() {
        setRankingActive(false);
    }

    @Override
    protected void populatePetsAndSort(boolean isRefreshing) {
        ParseQuery<Pet> query = ParseQuery.getQuery(Pet.class);
        query.include(Pet.KEY_USER);
        query.whereEqualTo(Pet.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(500);
        query.addDescendingOrder("createdAt");

        query.findInBackground((pets, e) -> {
            if (e != null) {
                Log.e(TAG, "Could not query pets", e);
                return;
            }

            if (isRefreshing) {
                mPets.clear();
                mBinding.swipeContainer.setRefreshing(false);
            }

            mPets.addAll(pets);
            mPetsAdapter.notifyDataSetChanged();
        });
    }
}