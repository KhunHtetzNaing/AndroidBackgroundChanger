package com.htetznaing.droidxbg;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by HtetzNaing on 9/13/2017.
 */
public class Floating extends Service {
    WindowManager windowManager;
    LinearLayout linearLayout;
    ImageView imageView;
    SharedPreferences share;
    int alpha,style;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        share = getSharedPreferences("myFile",MODE_PRIVATE);
        alpha = share.getInt("alpha",30);
        style = share.getInt("style",1);
        ImageView.ScaleType imageStyle = ImageView.ScaleType.FIT_XY;
        switch (style){
            case 1: imageStyle = ImageView.ScaleType.FIT_XY;break;
            case 2: imageStyle = ImageView.ScaleType.FIT_CENTER;break;
            case 3: imageStyle = ImageView.ScaleType.CENTER;break;
            case 4: imageStyle = ImageView.ScaleType.CENTER_CROP;break;
            case 5: imageStyle = ImageView.ScaleType.CENTER_INSIDE;break;
            case 6: imageStyle = ImageView.ScaleType.FIT_START;break;
            case 7: imageStyle = ImageView.ScaleType.FIT_END;break;
            case 8: imageStyle = ImageView.ScaleType.MATRIX;break;

        }

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        linearLayout = new LinearLayout(this);
        imageView = new ImageView(this);
        Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/.AndXBackground.bg");
        imageView.setScaleType(imageStyle);
        imageView.setImageBitmap(bm);
        imageView.setAlpha(alpha);

        LinearLayout.LayoutParams llParameters = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setBackgroundColor(Color.TRANSPARENT);
        linearLayout.setLayoutParams(llParameters);
        linearLayout.addView(imageView,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

        final WindowManager.LayoutParams wmParameters = new WindowManager.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        wmParameters.y = 0;
        wmParameters.x = 0;
        wmParameters.gravity = Gravity.CENTER | Gravity.CENTER;
        windowManager.addView(linearLayout,wmParameters);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        windowManager.removeView(linearLayout);
        stopSelf();
    }
}
