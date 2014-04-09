package org.kamol.nefete;

import android.app.Application;
import android.content.Context;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

public class NefeteApp extends Application {
  private ObjectGraph applicationGraph;

  @Override public void onCreate() {
    super.onCreate();

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
