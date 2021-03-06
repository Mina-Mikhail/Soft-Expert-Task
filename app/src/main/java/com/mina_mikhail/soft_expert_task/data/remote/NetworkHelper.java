package com.mina_mikhail.soft_expert_task.data.remote;

import com.mina_mikhail.soft_expert_task.data.remote.response.BaseResponse;
import com.uber.autodispose.ScopeProvider;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.uber.autodispose.AutoDispose.autoDisposable;

/*
 * *
 *  * Created by Mina Mikhail on 29/04/2019
 *  * Copyright (c) 2019 . All rights reserved.
 * *
 */

public class NetworkHelper<V extends BaseResponse> {

  private Single<V> apiCall;
  private NetworkListener listener;
  private CompositeDisposable disposable;

  private NetworkHelper(NetworkBuilder<V> builder) {
    apiCall = builder.apiCall;
    listener = builder.listener;

    // Start calling api logic
    disposable = new CompositeDisposable();
    callAPI();
  }

  private void callAPI() {
    disposable.add(apiCall
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .as(autoDisposable(ScopeProvider.UNBOUND))
        .subscribeWith(new DisposableSingleObserver<V>() {
          @Override
          public void onSuccess(V response) {
            if (!disposable.isDisposed()) {
              if (response.getStatus() == 1) {
                listener.onApiSuccess(response);
              } else {
                listener.onApiError();
              }

              dispose();
            }
          }

          @Override
          public void onError(Throwable e) {
            if (!disposable.isDisposed()) {
              System.out.println("---------CALL---ERROR--------" + e.getMessage());
              listener.onCallFail();

              dispose();
            }
          }
        }));
  }

  public interface NetworkListener {
    <V> void onApiSuccess(V response);

    void onApiError();

    void onCallFail();
  }

  public void dispose() {
    if (disposable != null) disposable.dispose();
  }

  public static class NetworkBuilder<V extends BaseResponse> {
    private Single<V> apiCall;
    private NetworkListener listener;

    public NetworkBuilder() {
    }

    public NetworkBuilder setApiCall(Single<V> apiCall) {
      this.apiCall = apiCall;
      return this;
    }

    public NetworkBuilder setListener(NetworkListener listener) {
      this.listener = listener;
      return this;
    }

    public NetworkHelper<V> call() {
      return new NetworkHelper<>(this);
    }
  }
}