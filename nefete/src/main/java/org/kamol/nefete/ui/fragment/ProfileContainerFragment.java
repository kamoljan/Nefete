package org.kamol.nefete.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import org.kamol.nefete.R;

public class ProfileContainerFragment extends AuthContainerFragment {
  private Fragment myAdsFragment;

  public static ProfileContainerFragment newInstance() {
    return new ProfileContainerFragment();
  }

  @Override public void showAuthFragment() {
    if (getChildFragmentManager().findFragmentByTag(MyAdsFragment.TAG) != null) {
      myAdsFragment = getChildFragmentManager().findFragmentByTag(MyAdsFragment.TAG);
    } else {
      myAdsFragment = new MyAdsFragment();
      FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
      transaction.replace(R.id.ll_fragment_container, myAdsFragment, MyAdsFragment.TAG);
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }
}
