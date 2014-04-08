package org.kamol.nefete.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;

import org.kamol.nefete.BaseFragment;
import org.kamol.nefete.R;

import static android.view.Gravity.CENTER;

public class PagerFragment extends BaseFragment {
  public static PagerFragment newInstance() {
    return new PagerFragment();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.gallery_view, container, false);


    return view;


//    TextView tv = new TextView(getActivity());
//    tv.setGravity(CENTER);
//    tv.setText("Hello Kenta");
//    return tv;
  }

  @Override public void onResume() {
    super.onResume();

    //
  }

}
