package org.kamol.nefete.ui.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import org.kamol.nefete.BaseActivity;
import org.kamol.nefete.R;
import org.kamol.nefete.ui.fragment.MainFragment;
import org.kamol.nefete.ui.fragment.PagerFragment;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements ActionBar.TabListener {
  @Inject LocationManager locationManager;
  AppSectionsPagerAdapter mAppSectionsPagerAdapter;
  ViewPager mViewPager;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // After the super.onCreate call returns we are guaranteed our injections are available.

    setContentView(R.layout.activity_main);

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .add(android.R.id.content, MainFragment.newInstance())
          .commit();
    }

    // Create the adapter that will return a fragment for each of the three primary sections
    // of the app.
    mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

    // Set up the action bar.
    final ActionBar actionBar = getActionBar();

    // Specify that the Home/Up button should not be enabled, since there is no hierarchical
    // parent.
    actionBar.setHomeButtonEnabled(false);

    // Specify that we will be displaying tabs in the action bar.
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    // Set up the ViewPager, attaching the adapter and setting up a listener for when the
    // user swipes between sections.
    mViewPager = (ViewPager) findViewById(R.id.pager);
    mViewPager.setAdapter(mAppSectionsPagerAdapter);
    mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        // When swiping between different app sections, select the corresponding tab.
        // We can also use ActionBar.Tab#select() to do this if we have a reference to the
        // Tab.
        actionBar.setSelectedNavigationItem(position);
      }
    });

    // For each of the sections in the app, add a tab to the action bar.
    for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
      // Create a tab with text corresponding to the page title defined by the adapter.
      // Also specify this Activity object, which implements the TabListener interface, as the
      // listener for when this tab is selected.
      actionBar.addTab(
          actionBar.newTab()
              .setIcon(mAppSectionsPagerAdapter.getPageIcon(i))
              .setText(mAppSectionsPagerAdapter.getPageTitle(i))
              .setTabListener(this)
      );
    }
  }

  @Override
  public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

  @Override
  public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    // When the given tab is selected, switch to the corresponding page in the ViewPager.
    mViewPager.setCurrentItem(tab.getPosition());
  }

  @Override
  public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

  /**
   * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding
   * to one of the primary
   * sections of the app.
   */
  public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {
    public AppSectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return PagerFragment.newInstance();
        case 1:
          return MainFragment.newInstance();
        default:
          return MainFragment.newInstance();
      }
    }

    @Override
    public int getCount() {
      return 3;
    }

    public int getPageIcon(int position) {
      switch (position) {
        case 0:
          return android.R.drawable.ic_menu_search;
        case 1:
          return android.R.drawable.ic_menu_camera;
        case 2:
          return android.R.drawable.ic_menu_myplaces;
      }
      return 0;
    }
  }

}