package com.mytom.minesclear;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.mytom.minesclear.view.CustomMinesView;

import utils.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取屏幕尺寸
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Utils.SCREENWIDTH = dm.widthPixels;
        Utils.SCREENHEIGHT = dm.heightPixels;

        setContentView(new CustomMinesView(this));
        //显示闯关通知
        displayDialogInfo();

    }
    private void displayDialogInfo() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(getString(R.string.gameinfotitles))
                .setMessage(getString(R.string.gameinfomsg))
                .setPositiveButton(getString(R.string.gamebegin), null)
                .create()
                .show();
    }
}
