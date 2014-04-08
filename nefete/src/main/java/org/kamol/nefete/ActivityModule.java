package org.kamol.nefete;

import android.content.Context;

import org.kamol.nefete.data.DataModule;
import org.kamol.nefete.ui.activity.MainActivity;
import org.kamol.nefete.ui.fragment.MainFragment;
import org.kamol.nefete.ui.fragment.PagerFragment;
import org.kamol.nefete.ui.gallery.GalleryView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * This module represents objects which exist only for the scope of a single activity.
 * We can safely create singletons using the activity instance because the entire
 * object graph will only ever exist inside of that activity.
 */
@Module(
    injects = {
        MainActivity.class,
        MainFragment.class,
        DataModule.class,
        PagerFragment.class
    },
    addsTo = AndroidModule.class,
    library = true
)
public class ActivityModule {
  private final BaseActivity activity;

  public ActivityModule(BaseActivity activity) {
    this.activity = activity;
  }

  /**
   * Allow the activity context to be injected but require that it be annotated with
   * {@link ForActivity @ForActivity} to explicitly differentiate it from application context.
   */
  @Provides @Singleton @ForActivity Context provideActivityContext() {
    return activity;
  }
}
