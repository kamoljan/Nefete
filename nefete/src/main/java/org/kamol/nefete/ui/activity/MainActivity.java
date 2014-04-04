package org.kamol.nefete.ui.activity;

import android.location.LocationManager;
import android.os.Bundle;

import org.kamol.nefete.BaseActivity;
import org.kamol.nefete.ui.fragment.MainFragment;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {
  @Inject LocationManager locationManager;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // After the super.onCreate call returns we are guaranteed our injections are available.

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .add(android.R.id.content, MainFragment.newInstance())
          .commit();
    }

    // TODO do something with the injected dependencies here!
  }
}