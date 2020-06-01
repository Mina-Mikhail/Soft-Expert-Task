package com.mina_mikhail.soft_expert_task.ui.main;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.view.View;
import com.mina_mikhail.soft_expert_task.BR;
import com.mina_mikhail.soft_expert_task.R;
import com.mina_mikhail.soft_expert_task.data.model.api.Car;
import com.mina_mikhail.soft_expert_task.databinding.ActivityMainBinding;
import com.mina_mikhail.soft_expert_task.ui.base.BaseActivity;
import com.mina_mikhail.soft_expert_task.utils.CommonUtils;
import java.util.ArrayList;
import java.util.List;

public class MainActivity
    extends BaseActivity<ActivityMainBinding, MainViewModel> {

  public static void open(Activity activity) {
    Intent intent = new Intent(activity, MainActivity.class);
    activity.startActivity(intent);
  }

  private MainViewModel mViewModel;

  private static boolean IS_API_CALLED = false;
  private static boolean SHOULD_CALL_API_AGAIN = false;

  private CarsAdapter carsAdapter;
  private List<Car> cars = new ArrayList<>();

  @Override
  public int getBindingVariable() {
    return BR.viewModel;
  }

  @Override
  public int getLayoutId() {
    return R.layout.activity_main;
  }

  @Override
  public MainViewModel getViewModel() {
    return mViewModel;
  }

  @Override
  protected void setUpViewModel() {
    mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    getViewDataBinding().setViewModel(getViewModel());
    initBaseObservables();
  }

  @Override
  protected void setUpViews() {
    initToolBar();

    initCarsRecyclerView();

    getData();
  }

  private void initToolBar() {
    getViewDataBinding().includedToolbar.toolbarTitle
        .setText(getResources().getString(R.string.cars));
  }

  private void initCarsRecyclerView() {
    CommonUtils.configRecyclerView(getViewDataBinding().includedList.recyclerView, true);
    carsAdapter = new CarsAdapter(cars);
    getViewDataBinding().includedList.recyclerView.setAdapter(carsAdapter);
    getViewDataBinding().includedList.reloadBtn.setOnClickListener(v -> getData());
  }

  private void getData() {
    IS_API_CALLED = false;
    showProgress();
    getViewModel().getCars(1);
  }

  @Override
  protected void setUpObservables() {
    getViewModel().getCarsData().observe(this, cars -> {
      IS_API_CALLED = true;
      SHOULD_CALL_API_AGAIN = false;
      if (cars != null) {
        carsAdapter.replaceItems(cars);
        showData();
      } else {
        showNoData();
      }
    });
  }

  private void showData() {
    getViewDataBinding().includedList.recyclerView.setVisibility(View.VISIBLE);
    getViewDataBinding().includedList.container.setVisibility(View.GONE);
    getViewDataBinding().includedList.progressBar.setVisibility(View.GONE);
  }

  private void showNoData() {
    getViewDataBinding().includedList.recyclerView.setVisibility(View.GONE);
    getViewDataBinding().includedList.progressBar.setVisibility(View.GONE);
    getViewDataBinding().includedList.emptyViewContainer.setVisibility(View.VISIBLE);
    getViewDataBinding().includedList.internetErrorViewContainer.setVisibility(View.GONE);
    getViewDataBinding().includedList.reloadBtn.setVisibility(View.GONE);
    getViewDataBinding().includedList.container.setVisibility(View.VISIBLE);
  }

  private void showProgress() {
    getViewDataBinding().includedList.recyclerView.setVisibility(View.GONE);
    getViewDataBinding().includedList.progressBar.setVisibility(View.VISIBLE);
    getViewDataBinding().includedList.emptyViewContainer.setVisibility(View.GONE);
    getViewDataBinding().includedList.internetErrorViewContainer.setVisibility(View.GONE);
    getViewDataBinding().includedList.reloadBtn.setVisibility(View.GONE);
    getViewDataBinding().includedList.container.setVisibility(View.VISIBLE);
  }

  private void showNoInternet() {
    getViewDataBinding().includedList.recyclerView.setVisibility(View.GONE);
    getViewDataBinding().includedList.progressBar.setVisibility(View.GONE);
    getViewDataBinding().includedList.emptyViewContainer.setVisibility(View.GONE);
    getViewDataBinding().includedList.internetErrorViewContainer.setVisibility(View.VISIBLE);
    getViewDataBinding().includedList.reloadBtn.setVisibility(View.VISIBLE);
    getViewDataBinding().includedList.container.setVisibility(View.VISIBLE);
  }

  @Override
  public void networkAvailable() {
    super.networkAvailable();

    if (SHOULD_CALL_API_AGAIN) {
      getData();
    }
  }

  @Override
  public void networkUnavailable() {
    super.networkUnavailable();

    if (!IS_API_CALLED) {
      showNoInternet();
      SHOULD_CALL_API_AGAIN = true;
    }
  }

  @Override
  protected void onDestroy() {
    IS_API_CALLED = false;
    SHOULD_CALL_API_AGAIN = false;
    super.onDestroy();
  }
}