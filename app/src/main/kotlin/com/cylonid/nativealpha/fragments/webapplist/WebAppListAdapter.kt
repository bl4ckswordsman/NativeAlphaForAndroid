package com.cylonid.nativealpha.fragments.webapplist

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.cylonid.nativealpha.R
import com.cylonid.nativealpha.WebAppSettingsActivity
import com.cylonid.nativealpha.model.DataManager
import com.cylonid.nativealpha.model.WebApp
import com.cylonid.nativealpha.util.Const
import com.cylonid.nativealpha.util.WebViewLauncher.startWebView
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter

class WebAppListAdapter(
    dataSet: List<WebApp> = emptyList(),
    private val activityOfFragment: Activity
) : DragDropSwipeAdapter<WebApp, WebAppListAdapter.ViewHolder>(dataSet) {

    class ViewHolder(webAppLayout: View) : DragDropSwipeAdapter.ViewHolder(webAppLayout) {

        val titleView: TextView = itemView.findViewById(R.id.btn_title)
        val openButton: ImageButton = itemView.findViewById(R.id.btnOpenWebview)
        val settingsButton: ImageButton = itemView.findViewById(R.id.btnSettings)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)
        val dragAnchor: ImageView = itemView.findViewById(R.id.dragAnchor)
    }

    override fun getViewHolder(itemView: View) = ViewHolder(itemView)
    override fun onBindViewHolder(item: WebApp, viewHolder: ViewHolder, position: Int) {
        viewHolder.titleView.text = item.title
        viewHolder.openButton.setOnClickListener {
            openWebView(
                item
            )
        }

        viewHolder.settingsButton.setOnClickListener {
            val intent = Intent(
                activityOfFragment,
                WebAppSettingsActivity::class.java
            )
            intent.putExtra(Const.INTENT_WEBAPPID, item.ID)
            intent.action = Intent.ACTION_VIEW
            startActivity(activityOfFragment, intent, null)
        }

        viewHolder.deleteButton.setOnClickListener {
            buildDeleteItemDialog(
                item.ID
            )
        }

    }

    fun updateWebAppList() {
        dataSet = DataManager.getInstance().activeWebsites
    }


    private fun buildDeleteItemDialog(ID: Int) {
        val builder = AlertDialog.Builder(activityOfFragment)
        builder.setMessage(getString(R.string.delete_question))
        builder.setPositiveButton(getString(android.R.string.ok)) { _: DialogInterface?, _: Int ->
            val webapp = DataManager.getInstance().getWebApp(ID)
            if (webapp != null) {
                webapp.markInactive()
                DataManager.getInstance().saveWebAppData()
            }
            updateWebAppList()
        }
        builder.setNegativeButton(
            getString(android.R.string.cancel)
        ) { dialog: DialogInterface, _: Int -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun openWebView(webapp: WebApp) {
        startWebView(webapp, activityOfFragment)
    }


    private fun getString(@StringRes resId: Int): String {
        return activityOfFragment.getString(resId)

    }

    override fun getViewToTouchToStartDraggingItem(
        item: WebApp,
        viewHolder: ViewHolder,
        position: Int
    ) = viewHolder.dragAnchor

}