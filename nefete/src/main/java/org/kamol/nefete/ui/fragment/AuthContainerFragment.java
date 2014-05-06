package org.kamol.nefete.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import org.kamol.nefete.BaseFragment;
import org.kamol.nefete.R;

public abstract class AuthContainerFragment extends BaseFragment {
  private static final String TAG = "ProfileContainerFragment";
  private UiLifecycleHelper uiHelper;
  private Fragment splashFragment;
  private Session.StatusCallback callback = new Session.StatusCallback() {
    @Override public void call(Session session, SessionState state, Exception exception) {
      onSessionStateChange(session, state, exception);
    }
  };

  // Implement your own showAuthFragment
  public abstract void showAuthFragment();

  private void onSessionStateChange(Session session, SessionState state, Exception exception) {
    if (state.isOpened()) {
      Log.i(TAG, "Logged in...");
      showAuthFragment();
    } else if (state.isClosed()) {
      Log.i(TAG, "Logged out...");
      showSplashFragment();
    }
  }

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

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    uiHelper = new UiLifecycleHelper(getActivity(), callback);
    uiHelper.onCreate(savedInstanceState);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_container, container, false);
  }

  @Override public void onResume() {
    super.onResume();
    Session session = Session.getActiveSession();
    if (session != null && (session.isOpened() || session.isClosed())) {
      onSessionStateChange(session, session.getState(), null);
    } else {
      showSplashFragment();
    }
    uiHelper.onResume();
  }

  @Override public void onPause() {
    super.onPause();
    uiHelper.onPause();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    uiHelper.onDestroy();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    uiHelper.onSaveInstanceState(outState);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    uiHelper.onActivityResult(requestCode, resultCode, data);
  }
}
