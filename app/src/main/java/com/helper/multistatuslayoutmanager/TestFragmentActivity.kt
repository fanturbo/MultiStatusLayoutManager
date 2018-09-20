package com.helper.multistatuslayoutmanager

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu

class TestFragmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_fragment)
        supportFragmentManager.beginTransaction().replace(R.id.frame, TestFragment.newInstance()).commit()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}