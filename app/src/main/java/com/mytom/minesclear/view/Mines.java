package com.mytom.minesclear.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import utils.Utils;

/**
 * Created by yufeiyang on 2018/9/8.
 */

public class Mines {

    public int mapStartPointX; // 地图左上角X开始坐标
    public int mapStartPointY; // 地图左上角Y开始坐标
    public int mapCol; //地图列数
    public int mapRow; //地图行数
    private int mineNum; //地雷数量
    public static short EMPTY = 0;//空
    public static short MINE = -1;//雷
    public BlockRect[][] BlockRect;//地图矩阵
    public int BlockRectWidth;//每一块区域的宽度／高度
    private Paint textPaint;
    private Paint bmpPaint;
    private Paint BlockRectPaint;
    private Paint rectPaint;
    private Paint minePaint;
    private Random rd = new Random();
    public int mapWidth;//地图宽度
    public int mapHeight;//地图高度
    public boolean isDisplayMines = false; //是否显示所有地雷
    // 定义二维数组表示中心点的周围的8个方向
    private int[][] path = {
            {-1, 1}, //左上角
            {0, 1}, //正上
            {1, 1}, //右上角
            {-1, 0}, //正左
            {1, 0}, //正右
            {-1, -1}, //左下角
            {0, -1}, //正下
            {1, -1} //右下角
    };



    public Mines(int x, int y, int mapCol, int mapRow, int mineNum, int BlockRectWidth) {
        this.mapStartPointX = x;
        this.mapStartPointY = y;
        this.mapCol = mapCol;
        this.mapRow = mapRow;
        this.mineNum = mineNum;
        this.BlockRectWidth = BlockRectWidth;
        mapWidth = mapCol * BlockRectWidth;
        mapHeight = mapRow * BlockRectWidth;

        //绘制数字
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(Utils.SCREENWIDTH / 10);
        textPaint.setColor(Color.RED);

        //地雷背景色
        bmpPaint = new Paint();
        bmpPaint.setAntiAlias(true);
        bmpPaint.setColor(Color.DKGRAY);

        //矩阵背景色
        BlockRectPaint = new Paint();
        BlockRectPaint.setAntiAlias(true);
        BlockRectPaint.setColor(0xff1faeff);

        //未知作用
/*
        minePaint = new Paint();
        minePaint.setAntiAlias(true);
        minePaint.setColor(0xffff981d);
*/

        //绘制矩形边框
        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setColor(0xff000000);
        rectPaint.setStyle(Paint.Style.STROKE);

        BlockRect = new BlockRect[mapRow][mapCol];
    }

    /**
     * 初始化地图
     */
    public void init() {
        for (int i = 0; i < mapRow; i++) {
            for (int j = 0; j < mapCol; j++) {
                BlockRect[i][j] = new BlockRect();
                BlockRect[i][j].value = EMPTY;
                BlockRect[i][j].flag = false;
                BlockRect[i][j].open = false;
                isDisplayMines = false;
            }
        }
    }

    /**
     * 生成雷
     *
     * @param touchRect 排除的位置，该位置不生成雷
     */
    public void create(Point touchRect) {
        List<Point> allPoint = new LinkedList<Point>();

        //把所有位置加入链表，此处循环排除除了用户第一次点击时的位置，此处不生成雷，并将其他位置的点全部增加到allpoint
        for (int i = 0; i < mapRow; i++)//mapStartPointY
        {
            for (int j = 0; j < mapCol; j++)//mapStartPointX
            {
                Point point = new Point(j, i);
                if (!point.equals(touchRect)) {
                    allPoint.add(point);
                }
            }
        }

        List<Point> minePoint = new LinkedList<Point>();
        //随机产生雷
        for (int i = 0; i < mineNum; i++) {
            int idx = rd.nextInt(allPoint.size());
            minePoint.add(allPoint.get(idx));
            allPoint.remove(idx);//取了之后，从所有集合中移除
        }

        //在矩阵中标记雷的位置
        for (Iterator<Point> it = minePoint.iterator(); it.hasNext(); ) {
            Point p = it.next();
            BlockRect[p.y][p.x].value = MINE;
        }

        //给地图添加数字
        for (int i = 0; i < mapRow; i++)//mapStartPointY
        {
            for (int j = 0; j < mapCol; j++)//mapStartPointX
            {
                short t = BlockRect[i][j].value;
                if (t == MINE) {
                    for (int k = 0; k < 8; k++) {
                        int offsetX = j + path[k][0], offsetY = i + path[k][1];
                        if (offsetX >= 0 && offsetX < mapCol && offsetY >= 0 && offsetY < mapRow) {
                            if (BlockRect[offsetY][offsetX].value != -1)
                                BlockRect[offsetY][offsetX].value += 1;
                        }
                    }
                }
            }
        }

    }


    /**
     * 打开某个位置
     *
     * @param touchRect 打开的区域块
     * @param isFirst 标记是否是第一次打开
     */

    public void open(Point touchRect, boolean isFirst) {
        if (isFirst) {
            create(touchRect);
        }

        BlockRect[touchRect.y][touchRect.x].open = true;
        if (BlockRect[touchRect.y][touchRect.x].value == -1)
            return;
        else if (BlockRect[touchRect.y][touchRect.x].value > 0)//点中数字块
        {
            return;
        }

        //广度优先遍历用队列
        Queue<Point> qu = new LinkedList<Point>();
        //加入第一个点
        qu.offer(new Point(touchRect.x, touchRect.y));

        //朝8个方向遍历
        for (int i = 0; i < 8; i++) {
            int offsetX = touchRect.x + path[i][0], offsetY = touchRect.y + path[i][1];
            //判断越界和是否已访问
            boolean isCan = offsetX >= 0 && offsetX < mapCol && offsetY >= 0 && offsetY < mapRow;
            if (isCan) {
                if (BlockRect[offsetY][offsetX].value == 0 && !BlockRect[offsetY][offsetX].open) {
                    qu.offer(new Point(offsetX, offsetY));
                } else if (BlockRect[offsetY][offsetX].value > 0) {
                    BlockRect[offsetY][offsetX].open = true;
                }
            }

        }

        while (qu.size() != 0) {
            Point p = qu.poll();
            BlockRect[p.y][p.x].open = true;
            for (int i = 0; i < 8; i++) {
                int offsetX = p.x + path[i][0], offsetY = p.y + path[i][1];
                //判断越界和是否已访问
                boolean isCan = offsetX >= 0 && offsetX < mapCol && offsetY >= 0 && offsetY < mapRow;
                if (isCan) {
                    if (BlockRect[offsetY][offsetX].value == 0 && !BlockRect[offsetY][offsetX].open) {
                        qu.offer(new Point(offsetX, offsetY));
                    } else if (BlockRect[offsetY][offsetX].value > 0) {
                        BlockRect[offsetY][offsetX].open = true;
                    }
                }

            }
        }

    }

    /**
     * 绘制地图
     *
     * @param canvas
     */
    public void draw(Canvas canvas) {


        for (int i = 0; i < mapRow; i++) {
            for (int j = 0; j < mapCol; j++) {
                BlockRect t = BlockRect[i][j];
                if (t.open) {
                    if (t.value > 0) {
                        canvas.drawText(t.value + "", mapStartPointX + j * BlockRectWidth, mapStartPointY + i * BlockRectWidth + BlockRectWidth, textPaint);
                    }

                } else {
                    //标记，备用
                    if (t.flag) {

                    } else {
                        //画矩形方块
                        RectF reactF = new RectF(mapStartPointX + j * BlockRectWidth, mapStartPointY + i * BlockRectWidth, mapStartPointX + j * BlockRectWidth + BlockRectWidth, mapStartPointY + i * BlockRectWidth + BlockRectWidth);
                        canvas.drawRoundRect(reactF, 0, 0, BlockRectPaint);
                    }
                }
                //是否画出所有雷
                if (isDisplayMines && BlockRect[i][j].value == -1) {
                    canvas.drawCircle((mapStartPointX + j * BlockRectWidth) + BlockRectWidth / 2, (mapStartPointY + i * BlockRectWidth) + BlockRectWidth / 2, BlockRectWidth / 2, bmpPaint);
                }
            }
        }

        //画边框
        canvas.drawRect(mapStartPointX, mapStartPointY, mapStartPointX + mapWidth, mapStartPointY + mapHeight, rectPaint);
        //画横线
        for (int i = 0; i < mapRow; i++) {
            canvas.drawLine(mapStartPointX, mapStartPointY + i * BlockRectWidth, mapStartPointX + mapWidth, mapStartPointY + i * BlockRectWidth, rectPaint);
        }
        //画竖线
        for (int i = 0; i < mapCol; i++) {
            canvas.drawLine(mapStartPointX + i * BlockRectWidth, mapStartPointY, mapStartPointX + i * BlockRectWidth, mapStartPointY + mapHeight, rectPaint);
        }

    }

}
