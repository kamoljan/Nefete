package org.kamol.nefete.ui.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.squareup.otto.Bus;
import com.squareup.otto.Produce;

import org.kamol.nefete.BaseActivity;
import org.kamol.nefete.R;
import org.kamol.nefete.event.ActivityResultEvent;
import org.kamol.nefete.ui.fragment.InsertAdContainerFragment;
import org.kamol.nefete.ui.fragment.ListingFragment;
import org.kamol.nefete.ui.fragment.MainFragment;
import org.kamol.nefete.ui.fragment.ProfileFragment;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements ActionBar.TabListener {
  AppSectionsPagerAdapter mAppSectionsPagerAdapter;
  ViewPager mViewPager;
  @Inject Bus bus;
  private int mRequestCode;
  private int mResultCode;
  private Intent mData;

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

    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayShowHomeEnabled(false);
    // Set up the ViewPager, attaching the adapter and setting up a listener for when the
    // user swipes between sections.
    mViewPager = (ViewPager) findViewById(R.id.pager);
    mViewPager.setAdapter(mAppSectionsPagerAdapter);
    mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override public void onPageSelected(int position) {
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

  protected int setPageIcon(int position, boolean active) {
    switch (position) {
      case 0:
        return active ? R.drawable.ic_menu_search_active : R.drawable.ic_menu_search;
      case 1:
        return active ? R.drawable.ic_menu_camera_active : R.drawable.ic_menu_camera;
      case 2:
        return active ? R.drawable.ic_menu_myplaces_active : R.drawable.ic_menu_myplaces;
    }
    return 0;
  }

  @Override public void onTabUnselected(ActionBar.Tab tab,
                                        FragmentTransaction fragmentTransaction) {
    tab.setIcon(setPageIcon(tab.getPosition(), false));
  }

  @Override public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    // When the given tab is selected, switch to the corresponding page in the ViewPager.
    mViewPager.setCurrentItem(tab.getPosition());
    tab.setIcon(setPageIcon(tab.getPosition(), true));
  }

  @Override public void onTabReselected(ActionBar.Tab tab,
                                        FragmentTransaction fragmentTransaction) {}

  /**
   * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding
   * to one of the primary
   * sections of the app.
   */
  public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {
    public AppSectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return ListingFragment.newInstance();
        case 1:
          return InsertAdContainerFragment.newInstance();
        default:
          return ProfileFragment.newInstance();
      }
    }

    @Override public int getCount() {
      return 3;
    }

    public int getPageIcon(int position) {
      switch (position) {
        case 0:
          return R.drawable.ic_menu_search;
        case 1:
          return R.drawable.ic_menu_camera;
        case 2:
          return R.drawable.ic_menu_myplaces;
      }
      return 0;
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    mRequestCode = requestCode;
    mResultCode = resultCode;
    mData = data;
//    bus.post(produceActivityResultEvent()); // redundant
  }

  @Override public void onResume() {
    super.onResume();
    bus.register(this);
  }

  @Override public void onPause() {
    super.onPause();
    bus.unregister(this);
  }

  @Produce public ActivityResultEvent produceActivityResultEvent() {
    return new ActivityResultEvent(mRequestCode, mResultCode, mData);
  }
}