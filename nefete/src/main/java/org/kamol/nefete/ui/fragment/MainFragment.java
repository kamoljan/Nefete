package org.kamol.nefete.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.kamol.nefete.BaseFragment;

import static android.view.Gravity.CENTER;

public class MainFragment extends BaseFragment {
  public static MainFragment newInstance() {
    return new MainFragment();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
    TextView tv = new TextView(getActivity());
    tv.setGravity(CENTER);
    tv.setText("MainFragment");
    return tv;
  }

  @Override public void onResume() {
    super.onResume();
  }
}
