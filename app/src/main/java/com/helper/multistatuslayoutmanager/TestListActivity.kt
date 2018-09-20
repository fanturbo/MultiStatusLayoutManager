package com.helper.multistatuslayoutmanager

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.helper.manager.MultiStatusLayoutManager
import com.helper.manager.SwitchStatusLayoutManager
import kotlinx.android.synthetic.main.activity_test_list.*

class TestListActivity : AppCompatActivity() {

    private var m: SwitchStatusLayoutManager? = null
    var list: ArrayList<String>? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_list)
        recyclerview.layoutManager = LinearLayoutManager(this);
        m = MultiStatusLayoutManager.Builder().create(this)
        m!!.showLoadingView()
        Handler().postDelayed({
            setData()
            m!!.showContentView()
        }, 2000)
    }

    private fun setData() {
        list = ArrayList<String>();
        list?.add("test1")
        list?.add("test2")
        list?.add("test3")
        list?.add("test4")
        recyclerview.adapter = object : Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val adapter = object : RecyclerView.ViewHolder(LayoutInflater.from(this@TestListActivity).inflate(android.R.layout.test_list_item, null)) {}
                return adapter
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                holder.itemView.findViewById<TextView>(android.R.id.text1).setText(list!![position])
            }

            override fun getItemCount(): Int {
                return list!!.size
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        m!!.release()
    }
}