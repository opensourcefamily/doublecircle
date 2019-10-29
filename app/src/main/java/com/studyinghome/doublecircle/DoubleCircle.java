package com.studyinghome.doublecircle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Michell chen 2017-2-22
 * @version 1.0
 * 仿俄罗斯转盘的双层滑动组件
 * 1：显示两级菜单，一级菜单与二级菜单联系
 * 2：与外部组件进行交互，由二级菜单的滑动决定外部显示数据
 * 3：显示数据可动态调整
 * 3：数据的获取与更改由helper类进行
 * 4：数据的变化也由helper控制
 */

public class DoubleCircle extends View {

    private static final int FIRST_CONTENT_NUM = 6;
    private static final int SECOND_CONTENT_NUM = 12;
    private static final int FIRST_CONTENT_COLOR_BASE = Color.parseColor("#FFC600");
    private static final int SECOND_CONTENT_COLOR_BASE = Color.parseColor("#36B0B1");
    private static final String[] FIRST_CONTENT = {"菜 系1", "菜 系2", "菜 系3", "菜 系4", "菜 系5", "菜 系6"};
    private static final String[] SECOND_CONTENT = {"菜 品", "菜 品", "菜 品", "菜 品", "菜 品", "菜 品", "菜 品", "菜 品", "菜 品", "菜 品", "菜 品", "菜 品"};
    private int firstChoose = 1;
    private int secondChoose = 1;
    private int secondChoosePosition = 1;
    //转盘两层颜色
    private int firstContentColor;
    private int secondContentColor;
    //转盘两层内容
    private String[] firstContent;
    private String[] secondContent;
    //    private List<Food> foodList;
    private Bitmap bitmap;
    private int width, height;
    private Paint paint;
    private RectF firstRec, secondRec, bitRec;
    private TouchMode mode = TouchMode.MODE_NORMAL;
    private int position = 1;
    private int rotateTime = 0;
    private int rotateTimeFirst = 0;
    private int rotatedFirst = 0;
    private int lastFirstRotated = 0;
    private int lastSecondRotated = 0;
    private int x, y;
    private FirstContentListener firstContentListener;
    private SecondContentListener secondContentListener;
    /**
     * 半径
     */
    private int radius;
    private float angle;

    public DoubleCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 无视padding
        setPadding(0, 0, 0, 0);
    }


    /**
     * 主要为了action_down时，返回true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        touchToRotate(event);
        return super.dispatchTouchEvent(event);
    }

    /**
     * 当View要为所有子对象分配大小和位置时，调用此方法
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        initResource();
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * View会调用此方法，来确认自己及所有子对象的大小
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 获得半径
        radius = Math.max(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(measureView(widthMode, widthSize), measureView(heightMode, heightSize));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 用于重绘的方法
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(SECOND_CONTENT_COLOR_BASE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        doDraw(canvas);
    }


    /**
     * 当View大小改变时，调用此方法
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        firstRec = new RectF(width / 6, height / 6, width * 5 / 6, height * 5 / 6);
        bitRec = new RectF(width / 3, height / 3, width * 2 / 3, height * 2 / 3);
        secondRec = new RectF(0, 0, width, height);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 对当前控件的高和宽进行测量
     *
     * @param mode          测量模式
     * @param widthOrHeight 测量宽或者高属性
     * @return 返回测量结果
     */
    private int measureView(int mode, int widthOrHeight) {
        int result;
        if (mode == MeasureSpec.EXACTLY) {
            result = widthOrHeight;
        } else {
            result = 400;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, widthOrHeight);
            }
        }
        return result;
    }

    /**
     * 设置当前的第一级转盘的内容
     *
     * @param firstContent 内容数组
     */
    public void setFirstContent(String[] firstContent) {
        this.firstContent = firstContent;
        invalidate();
    }

    /**
     * 设置当前的第二级转盘的内容
     */
    public void setSecondContent(String[] secondContent) {
        this.secondContent = secondContent;
        invalidate();
    }

    /**
     * 设置第一级转盘的颜色
     *
     * @param firstContentColor 第一级转盘颜色
     */
    public void setFirstContentColor(int firstContentColor) {
        this.firstContentColor = firstContentColor;
        invalidate();
    }

    /**
     * 解析资源
     */
    private void initResource() {
        paint = new Paint();
        //添加内容
        firstContent = FIRST_CONTENT;
        secondContent = SECOND_CONTENT;
        //设置颜色
        firstContentColor = FIRST_CONTENT_COLOR_BASE;
        secondContentColor = SECOND_CONTENT_COLOR_BASE;
        //设置中间logo
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_russian, options);
    }

    /**
     * 绘制当前内容
     */
    private void doDraw(Canvas canvas) {
        //绘制第二个圆
        drawSecondContent(canvas);
        //绘制第一个圆
        drawFirstContent(canvas);
        //绘制中心logo
        canvas.drawBitmap(bitmap, null, bitRec, paint);

        if (mode == TouchMode.MODE_FIRST) {
            drawFirstContentText(canvas, 8 * rotateTimeFirst);
            drawSecondContentText(canvas, -8 * rotateTimeFirst);
        } else if (mode == TouchMode.MODE_SECOND) {
            drawSecondContentText(canvas, 4 * rotateTime);
            canvas.rotate(rotatedFirst * 8, width / 2, height / 2);
            drawFirstContentText(canvas, -4 * rotateTime);
        } else {
            drawFirstContentText(canvas, 0);
            drawSecondContentText(canvas, 0);
        }
    }

    /**
     * 获取当前第一层转盘的选中内容
     *
     * @return 选中内容
     */
    public String getChoosedFirstContent() {
        return firstContent[firstChoose];
    }

    /**
     * 获取当前第二层转盘的选中内容
     *
     * @return 选中内容数组
     */
    public String getChoosedSecondContent() {
        return secondContent[secondChoose];
    }

    /**
     * 处理触摸事件
     *
     * @param event 触摸事件
     */
    private void touchToRotate(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getX();
                y = (int) event.getY();
                positionLocate(x, y);
                positionPart(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                int moveY = (int) event.getY();
                touchHandle(moveX, moveY);
                x = moveX;
                y = moveY;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
//                if (mode == TouchMode.MODE_FIRST) {
//                    int rotatedRadius = rotateTimeFirst * 8;
//                    int rotated = rotatedRadius / 60;
//                    int leftRadius = rotatedRadius % 60;
//                    if (leftRadius > 30) {
//                        rotated += 1;
//                    }
//                    firstChoose -= (rotated - lastFirstRotated);
//                    lastFirstRotated = rotated;
//                    if (firstChoose < 0) {
//                        firstChoose = 5;
//                    } else if (firstChoose > 5) {
//                        firstChoose = 0;
//                    }
//                    firstContentListener.rotate(getChoosedFirstContent());
//                } else if (mode == TouchMode.MODE_SECOND) {
//                    int rotateRadius = rotateTime * 4;
//                    int rotated = rotateRadius / 30;
//                    int leftRadius = rotateRadius % 30;
//                    if (leftRadius > 15) {
//                        rotated += 1;
//                    }
//                    secondChoosePosition -= (rotated - lastSecondRotated);
//                    lastSecondRotated = rotated;
//                    if (secondChoosePosition < 0) {
//                        secondChoosePosition = 11;
//                    } else if (firstChoose > 11) {
//                        secondChoosePosition = 0;
//                    }
//                    secondContentListener.rotate(getChoosedSecondContent());
//                }
                break;
            default:
                break;
        }
    }

    /**
     * 绘制第一部分的图形
     *
     * @param canvas 画布
     */
    private void drawFirstContent(Canvas canvas) {
        for (int i = 0; i < FIRST_CONTENT_NUM; i++) {
            if (i == 0) {
                paint.setColor(Color.parseColor("#FFD052"));
            } else {
                paint.setColor(FIRST_CONTENT_COLOR_BASE);
            }
            canvas.rotate(60, width / 2, height / 2);
            canvas.drawArc(firstRec, 0, 61, true, paint);
        }
    }

    /**
     * 绘制第二部分的图形
     *
     * @param canvas 画布
     */
    private void drawSecondContent(Canvas canvas) {
        for (int i = 0; i < SECOND_CONTENT_NUM; i++) {
            if (i == 1 || i == 4 || i == 7 || i == 10) {
                paint.setColor(Color.parseColor("#5EC0C1"));
            } else {
                paint.setColor(SECOND_CONTENT_COLOR_BASE);
            }
            canvas.drawArc(secondRec, 0, 31, true, paint);
            canvas.rotate(30, width / 2, height / 2);
        }
    }

    /**
     * 绘制第一部分的文字内容
     *
     * @param canvas 画布
     * @param degree 已经转动的角度
     */
    private void drawFirstContentText(Canvas canvas, float degree) {
//        System.out.println("degree======" + degree);

        canvas.rotate(degree, width / 2, height / 2);
        for (int i = 0; i < FIRST_CONTENT_NUM; i++) {
            paint.setColor(Color.WHITE);
            paint.setTextSize(width / 18);
            paint.setFakeBoldText(true);
            Path path = new Path();
            path.addArc(firstRec, 0, 60);
            String text = firstContent[i];
            canvas.drawTextOnPath(text, path, width / 12, height / 12, paint);
            canvas.rotate(60, width / 2, height / 2);
        }
    }

    /**
     * 绘制第二部分的文字内容
     *
     * @param canvas       画布
     * @param rotateDegree 已经转动的角度
     */
    private void drawSecondContentText(Canvas canvas, float rotateDegree) {
        canvas.rotate(rotateDegree, width / 2, height / 2);
        for (int i = 0; i < SECOND_CONTENT_NUM; i++) {
            paint.setColor(Color.WHITE);
            paint.setTextSize(width / 22);
            Path path = new Path();
            path.addArc(secondRec, 0, 30);
            String text = SECOND_CONTENT[i];
            canvas.drawTextOnPath(text, path, width / 12, height / 12, paint);
            canvas.rotate(30, width / 2, height / 2);
        }
    }

    /**
     * 判断当前的触摸点的位置在哪个圆的内部
     */
    private void positionLocate(float x, float y) {
        double position = Math.sqrt((x - width / 2) * (x - width / 2) + (y - height / 2) * (y - height / 2));
        if (position > width / 3 && position <= width / 2) {//在第2个圆内
            mode = TouchMode.MODE_SECOND;
        } else if (position <= width / 3) {//在第一个圆内
            mode = TouchMode.MODE_FIRST;
        }
    }

    /**
     * 根据触摸的位置，计算角度
     *
     * @param xTouch
     * @param yTouch
     * @return
     */
    private float getAngle(float xTouch, float yTouch) {
        double x = xTouch - (radius / 2d);
        double y = yTouch - (radius / 2d);
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    /**
     * 判断当前的触摸点所在的区域
     *
     * @param x x
     * @param y y
     */
    private int positionPart(float x, float y) {
//        if (x < width / 2 && y < height / 2) {
//            position = 1;
//        } else if (x > width / 2 && y < height / 2) {
//            position = 2;
//        } else if (x > width / 2 && height > height / 2) {
//            position = 3;
//        } else if (x < width / 2 && height > height / 2) {
//            position = 4;
//        }

        int tmpX = (int) (x - radius / 2);
        int tmpY = (int) (y - radius / 2);
        if (tmpX >= 0) {
            return position = tmpY >= 0 ? 4 : 1;
        } else {
            return position = tmpY >= 0 ? 3 : 2;
        }

    }

    /**
     * 不同位置的触摸处理
     *
     * @param moveX x
     * @param moveY y
     */
    private void touchHandle(int moveX, int moveY) {
        /**
         * 获得开始的角度
         */
        float start = getAngle(x, y);
        /**
         * 获得当前的角度
         */
        float end = getAngle(moveX, moveY);
        if (end - start == 0) {

        } else if (positionPart(moveX, moveY) == 1 || positionPart(moveX, moveY) == 4) {
            if (end - start > 0) {
                if (mode == TouchMode.MODE_FIRST) {
                    rotateTimeFirst += 1;
                    rotatedFirst += 1;
                } else if (mode == TouchMode.MODE_SECOND) {
                    rotateTime += 1;
                }
            } else {
                if (mode == TouchMode.MODE_FIRST) {
                    rotateTimeFirst -= 1;
                    rotatedFirst -= 1;
                } else if (mode == TouchMode.MODE_SECOND) {
                    rotateTime -= 1;
                }
            }
            angle = (int) (end - start);
        } else {
            if (end - start < 0) {
                if (mode == TouchMode.MODE_FIRST) {
                    rotateTimeFirst += 1;
                    rotatedFirst += 1;
                } else if (mode == TouchMode.MODE_SECOND) {
                    rotateTime += 1;
                }
            } else {
                if (mode == TouchMode.MODE_FIRST) {
                    rotateTimeFirst -= 1;
                    rotatedFirst -= 1;
                } else if (mode == TouchMode.MODE_SECOND) {
                    rotateTime -= 1;
                }
            }
            angle = (int) (end - start);
        }
//        System.out.println("rotateTimeFirst========" + rotateTimeFirst);
//        System.out.println("rotatedFirst========" + rotatedFirst);
//        System.out.println("rotateTime========" + rotateTime);
//        System.out.println("angle========" + angle);

//        if (moveX - x < 0 && moveY - y > 1) {
//            if (mode == TouchMode.MODE_FIRST) {
//                rotateTimeFirst += 1;
//                rotatedFirst += 1;
//            } else if (mode == TouchMode.MODE_SECOND) {
//                rotateTime += 1;
//            }
//        } else if (moveX - x > 0 && moveY - y < -1) {
//            if (mode == TouchMode.MODE_FIRST) {
//                rotateTimeFirst -= 1;
//                if (rotatedFirst > 1) {
//                    rotatedFirst -= 1;
//                }
//            } else if (mode == TouchMode.MODE_SECOND) {
//                rotateTime -= 1;
//            }
//        }
    }

    public interface FirstContentListener {
        void rotate(String choose);
    }

    public interface SecondContentListener {
        void rotate(String choose);
    }

    public void setFirstContentListener(FirstContentListener firstContentListener) {
        this.firstContentListener = firstContentListener;
    }

    public void setSecondContentListener(SecondContentListener secondContentListener) {
        this.secondContentListener = secondContentListener;
    }

    private enum TouchMode {
        MODE_NORMAL,
        MODE_FIRST,
        MODE_SECOND
    }
}
