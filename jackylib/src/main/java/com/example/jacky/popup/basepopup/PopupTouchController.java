package com.example.jacky.popup.basepopup;

import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * Created by Jacky on 2017/1/13.
 */

interface PopupTouchController {


    boolean onBeforeDismiss();

    boolean callDismissAtOnce();

    boolean onDispatchKeyEvent(KeyEvent event);

    boolean onTouchEvent(MotionEvent event);

    boolean onBackPressed();

    boolean onOutSideTouch();


}
