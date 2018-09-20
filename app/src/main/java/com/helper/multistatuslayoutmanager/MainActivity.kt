package com.helper.multistatuslayoutmanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.helper.manager.MultiStatusLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var m: MultiStatusLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        m = MultiStatusLayoutManager.Builder(rootView).create();
        m!!.showLoadingView()
        Handler().postDelayed({
            m!!.showContentView()
        }, 2000)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_multi_status_layout_demo, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.loading_status_id -> m!!.showLoadingView()
            R.id.no_network_id -> m!!.showNoNetWorkView()
            R.id.error_view_id -> m!!.showErrorView()
            R.id.empty_view_id -> m!!.showEmptyView()
            R.id.content_view_id -> m!!.showContentView()
            R.id.fragment_test_id -> {
                startActivity(Intent(this,TestFragmentActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        m!!.release()
    }


}
