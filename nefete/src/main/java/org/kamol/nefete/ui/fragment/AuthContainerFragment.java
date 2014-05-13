package org.kamol.nefete.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.parse.ParseFacebookUtils;

import org.kamol.nefete.BaseFragment;
import org.kamol.nefete.R;

public abstract class AuthContainerFragment extends BaseFragment {
  private static final String TAG = "AuthContainerFragment";
  private Fragment splashFragment;

  // Implement your own
  public abstract void showAuthFragment();

  private void showSplashFragment() {
    if (getChildFragmentManager().findFragmentByTag(SplashFragment.TAG) != null) {
      splashFragment = getChildFragmentManager().findFragmentByTag(SplashFragment.TAG);
    } else {
      splashFragment = new SplashFragment();
      FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
      transaction.replace(R.id.ll_fragment_container, splashFragment, SplashFragment.TAG);
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_container, container, false);
  }

  @Override public void onResume() {
    super.onResume();
    // Fetch Facebook user info if the session is active
    Session session = ParseFacebookUtils.getSession();
    if (session != null && session.isOpened()) {
      showAuthFragment();
    } else {
      showSplashFragment();
    }
  }
}
