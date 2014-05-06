package org.kamol.nefete.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.kamol.nefete.BaseFragment;
import org.kamol.nefete.R;

public class MyAdsFragment extends BaseFragment {
  static final String TAG = "MyAdsFragment";

  public static MyAdsFragment newInstance() {
    return new MyAdsFragment();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
    return inflater.inflate(R.layout.gallery_myads_view, container, false);
  }
}
