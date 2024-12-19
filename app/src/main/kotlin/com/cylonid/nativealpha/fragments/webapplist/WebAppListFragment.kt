package com.cylonid.nativealpha.fragments.webapplist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cylonid.nativealpha.R
import com.cylonid.nativealpha.model.DataManager
import com.cylonid.nativealpha.model.WebApp
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemDragListener

class WebAppListFragment : Fragment(R.layout.fragment_web_app_list) {
    private lateinit var adapter: WebAppListAdapter

    private lateinit var list: DragDropSwipeRecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WebAppListAdapter(DataManager.getInstance().activeWebsites, requiredActivity())

        list = view.findViewById(R.id.web_app_list)
        list.layoutManager = LinearLayoutManager(requiredActivity())
        list.adapter = adapter
        list.orientation = DragDropSwipeRecyclerView.ListOrientation.VERTICAL_LIST_WITH_VERTICAL_DRAGGING
        list.dragListener = onItemDragListener
        list.disableSwipeDirection(DragDropSwipeRecyclerView.ListOrientation.DirectionFlag.RIGHT)
        list.disableSwipeDirection(DragDropSwipeRecyclerView.ListOrientation.DirectionFlag.LEFT)
    }

    fun updateWebAppList() {
        adapter.updateWebAppList()
    }

    private fun requiredActivity(): FragmentActivity {
        return requireNotNull(activity) { "WebAppListFragment is not attached to an activity." }
    }

    private val onItemDragListener = object : OnItemDragListener<WebApp> {

        override fun onItemDropped(initialPosition: Int, finalPosition: Int, item: WebApp) {
            for ((i, webapp) in adapter.dataSet.withIndex()) {
                // Do not use "i" as index here, since adapter.dataSet includes only active website.
                // The DataManager's websites array contains both active and inactive websites.
                DataManager.getInstance().websites[webapp.ID].order = i
            }
            DataManager.getInstance().saveWebAppData()

        }

        override fun onItemDragged(previousPosition: Int, newPosition: Int, item: WebApp) {
        }
    }
}