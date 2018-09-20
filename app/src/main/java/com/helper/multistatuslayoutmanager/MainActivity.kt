package com.helper.multistatuslayoutmanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.helper.manager.MultiStatusLayoutManager
import com.helper.manager.SwitchStatusLayoutManager

class MainActivity : AppCompatActivity() {

    private var m: SwitchStatusLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //测试单例
        MultiStatusLayoutManager.Builder()
                .addNoNetWorkClickListener({
                    m!!.showLoadingView()
                    Handler().postDelayed({
                        m!!.showContentView()
                    }, 2000)
                })
                .addErrorClickListener { Toast.makeText(this, "错误页面点击", Toast.LENGTH_SHORT).show() }
                .setLoadingView(R.layout.layout_loading)
                .commit()
        m = MultiStatusLayoutManager.getInstance().regist(this)
        m!!.showLoadingView()
        Handler().postDelayed({
            m!!.showContentView()
        }, 2000)

//        ---------------------------

        //测试普通对象
//        m = MultiStatusLayoutManager.Builder().create(rootView);
//        m!!.showLoadingView()
//        Handler().postDelayed({
//            m!!.showContentView()
//        }, 2000)
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
                startActivity(Intent(this, TestFragmentActivity::class.java))
            }
            R.id.list_test_id -> {
                startActivity(Intent(this, TestListActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        m!!.release()
    }


}
