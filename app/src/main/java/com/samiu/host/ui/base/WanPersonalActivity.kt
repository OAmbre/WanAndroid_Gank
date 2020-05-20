package com.samiu.host.ui.base

import android.content.Intent
import androidx.lifecycle.Observer
import com.samiu.base.ui.BaseActivity
import com.samiu.base.ui.viewBinding
import com.samiu.host.databinding.ActivityWanPersonalBinding
import com.samiu.host.global.TITLE
import com.samiu.host.global.URL
import com.samiu.host.global.USER_NAME
import com.samiu.host.ui.adapter.WanArticleAdapter
import com.samiu.host.util.Preference
import com.samiu.host.viewmodel.WanPersonalViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

/**
 * @author Samiu 2020/5/16
 */
class WanPersonalActivity : BaseActivity() {

    private val mBinding by viewBinding(ActivityWanPersonalBinding::inflate)
    private val viewModel: WanPersonalViewModel by viewModel()
    override fun getBindingRoot() = mBinding.root

    private val userName: String by Preference(USER_NAME, "")
    private var mCurrentPage by Delegates.notNull<Int>()
    private lateinit var mAdapter: WanArticleAdapter

    override fun initView() {
        mBinding.toolbar.setNavigationOnClickListener { finish() }
        mBinding.nickname.text = userName
        initRecyclerView()
        with(mBinding.refreshLayout) {
            setOnRefreshListener {
                mCurrentPage = 0
                mAdapter.clearAll()
                viewModel.getCollections(mCurrentPage)
                finishRefresh(1000)
            }
            setOnLoadMoreListener {
                mCurrentPage += 1
                viewModel.getCollections(mCurrentPage)
                finishLoadMore(1000)
            }
        }
    }

    override fun initData() {
        mBinding.refreshLayout.autoRefresh()
    }

    override fun startObserve() = viewModel.run {
        mCollections.observe(this@WanPersonalActivity, Observer { mAdapter.addAll(it) })
    }

    private fun initRecyclerView() {
        mAdapter = WanArticleAdapter(this)
        mBinding.recycler.adapter = mAdapter
        mAdapter.setOnItemClick {
            val intent = Intent(this, BrowserActivity::class.java).apply {
                putExtra(URL, it)
            }
            startActivity(intent)
        }
    }
}