package org.kamol.nefete;

import android.content.Context;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import org.kamol.nefete.event.ActivityResultEvent;
import org.kamol.nefete.ui.activity.MainActivity;
import org.kamol.nefete.ui.fragment.InsertAdContainerFragment;
import org.kamol.nefete.ui.fragment.InsertAdFragment;
import org.kamol.nefete.ui.fragment.ListingFragment;
import org.kamol.nefete.ui.fragment.MainFragment;
import org.kamol.nefete.ui.fragment.MyAdsFragment;
import org.kamol.nefete.ui.fragment.ProfileContainerFragment;

/**
 * This module represents objects which exist only for the scope of a single activity.
 * We can safely create singletons using the activity instance because the entire
 * object graph will only ever exist inside of that activity.
 */
@Module(
    injects = {
        MainActivity.class,
        MainFragment.class,
        ListingFragment.class,
        InsertAdFragment.class,
        InsertAdContainerFragment.class,
        ProfileContainerFragment.class,
        ActivityResultEvent.class,
        MyAdsFragment.class
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

  @Provides @Singleton Bus provideBus() {
    return new Bus();
  }
}
