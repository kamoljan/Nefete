package org.kamol.nefete;

import android.app.Application;
import android.content.Context;

import java.util.Arrays;
import java.util.List;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.PushService;

import org.kamol.nefete.ui.activity.ViewActivity;

import dagger.ObjectGraph;
import timber.log.Timber;

import static timber.log.Timber.DebugTree;

public class NefeteApp extends Application {
  private ObjectGraph applicationGraph;

  @Override public void onCreate() {
    super.onCreate();

    Parse.initialize(this, "aTAL8FkWWQRG2bsilzCzwQpVMY2YCK8skFryZIFa", "vLefUWhxBk12E8X4oighFYt25iGnMYRTUP58RDJg");
    ParseFacebookUtils.initialize(getString(R.string.app_id));
    PushService.setDefaultPushCallback(this, ViewActivity.class);
    ParseInstallation.getCurrentInstallation().saveInBackground();

    if (BuildConfig.DEBUG) {
      Timber.plant(new DebugTree());
    } else {
      // TODO Crashlytics.start(this);
      // TODO Timber.plant(new CrashlyticsTree());
    }

    applicationGraph = ObjectGraph.create(getModules().toArray());
  }

  /**
   * A list of modules to use for the application graph.
   * Subclasses can override this method to
   * provide additional modules provided they call {@code super.getModules()}.
   */
  protected List<Object> getModules() {
    return Arrays.<Object>asList(new AndroidModule(this));
  }

  ObjectGraph getApplicationGraph() {
    return applicationGraph;
  }

  //TODO: Added from U2020, is it correct?
  public void inject(Object o) {
    applicationGraph.inject(o);
  }

  //TODO: Added from U2020, is it correct?
  public static NefeteApp get(Context context) {
    return (NefeteApp) context.getApplicationContext();
  }

}
