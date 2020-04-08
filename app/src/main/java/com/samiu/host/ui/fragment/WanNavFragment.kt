package com.samiu.host.ui.fragment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.jeremyliao.liveeventbus.LiveEventBus
import com.samiu.base.interactive.ZoomOutPageTransformer
import com.samiu.base.ui.BaseFragment
import com.samiu.base.ui.viewBinding
import com.samiu.host.R
import com.samiu.host.databinding.FragmentWanNavBinding
import com.samiu.host.global.*
import kotlinx.android.synthetic.main.fragment_wan_nav.*

/**
 * @author Samiu 2020/3/2
 */
class WanNavFragment : BaseFragment(R.layout.fragment_wan_nav) {
    private val binding by viewBinding(FragmentWanNavBinding::bind)
    override fun initData() = Unit

    private val homeFragment by lazy { WanHomeFragment() }
    private val squareFragment by lazy { WanSquareFragment() }
    private val recentProjectFragment by lazy { WanRecentProjectFragment() }
    private val systemFragment by lazy { WanSystemFragment() }
    private val wxArticleFragment by lazy { WanWxArticleFragment() }
    private val fragmentList = ArrayList<Fragment>()
    private val titleList = arrayOf(
        HOME_PAGE, SQUARE, RECENT_PROJECT, SYSTEM, WX_ARTICLE, NAVIGATION
    )
    private var currentTitle = titleList[0]

    init {
        fragmentList.add(homeFragment)
        fragmentList.add(squareFragment)
        fragmentList.add(recentProjectFragment)
        fragmentList.add(systemFragment)
        fragmentList.add(wxArticleFragment)
    }

    override fun initView() {
        //viewPager2
        wan_nav_pager.adapter = ScreenPagerAdapter(this)
        wan_nav_pager.setPageTransformer(ZoomOutPageTransformer())
        wan_nav_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentTitle = titleList[position]
                wan_nav_refresh_layout.isEnabled = ((position != 3) && (position != 4))
            }
        })
        //tabLayout
        TabLayoutMediator(wan_nav_tab, wan_nav_pager) { tab, position ->
            tab.text = titleList[position]
        }.attach()
        //smartRefreshLayout
        with(wan_nav_refresh_layout) {
            setOnRefreshListener {
                LiveEventBus
                    .get(currentTitle, Int::class.java)
                    .post(REFRESH)
                finishRefresh(1500)
            }
            setOnLoadMoreListener {
                LiveEventBus
                    .get(currentTitle, Int::class.java)
                    .post(LOAD_MORE)
                finishLoadMore(2000)
            }
        }
    }

    private inner class ScreenPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount() = fragmentList.size
        override fun createFragment(position: Int) = fragmentList[position]
    }
}