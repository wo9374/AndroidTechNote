package com.example.androidtechnote.recycler.paging3.basicforloop

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityBasicPagingBinding
import com.example.androidtechnote.recycler.paging3.SpaceItemSpaceDeco
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class PgBasicActivity : AppCompatActivity() {
    private lateinit var viewModelPg : PgBasicViewModel
    private lateinit var pagingAdapter: PgBasicAdapter

    private lateinit var binding: ActivityBasicPagingBinding

    private lateinit var centerZoomManager: LinearLayoutManager //LayoutManager 사용 중앙 포커싱 아이템 확대

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_basic_paging)

        pagingAdapter = PgBasicAdapter()
        pagingAdapter.setOnItemClick(object : PgBasicAdapter.ItemClickListener{
            override fun onClick(v: View, position: Int) {
                //centerZoomManager.scrollToPositionWithOffset(position, if (orientation == LinearLayoutManager.HORIZONTAL) v.width/2 else v.height/2)
            }
        })

        //val orientation = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) LinearLayoutManager.HORIZONTAL else LinearLayoutManager.VERTICAL
        //centerZoomManager = CenterFocusedZoomManager(this, orientation)

        binding.recyclerView.apply {
            adapter = pagingAdapter
            //layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

            layoutManager = GridLayoutManager(applicationContext, 4) // centerZoomManager

            val metrics = context.resources.displayMetrics
            val dp5 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5F, metrics).toInt()
            addItemDecoration(SpaceItemSpaceDeco(dp5, true))
        }

        val service = PgForLoopService()

        viewModelPg = PgBasicViewModel(PgBasicRepository(service))

        lifecycleScope.launch {
            viewModelPg.pagingData.collectLatest {
                pagingAdapter.submitData(it)
            }
        }
    }
}


