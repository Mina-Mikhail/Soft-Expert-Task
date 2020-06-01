package com.mina_mikhail.soft_expert_task.data.remote.response;

import com.mina_mikhail.soft_expert_task.data.model.api.Car;
import java.util.List;

public class CarsResponse
    extends BaseResponse {

  private List<Car> data;

  public List<Car> getData() {
    return data;
  }
}
