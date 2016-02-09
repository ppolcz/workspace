package polcz.peter.hf3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class MyDrawerView extends View {

    private Paint paint = new Paint();

    int width;
    int height;

    public MyDrawerView(Context context) {
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(1);
        paint.setShader(new LinearGradient(0.40f, 0.0f, 60.60f, 100.0f, Color.RED, Color.RED, Shader.TileMode.CLAMP));
    }

    public MyDrawerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        System.out.println(String.format("%d  %d", getWidth(), getHeight()));

        width = getWidth();
        height = getHeight();
    }

    @Override
    public void onDraw (Canvas canvas) {

        double[] x = linspace(-10.0, 10.0, 100);
        double[] y = Functions.sin(x);

        double x_min = x[0];
        double y_min = y[0];
        double x_max = x[0];
        double y_max = y[0];

        for (int i = 1; i < x.length; ++i) {
            if (x_min > x[i]) x_min = x[i];
            if (y_min > y[i]) y_min = y[i];
            if (x_max < x[i]) x_max = x[i];
            if (y_max < y[i]) y_max = y[i];
        }

        double dx = x_max - x_min;
        double dy = y_max - y_min;

        double scale_x = width / dx;
        double scale_y = height / dy;

        for (int i = 0; i < x.length - 1; ++i) {
            canvas.drawLine((int) (scale_x * (x[i] - x_min)), (int) (scale_y * (y[i] - y_min)),
                    (int) (scale_x * (x[i + 1] - x_min)), (int) (scale_y * (y[i + 1] - y_min)), paint);
        }
    }

    double[] linspace (double a, double b, double n) {
        double[] array = new double[(int) n];

        for (double i = 0; i < (int) n; i = i + 1.0) {
            array[(int) i] = (a * i + b * (n - 1.0 - i)) / (n - 1.0);
        }

        return array;
    }
}

class Functions {
    static double[] square (double[] x) {
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; ++i) {
            y[i] = Math.pow(x[i], 2.0);
        }
        return y;
    }
    
    static double[] sin (double[] x) {
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; ++i) {
            y[i] = Math.sin(x[i]);
        }
        return y;
    }
}
