package org.kamol.nefete;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.Uri;

import com.squareup.okhttp.HttpResponseCache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.kamol.nefete.data.api.ApiModule;
import org.kamol.nefete.ui.gallery.MyAdsView;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;


@Module(
    includes = MyAdsView.class,
    complete = false,
    library = true
)
public final class UserModule {

  private final String profile;

  public UserModule(String profile) {this.profile = profile;}

}

