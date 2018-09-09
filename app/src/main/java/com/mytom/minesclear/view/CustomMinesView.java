package com.mytom.minesclear.view;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import com.mytom.minesclear.R;

import utils.Utils;

/**
 * Created by yufeiyang on 2018/9/8.
 */

public class CustomMinesView extends View {
    private static final String TAG = "CustomMinesView";
    private Mines mine;
    private boolean isFirstIn = true;//标记是否是本局第一次点击屏幕
    private Context context;
    private final int mineNum = 10;//产生的雷的个数
    private final int ROW = 10;//要生成的矩阵高
    private final int COL = 8;//要生成的矩阵宽
    private int TILE_WIDTH = 50;//块大小
    private boolean isFalse = false;
    private long getCurrentTime = 0l;

    public CustomMinesView(Context context) {
        super(context);
        this.context = context;

        TILE_WIDTH = Utils.SCREENWIDTH / 10; //块大小为屏幕宽度的 1／10
        mine = new Mines((Utils.SCREENWIDTH - COL * TILE_WIDTH) / 2, (Utils.SCREENHEIGHT - ROW * TILE_WIDTH) / 2, COL, ROW, mineNum, TILE_WIDTH);
        try {
            mine.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 游戏逻辑
     */
    public void logic() {
        int count = 0;

        for (int i = 0; i < mine.mapRow; i++) {
            for (int j = 0; j < mine.mapCol; j++) {
                if (!mine.BlockRect[i][j].open) {
                    count++;
                }
            }
        }
        //逻辑判断是否胜利
        if (count == mineNum) {
            new AlertDialog.Builder(context)
                    .setMessage("恭喜你，你找出了所有雷")
                    .setCancelable(false)
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mine.init();
                            invalidate();
                            isFirstIn = true;
                        }
                    })
                    .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .create()
                    .show();
        }
    }


    /**
     * 刷新View
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        mine.draw(canvas);
    }

    /**
     * 点击屏幕事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getCurrentTime = System.currentTimeMillis();
                Log.d(TAG,"ontouchEvent  action_down-- > "+ getCurrentTime);
                break;
            case MotionEvent.ACTION_MOVE:
                getCurrentTime = 0l;
                Log.d(TAG,"ontouchEvent  action_move-- > "+ getCurrentTime);
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG,"ontouchEvent  action_up-- > "+ (getCurrentTime - System.currentTimeMillis()));

                if (getCurrentTime - System.currentTimeMillis() > 500) { //触发长按

                } else if (getCurrentTime == 0){ //触发取消

                } else { //触发点击事件
                    return singleTap(event);
                }
                break;
        }

        return true;
    }

    /**
     *
     * @param event
     * @return
     */
    private boolean singleTap(MotionEvent event) {
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();
        //判断触摸点是否在地图区域
        if (touchX >= mine.mapStartPointX && touchY >= mine.mapStartPointY && touchX <= (mine.mapWidth + mine.mapStartPointX) && touchY <= (mine.mapStartPointY + mine.mapHeight)) {
            int idxX = (touchX - mine.mapStartPointX) / mine.BlockRectWidth;
            int idxY = (touchY - mine.mapStartPointY) / mine.BlockRectWidth;
            mine.open(new Point(idxX, idxY), isFirstIn);
            isFirstIn = false;

            if (mine.BlockRect[idxY][idxX].value == -1) {
                mine.isDisplayMines = true;
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setMessage(context.getString(R.string.fail))
                        .setPositiveButton(context.getString(R.string.next), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mine.init();
                                isFalse = true;
                                isFirstIn = true;

                                invalidate();
                            }
                        })
                        .setNegativeButton(context.getString(R.string.exit), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                            }
                        })
                        .create()
                        .show();
            }
            if (isFalse) {
                isFalse = false;
                invalidate();
                return true;
            }
            logic();
            invalidate();
        }
        return false;
    }
}
