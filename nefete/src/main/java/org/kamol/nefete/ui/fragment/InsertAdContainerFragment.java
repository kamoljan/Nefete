package org.kamol.nefete.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import org.kamol.nefete.R;

public class InsertAdContainerFragment extends AuthContainerFragment {
  private Fragment insertAdFragment;

  public static InsertAdContainerFragment newInstance() {
    return new InsertAdContainerFragment();
  }

  @Override public void showAuthFragment() {
    if (getChildFragmentManager().findFragmentByTag(InsertAdFragment.TAG) != null) {
      insertAdFragment = getChildFragmentManager().findFragmentByTag(InsertAdFragment.TAG);
    } else {
      insertAdFragment = new InsertAdFragment();
      FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
      transaction.replace(R.id.ll_fragment_container, insertAdFragment, InsertAdFragment.TAG);
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }
}