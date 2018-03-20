package com.witcher.testcamera2;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by witcher on 2018/3/16 0016.
 */

public class L {
    public static void i(String content){
        Log.i("witcher",content);
    }
    public static void t(Context ctx,String content){
        Toast.makeText(ctx,content,Toast.LENGTH_SHORT).show();
    }
}
