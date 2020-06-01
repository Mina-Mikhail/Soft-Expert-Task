package com.mina_mikhail.soft_expert_task.ui.main;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.mina_mikhail.soft_expert_task.R;
import com.mina_mikhail.soft_expert_task.app.MyApplication;
import com.mina_mikhail.soft_expert_task.data.model.api.Car;
import com.mina_mikhail.soft_expert_task.databinding.ItemCarBinding;
import com.mina_mikhail.soft_expert_task.ui.base.BaseAdapter;
import com.mina_mikhail.soft_expert_task.ui.base.BaseViewHolder;
import java.util.List;

public class CarsAdapter
    extends BaseAdapter<Car> {

  private List<Car> items;

  private int lastPosition = -1;

  CarsAdapter(List<Car> items) {
    super(items);
    this.items = items;
  }

  @Override
  public RecyclerView.ViewHolder setViewHolder(ViewGroup parent) {
    ItemCarBinding binding = DataBindingUtil.inflate(
        LayoutInflater.from(parent.getContext()),
        R.layout.item_car,
        parent,
        false);
    return new CarsAdapter.ViewHolder(binding);
  }

  @Override
  public void onBindData(RecyclerView.ViewHolder holder, int position) {
    ViewHolder myHolder = (ViewHolder) holder;
    myHolder.onBind(position);

    // add anim to items
    setAnimation(holder.itemView, position);
  }

  // start animation
  private void setAnimation(View viewToAnimate, int position) {
    // If the bound view wasn't previously displayed on screen, it's animated
    if (position > lastPosition) {
      Animation animation = AnimationUtils.loadAnimation(MyApplication.getInstance(),
          (position > lastPosition) ? R.anim.translate : R.anim.slide_up);
      viewToAnimate.startAnimation(animation);
      lastPosition = position;
    }
  }

  @Override
  public long getItemId(int position) {
    // To solve blinking after notifyDataSetChanged()
    return position;
  }

  // To solve the problem of fast scroll
  @Override
  public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    ViewHolder myHolder = (ViewHolder) holder;
    myHolder.clearAnimation();
    myHolder.unbind();
  }

  class ViewHolder
      extends BaseViewHolder {

    private final ItemCarBinding itemBinding;

    ViewHolder(ItemCarBinding itemBinding) {
      super(itemBinding.getRoot());
      this.itemBinding = itemBinding;
    }

    @Override
    public void onBind(int position) {
      itemBinding.setItem(items.get(position));
    }

    @Override
    public void unbind() {// Don't forget to unbind
      if (itemBinding != null) {
        itemBinding.unbind();
      }
    }

    @Override
    public void clearAnimation() {
      itemView.clearAnimation();
    }
  }
}