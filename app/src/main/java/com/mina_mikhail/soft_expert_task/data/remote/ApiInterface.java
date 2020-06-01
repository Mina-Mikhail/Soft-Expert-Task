package com.mina_mikhail.soft_expert_task.data.remote;

import com.mina_mikhail.soft_expert_task.data.remote.response.CarsResponse;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

  @GET("cars")
  Single<CarsResponse> getCars(@Query("page") int pageNumber);
}