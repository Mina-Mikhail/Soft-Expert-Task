package com.mina_mikhail.soft_expert_task.ui.base;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public abstract class BaseViewHolder
    extends RecyclerView.ViewHolder {

  public BaseViewHolder(View itemView) {
    super(itemView);
  }

  public abstract void onBind(int position);

  public abstract void unbind();

  public abstract void clearAnimation();
}
