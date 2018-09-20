package com.helper.multistatuslayoutmanager

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.*
import com.helper.manager.MultiStatusLayoutManager
import com.helper.manager.SwitchStatusLayoutManager
import kotlinx.android.synthetic.main.fragment_test.*

class TestFragment : Fragment() {

    private var m: SwitchStatusLayoutManager? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.fragment_test, container, false)
        return inflate
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //测试单例
//        MultiStatusLayoutManager.Builder().setLoadingView(R.layout.layout_loading).commit()
//        m = MultiStatusLayoutManager.getInstance().regist(this)
//        m!!.showLoadingView()
//        Handler().postDelayed({
//            m!!.showContentView()
//        }, 2000)


//        ---------------------------

        //测试普通对象
        m = MultiStatusLayoutManager.Builder().create(this)
        m!!.showLoadingView()
        Handler().postDelayed({
            m!!.showContentView()
        }, 2000)
    }

    companion object {
        fun newInstance(): TestFragment {
            val fragment = TestFragment()
            return fragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        m!!.release()
    }
}
