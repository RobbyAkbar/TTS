package edu.upi.ttsGisel.ui.dashboard;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import edu.upi.ttsGisel.R;
import edu.upi.ttsGisel.utils.Config;

import static edu.upi.ttsGisel.ui.dashboard.StatusFragment.newInstance;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;

    StatusPagerAdapter(Context context, FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
    }

    @StringRes
    private final int[] TAB_TITLES = new int[]{
            R.string.beginner,
            R.string.intermediate,
            R.string.advanced,
            R.string.expert
    };

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = newInstance();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(StatusFragment.ARG_OBJECT, position + 1);
        switch (position){
            case 0:
                args.putString(Config.LEVEL, Config.BEGINNER);
                break;
            case 1:
                args.putString(Config.LEVEL, Config.INTERMEDIATE);
                break;
            case 2:
                args.putString(Config.LEVEL, Config.ADVANCED);
                break;
            case 3:
                args.putString(Config.LEVEL, Config.EXPERT);
                break;
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 4;
    }
}
