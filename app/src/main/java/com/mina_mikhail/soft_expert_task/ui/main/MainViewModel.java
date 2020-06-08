package com.mina_mikhail.soft_expert_task.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mina_mikhail.soft_expert_task.data.model.api.Car;
import com.mina_mikhail.soft_expert_task.data.remote.ApiClient;
import com.mina_mikhail.soft_expert_task.data.remote.NetworkHelper;
import com.mina_mikhail.soft_expert_task.data.remote.response.CarsResponse;
import com.mina_mikhail.soft_expert_task.ui.base.BaseViewModel;
import com.mina_mikhail.soft_expert_task.utils.Constants;
import com.mina_mikhail.soft_expert_task.utils.SingleLiveEvent;
import java.util.List;

public class MainViewModel
    extends BaseViewModel {

  private int currentPage;
  private boolean isLoading;
  private boolean shouldLoadMore;

  private MutableLiveData<List<Car>> cars;
  private SingleLiveEvent<Boolean> clearOldList;

  public MainViewModel() {
    currentPage = 1;
    clearOldList = new SingleLiveEvent<>();

    cars = new MutableLiveData<>();
  }

  void getCars() {
    helper = new NetworkHelper.NetworkBuilder<CarsResponse>()
        .setApiCall(ApiClient.getInstance().getApiService()
            .getCars(currentPage))
        .setListener(new NetworkHelper.NetworkListener() {
          @Override
          public <V> void onApiSuccess(V response) {
            CarsResponse apiResponse = ((CarsResponse) response);
            List<Car> historyList = apiResponse.getData();

            if (currentPage == 1) {
              clearOldList.setValue(true);
            }
            cars.setValue(historyList);

            if (historyList.size() == Constants.LIST_PAGE_SIZE) {
              currentPage++;
              shouldLoadMore = true;
            } else {
              shouldLoadMore = false;
            }

            isLoading = false;
          }

          @Override
          public void onApiError() {
            cars.setValue(null);
          }

          @Override
          public void onCallFail() {
            apiFail().call();
          }
        })
        .call();
  }

  void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }

  int getCurrentPage() {
    return currentPage;
  }

  boolean isLoading() {
    return isLoading;
  }

  void setLoading(boolean loading) {
    isLoading = loading;
  }

  boolean shouldLoadMore() {
    return shouldLoadMore;
  }

  void setShouldLoadMore(boolean shouldLoadMore) {
    this.shouldLoadMore = shouldLoadMore;
  }

  LiveData<Boolean> clearOldList() {
    return clearOldList;
  }

  MutableLiveData<List<Car>> getCarsData() {
    return cars;
  }
}