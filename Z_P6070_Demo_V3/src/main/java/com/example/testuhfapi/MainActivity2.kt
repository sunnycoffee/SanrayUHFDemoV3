package com.example.testuhfapi

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent

class MainActivity2 : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        UHFHelper.onScanCallback = {
            Log.d("test", it)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == 139 || keyCode == 280 || keyCode == 24) { // 139/280 scan button for p6070//keyCode ==
            Log.e("test", "--------------------")
            if (UHFHelper.isScan) UHFHelper.stop()
            else UHFHelper.start()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }
}