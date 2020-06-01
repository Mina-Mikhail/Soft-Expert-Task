package com.mina_mikhail.soft_expert_task.utils.image_loader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mina_mikhail.soft_expert_task.R;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

@GlideModule
public class ImageUtils
    extends AppGlideModule {

  public static void loadImageWithGlide(String imageURL,
      int placeHolder,
      ImageView imageView,
      boolean cacheImage,
      ProgressBar progressBar) {
    if (imageURL == null || TextUtils.isEmpty(imageURL)) {
      if (progressBar != null) progressBar.setVisibility(View.GONE);
      imageView.setImageDrawable(null);
      imageView.setImageDrawable(
          imageView.getContext().getResources().getDrawable(R.drawable.bg_no_image));
      clearGlideCache(imageView);
      imageView.setVisibility(View.VISIBLE);
      return;
    }

    if (cacheImage) {
      GlideApp.with(imageView.getContext())
          .load(imageURL)
          .placeholder(placeHolder)
          .error(placeHolder)
          .listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                Target<Drawable> target, boolean isFirstResource) {

              if (progressBar != null) progressBar.setVisibility(View.GONE);
              imageView.setImageDrawable(null);
              imageView
                  .setImageDrawable(
                      imageView.getContext().getResources().getDrawable(R.drawable.bg_no_image));
              imageView.setVisibility(View.VISIBLE);
              return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                DataSource dataSource, boolean isFirstResource) {
              imageView.setVisibility(View.VISIBLE);
              imageView.setImageDrawable(resource);

              if (progressBar != null) progressBar.setVisibility(View.GONE);
              return false;
            }
          })
          .submit();
    } else {
      GlideApp.with(imageView.getContext())
          .load(imageURL)
          .placeholder(placeHolder)
          .error(placeHolder)
          .diskCacheStrategy(DiskCacheStrategy.NONE)
          .skipMemoryCache(true)
          .listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                Target<Drawable> target, boolean isFirstResource) {

              if (progressBar != null) progressBar.setVisibility(View.GONE);
              imageView.setImageDrawable(null);
              imageView
                  .setImageDrawable(
                      imageView.getContext().getResources().getDrawable(R.drawable.bg_no_image));
              imageView.setVisibility(View.VISIBLE);
              return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                DataSource dataSource, boolean isFirstResource) {
              imageView.setVisibility(View.VISIBLE);
              imageView.setImageDrawable(resource);

              if (progressBar != null) progressBar.setVisibility(View.GONE);

              return false;
            }
          })
          .submit();
    }
  }

  public static void clearGlideCache(ImageView imageView) {
    GlideApp.with(imageView.getContext()).clear(imageView);
  }

  @Override
  public void registerComponents(@NonNull Context context, @NonNull Glide glide,
      @NonNull Registry registry) {
    super.registerComponents(context, glide, registry);
    OkHttpClient client = new OkHttpClient.Builder()
        .addNetworkInterceptor(chain -> {
          Request request = chain.request();
          Response response = chain.proceed(request);
          ResponseProgressListener listener = new DispatchingProgressListener();
          return response.newBuilder()
              .body(new OkHttpProgressResponseBody(request.url(), response.body(), listener))
              .build();
        })
        .build();
    registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
  }

  public static void forget(String url) {
    DispatchingProgressListener.forget(url);
  }

  static void expect(String url, UIonProgressListener listener) {
    DispatchingProgressListener.expect(url, listener);
  }

  private interface ResponseProgressListener {
    void update(HttpUrl url, long bytesRead, long contentLength);
  }

  public interface UIonProgressListener {
    void onProgress(long bytesRead, long expectedLength);

    /**
     * Control how often the listener needs an update. 0% and 100% will always be dispatched.
     *
     * @return in percentage (0.2 = call {@link #onProgress} around every 0.2 percent of progress)
     */
    float getGranualityPercentage();
  }

  private static class DispatchingProgressListener
      implements ResponseProgressListener {
    private static final Map<String, UIonProgressListener> LISTENERS = new HashMap<>();
    private static final Map<String, Long> PROGRESSES = new HashMap<>();

    private final Handler handler;

    DispatchingProgressListener() {
      this.handler = new Handler(Looper.getMainLooper());
    }

    static void forget(String url) {
      LISTENERS.remove(url);
      PROGRESSES.remove(url);
    }

    static void expect(String url, UIonProgressListener listener) {
      LISTENERS.put(url, listener);
    }

    @Override
    public void update(HttpUrl url, final long bytesRead, final long contentLength) {
      //System.out.printf("%s: %d/%d = %.2f%%%n", url, bytesRead, contentLength, (100f * bytesRead) / contentLength);
      String key = url.toString();
      final UIonProgressListener listener = LISTENERS.get(key);
      if (listener == null) {
        return;
      }
      if (contentLength <= bytesRead) {
        forget(key);
      }
      if (needsDispatch(key, bytesRead, contentLength, listener.getGranualityPercentage())) {
        handler.post(() -> listener.onProgress(bytesRead, contentLength));
      }
    }

    private boolean needsDispatch(String key, long current, long total, float granularity) {
      if (granularity == 0 || current == 0 || total == current) {
        return true;
      }
      float percent = 100f * current / total;
      long currentProgress = (long) (percent / granularity);
      Long lastProgress = PROGRESSES.get(key);
      if (lastProgress == null || currentProgress != lastProgress) {
        PROGRESSES.put(key, currentProgress);
        return true;
      } else {
        return false;
      }
    }
  }

  private static class OkHttpProgressResponseBody
      extends ResponseBody {

    private final HttpUrl url;
    private final ResponseBody responseBody;
    private final ResponseProgressListener progressListener;
    private BufferedSource bufferedSource;

    OkHttpProgressResponseBody(HttpUrl url, ResponseBody responseBody,
        ResponseProgressListener progressListener) {
      this.url = url;
      this.responseBody = responseBody;
      this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
      return responseBody.contentType();
    }

    @Override
    public long contentLength() {
      return responseBody.contentLength();
    }

    @NonNull
    @Override
    public BufferedSource source() {
      if (bufferedSource == null) {
        bufferedSource = Okio.buffer(source(responseBody.source()));
      }
      return bufferedSource;
    }

    private Source source(Source source) {
      return new ForwardingSource(source) {
        long totalBytesRead = 0L;

        @Override
        public long read(@NonNull Buffer sink, long byteCount) throws IOException {
          long bytesRead = super.read(sink, byteCount);
          long fullLength = responseBody.contentLength();
          if (bytesRead == -1) { // this source is exhausted
            totalBytesRead = fullLength;
          } else {
            totalBytesRead += bytesRead;
          }
          progressListener.update(url, totalBytesRead, fullLength);
          return bytesRead;
        }
      };
    }
  }
}