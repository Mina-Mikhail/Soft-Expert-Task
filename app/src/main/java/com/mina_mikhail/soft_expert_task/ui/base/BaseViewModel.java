package com.mina_mikhail.soft_expert_task.ui.base;

import androidx.lifecycle.ViewModel;
import com.mina_mikhail.soft_expert_task.data.remote.NetworkHelper;
import com.mina_mikhail.soft_expert_task.utils.DisplayLoader;
import com.mina_mikhail.soft_expert_task.utils.SingleLiveEvent;
import com.mina_mikhail.soft_expert_task.utils.display_message.DisplayMessage;
import io.reactivex.disposables.CompositeDisposable;

public class BaseViewModel
    extends ViewModel {

  private CompositeDisposable mCompositeDisposable;
  private DisplayMessage message;
  private DisplayLoader loader;
  private SingleLiveEvent<Void> apiFail;
  private SingleLiveEvent<Void> hideKeyboard;
  public NetworkHelper helper;

  public BaseViewModel() {
    mCompositeDisposable = new CompositeDisposable();
    message = new DisplayMessage();
    loader = new DisplayLoader();
    apiFail = new SingleLiveEvent<>();
    hideKeyboard = new SingleLiveEvent<>();
  }

  public CompositeDisposable getDisposable() {
    return mCompositeDisposable;
  }

  public DisplayMessage getMessageObserver() {
    return message;
  }

  public DisplayLoader getLoaderObserver() {
    return loader;
  }

  public SingleLiveEvent<Void> apiFail() {
    return apiFail;
  }

  public SingleLiveEvent<Void> hideKeyboard() {
    return hideKeyboard;
  }

  @Override
  protected void onCleared() {
    dispose();
    super.onCleared();
  }

  private void dispose() {
    if (!mCompositeDisposable.isDisposed()) {
      mCompositeDisposable.clear();
      mCompositeDisposable.dispose();
    }

    if (helper != null) {
      helper.dispose();
    }
  }
}