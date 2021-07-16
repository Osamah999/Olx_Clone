package com.example.forsaleApp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.forsaleApp.Fragments.FavoritesFragment;
import com.example.forsaleApp.Fragments.ProductsFragment;

import org.jetbrains.annotations.NotNull;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {

        switch (position)
        {
            case 1 :
                return new FavoritesFragment();
        }

        return new ProductsFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
