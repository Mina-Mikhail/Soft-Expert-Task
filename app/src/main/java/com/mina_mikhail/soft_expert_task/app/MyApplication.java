package com.mina_mikhail.soft_expert_task.app;

import android.app.Application;
import android.os.StrictMode;
import com.mina_mikhail.soft_expert_task.BuildConfig;
import io.reactivex.internal.functions.Functions;
import io.reactivex.plugins.RxJavaPlugins;

public class MyApplication
    extends Application {

  private static MyApplication INSTANCE;

  public static MyApplication getInstance() {
    return INSTANCE;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    INSTANCE = this;

    setStrictMode();
    initRXJava();
  }
  private void setStrictMode() {
    if (BuildConfig.DEBUG) {
      StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads()
          .detectDiskWrites()
          .detectNetwork()   // or .detectAll() for all detectable problems
          .penaltyLog()
          .build());
      StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
          .detectLeakedClosableObjects()
          .penaltyLog()
          .build());
    }
  }

  private void initRXJava() {
    RxJavaPlugins.setErrorHandler(Functions.emptyConsumer());
  }
}