package org.kamol.nefete;

import android.app.Application;
import android.content.Context;
import android.location.LocationManager;

import com.squareup.otto.Bus;

import org.kamol.nefete.data.DataModule;
import org.kamol.nefete.ui.gallery.ListingView;
import org.kamol.nefete.ui.gallery.MyAdsView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A module for Android-specific dependencies which require a {@link Context} or
 * {@link android.app.Application} to create.
 */
@Module(
    injects = {
        ListingView.class,
        MyAdsView.class,
    },
    includes = DataModule.class,
    complete = false,
    library = true
)
public class AndroidModule {
  private final NefeteApp application;

  public AndroidModule(NefeteApp application) {
    this.application = application;
  }

  /**
   * Allow the application context to be injected but require that it be annotated with
   * {@link ForApplication @ForApplication} to explicitly differentiate it
   * from an activity context.
   */
  //@Provides @Singleton @ForApplication Context provideApplicationContext() {
  //  return application;
  //}

  //http://stackoverflow.com/questions/22942327/no-injectable-members-on-android-app-application
  // -do-you-want-to-add-an-injectab
  @Provides @Singleton Application provideApplicationContext() {
    return application;
  }

  @Provides @Singleton LocationManager provideLocationManager() {
    return (LocationManager) application.getSystemService(LOCATION_SERVICE);
  }

  @Provides @Singleton Bus provideBus() {
    return new Bus();
  }

}
