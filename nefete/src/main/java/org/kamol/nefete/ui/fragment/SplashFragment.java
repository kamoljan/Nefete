package org.kamol.nefete.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.kamol.nefete.R;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class SplashFragment extends Fragment {
  static final String TAG = "SplashFragment";
  @InjectView(R.id.b_login) Button btnLogin;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_splash, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @OnClick(R.id.b_login) public void onClickBtnLogin() {
    List<String> permissions = Arrays.asList("email");
    ParseFacebookUtils.logIn(permissions, getActivity(), new LogInCallback() {
      @Override
      public void done(ParseUser user, ParseException err) {
        if (user == null) {
          Timber.d(SplashFragment.TAG, "Uh oh. The user cancelled the Facebook login.");
        } else if (user.isNew()) {
          Timber.d(SplashFragment.TAG, "User signed up and logged in through Facebook!");
        } else {
          Timber.d(SplashFragment.TAG, "User logged in through Facebook!");
        }
      }
    });
  }

  private void destroyFragment() {
    getChildFragmentManager().beginTransaction().hide(this).commit();
  }
}
