package com.mina_mikhail.soft_expert_task.utils.display_message;

import androidx.lifecycle.LifecycleOwner;
import com.mina_mikhail.soft_expert_task.utils.SingleLiveEvent;

public class DisplayMessage
    extends SingleLiveEvent<Message> {

  public void observe(LifecycleOwner owner, final MessageObserver observer) {
    super.observe(owner, t -> {
      if (t == null) {
        return;
      }
      observer.onNewMessage(t);
    });
  }

  public interface MessageObserver {
    /**
     * Called when there is a new message to be shown.
     *
     * @param message The new message
     */
    void onNewMessage(Message message);
  }
}