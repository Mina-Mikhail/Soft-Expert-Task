package com.mina_mikhail.soft_expert_task.ui.main;

import android.arch.lifecycle.MutableLiveData;
import com.mina_mikhail.soft_expert_task.data.model.api.Car;
import com.mina_mikhail.soft_expert_task.data.remote.ApiClient;
import com.mina_mikhail.soft_expert_task.data.remote.NetworkHelper;
import com.mina_mikhail.soft_expert_task.data.remote.response.CarsResponse;
import com.mina_mikhail.soft_expert_task.ui.base.BaseViewModel;
import java.util.List;

public class MainViewModel
    extends BaseViewModel {

  private MutableLiveData<List<Car>> cars;

  public MainViewModel() {
    cars = new MutableLiveData<>();
  }

  void getCars(int pageNumber) {
    helper = new NetworkHelper.NetworkBuilder<CarsResponse>()
        .setApiCall(ApiClient.getInstance().getApiService()
            .getCars(pageNumber))
        .setListener(new NetworkHelper.NetworkListener() {
          @Override
          public <V> void onApiSuccess(V response) {
            cars.setValue(((CarsResponse) response).getData());
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

  MutableLiveData<List<Car>> getCarsData() {
    return cars;
  }
}