package com.fpliu.newton.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 自定义Toast，可以自有控制要显示的时间
 *
 * @author 792793182@qq.com 2015-06-16
 */
public final class CustomToast {
    /**
     * 预定义两个持续时间，与系统的Toast保持一致
     */
    public static final long LENGTH_SHORT = 1000;
    public static final long LENGTH_LONG = 3500;

    private long duration = LENGTH_SHORT;
    private WindowManager windowManager;
    private LinearLayout rootView;
    private OnDismissListener onDismissListener;

    public CustomToast(Context context) {
        // 此处必须是ApplicationContext，因为Activity退出也可以显示
        context = context.getApplicationContext();

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        rootView = new LinearLayout(context);

        // 背景设置为圆角矩形
        float r = 10;
        float[] outerR = new float[]{r, r, r, r, r, r, r, r};
        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(Color.BLACK);

        setBackground(drawable);
    }

    public CustomToast setBackground(Drawable drawable) {
        rootView.setBackgroundDrawable(drawable);
        return this;
    }

    /**
     * @param context  上下文
     * @param text     要显示的文本
     * @param duration 显示时长，单位：毫秒
     */
    public static CustomToast makeText(Context context, CharSequence text, long duration) {
        CustomToast toast = new CustomToast(context);

        toast.setDuration(duration);

        TextView textView = new TextView(context);
        int width = dip2px(context, 20);
        int height = dip2px(context, 20);
        textView.setPadding(width, height, width, height);
        textView.setText(text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        toast.setView(textView);

        // 背景设置为圆角矩形
        float r = 10;
        float[] outerR = new float[]{r, r, r, r, r, r, r, r};
        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(Color.parseColor("#BA000000"));

        toast.setBackground(drawable);

        return toast;
    }

    public static CustomToast makeText(Context context, int stringId, long duration)
            throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(stringId), duration);
    }

    /**
     * @param context  上下文
     * @param imageId  要显示的图片的Id
     * @param text     要显示的文本
     * @param duration 显示时长，单位：毫秒
     */
    public static CustomToast makeImageAndText(Context context, int imageId, CharSequence text, long duration) {
        CustomToast toast = new CustomToast(context);

        toast.setDuration(duration);

        int padding = dip2px(context, 20);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(2 * padding, padding, 2 * padding, padding);
        layout.setGravity(Gravity.CENTER);

        ImageView imageView = new ImageView(context);
        imageView.setBackgroundColor(Color.TRANSPARENT);
        imageView.setImageResource(imageId);

        TextView textView = new TextView(context);
        textView.setPadding(0, padding, 0, 0);
        textView.setGravity(Gravity.CENTER);
        textView.setText(text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);

        layout.addView(imageView);
        layout.addView(textView);

        toast.setView(layout);

        // 背景设置为圆角矩形
        float r = 10;
        float[] outerR = new float[]{r, r, r, r, r, r, r, r};
        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(Color.parseColor("#BA000000"));

        toast.setBackground(drawable);

        return toast;
    }

    /**
     * 显示时长，单位：毫秒
     */
    public void setDuration(long duration) {
        if (duration > 500) {
            this.duration = duration;
        }
    }

    /**
     * 可以放入任何视图，不仅仅是文本
     */
    public void setView(View view) {
        if (view != null && view.getParent() == null) {
            this.rootView.addView(view);
        }
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    /**
     * @param gravity 相对屏幕的参考点
     * @param xOffset 水平方向上的偏移量
     * @param yOffset 竖直方向上的偏移量
     * @see Gravity#TOP
     * @see Gravity#BOTTOM
     * @see Gravity#LEFT
     * @see Gravity#RIGHT
     */
    public void show(int gravity, int xOffset, int yOffset) {
        LayoutParams lp = getDefaultLayoutParams();
        lp.gravity = gravity;
        lp.x = xOffset;
        lp.y = yOffset;

        try {
            windowManager.addView(rootView, lp);
            handleDelayDismiss();
        } catch (Exception e) {
            //do nothing
        }
    }

    public void show() {
        show(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 100);
    }

    private LayoutParams getDefaultLayoutParams() {
        LayoutParams lp = new LayoutParams();
        lp.gravity = Gravity.CENTER;

        lp.dimAmount = 0f;
        lp.width = LayoutParams.WRAP_CONTENT;
        lp.height = LayoutParams.WRAP_CONTENT;
        lp.flags = LayoutParams.FLAG_DIM_BEHIND
                | LayoutParams.FLAG_NOT_FOCUSABLE
                | LayoutParams.FLAG_NOT_TOUCHABLE
                | LayoutParams.FLAG_KEEP_SCREEN_ON;
        lp.type = LayoutParams.TYPE_TOAST;
        // 此值必须设置为透明，否则圆角处会有黑色
        lp.format = PixelFormat.TRANSLUCENT;
        // 显示动画，此处必须是系统的样式资源
        lp.windowAnimations = android.R.style.Animation_Toast;
        return lp;
    }

    private void handleDelayDismiss() {
        new Handler().postDelayed(new Runnable() {

            public void run() {
                try {
                    dismiss();
                } catch (Exception e) {
                    // do nothing
                }
            }
        }, duration);
    }

    private void dismiss() {
        if (rootView != null) {
            if (rootView.getParent() != null) {
                windowManager.removeView(rootView);
                if (onDismissListener != null) {
                    onDismissListener.onDismiss();
                }
            }
            rootView = null;
        }
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    private static int dip2px(Context context, double dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5);
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
