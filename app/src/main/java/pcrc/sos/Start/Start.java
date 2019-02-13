package pcrc.sos.Start;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pcrc.sos.R;
import pcrc.sos.ViewPagerAdapter.ViewPagerMain;

public class Start extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.start, container, false);

        TabLayout tabLayout = view.findViewById(R.id.start_tab);
        ViewPager viewPager = view.findViewById(R.id.start_viewpager);

        ViewPagerMain viewPagerMain = new ViewPagerMain(getFragmentManager());

        viewPagerMain.AddFragment(new SOS(), null);
        viewPagerMain.AddFragment(new Emergency_calls(), null);
        viewPagerMain.AddFragment(new Tracking(), null);
        viewPagerMain.AddFragment(new Help_centers(), null);

        viewPager.setAdapter(viewPagerMain);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_sos);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_emergencycalls);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_tracingfamily);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_helpcenters);

        return view;

    }

}
