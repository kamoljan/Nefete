package org.kamol.nefete;

import android.content.Context;
import android.location.LocationManager;

import org.kamol.nefete.data.DataModule;
import org.kamol.nefete.ui.gallery.GalleryView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A module for Android-specific dependencies which require a {@link Context} or
 * {@link android.app.Application} to create.
 */
@Module(
//    includes = DataModule.class,
    injects = DataModule.class,
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
  @Provides @Singleton @ForApplication Context provideApplicationContext() {
    return application;
  }

  @Provides @Singleton LocationManager provideLocationManager() {
    return (LocationManager) application.getSystemService(LOCATION_SERVICE);
  }
}
