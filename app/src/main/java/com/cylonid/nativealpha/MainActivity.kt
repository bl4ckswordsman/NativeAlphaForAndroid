package com.cylonid.nativealpha

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Switch
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.cylonid.nativealpha.fragments.webapplist.WebAppListFragment
import com.cylonid.nativealpha.model.DataManager
import com.cylonid.nativealpha.model.WebApp
import com.cylonid.nativealpha.util.Const
import com.cylonid.nativealpha.util.EntryPointUtils.entryPointReached
import com.cylonid.nativealpha.util.Utility
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : EdgeToEdgeActivity() {
    private var webAppListFragment: WebAppListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webAppListFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as WebAppListFragment?
        entryPointReached(this)

        if (DataManager.getInstance().websites.size == 0) {
            buildAddWebsiteDialog(getString(R.string.welcome_msg))
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { buildAddWebsiteDialog(getString(R.string.add_webapp)) }
        personalizeToolbar()

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra(Const.INTENT_BACKUP_RESTORED, false)) {
            updateWebAppList()

            buildImportSuccessDialog()
            intent.putExtra(Const.INTENT_BACKUP_RESTORED, false)
            intent.putExtra(Const.INTENT_REFRESH_NEW_THEME, false)
        }
        if (intent.getBooleanExtra(Const.INTENT_WEBAPP_CHANGED, false)) {
            updateWebAppList()
            intent.putExtra(Const.INTENT_WEBAPP_CHANGED, false)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun updateWebAppList() {
        webAppListFragment!!.updateWebAppList()
    }

    private fun personalizeToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setLogo(R.mipmap.native_alpha_white)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            @StringRes val appName =
                if (BuildConfig.FLAVOR != "extended") R.string.app_name else R.string.app_name_plus
            setTitle(appName)
            setDisplayShowTitleEnabled(true)
            setDisplayUseLogoEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun buildImportSuccessDialog() {
        val builder = AlertDialog.Builder(this)

        val message = """
            ${getString(R.string.import_success_dialog_txt2)}

            ${getString(R.string.import_success_dialog_txt3)}
            """.trimIndent()

        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setTitle(
            getString(
                R.string.import_success,
                DataManager.getInstance().activeWebsitesCount
            )
        )
        builder.setPositiveButton(getString(android.R.string.ok)) { _: DialogInterface?, _: Int ->
            val webapps = DataManager.getInstance().activeWebsites
            for (i in webapps.indices.reversed()) {
                val webapp = webapps[i]
                i == webapps.size - 1
                val msg = Html.fromHtml(
                    getString(R.string.restore_shortcut, webapp.title),
                    Html.FROM_HTML_MODE_COMPACT
                )
                val addition_dialog = AlertDialog.Builder(this)
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int ->
                        val frag = ShortcutDialogFragment.newInstance(webapp)
                        frag.show(supportFragmentManager, "SCFetcher-" + webapp.ID)
                    }
                    .setNegativeButton(android.R.string.cancel) { _: DialogInterface?, _: Int -> }
                    .create()

                addition_dialog.show()
            }
        }
        builder.setNegativeButton(getString(android.R.string.cancel)) { _: DialogInterface?, _: Int -> }
        builder.create().show()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            return true
        }
        if (id == R.id.action_about) {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun buildAddWebsiteDialog(title: String) {
        val inflated_view = layoutInflater.inflate(R.layout.add_website_dialogue, null)
        val url = inflated_view.findViewById<EditText>(R.id.websiteUrl)
        val create_shortcut = inflated_view.findViewById<Switch>(R.id.switchCreateShortcut)

        val dialog = AlertDialog.Builder(this@MainActivity)
            .setView(inflated_view)
            .setTitle(title)
            .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
            .setNegativeButton(android.R.string.cancel, null)
            .create()

        dialog.setOnShowListener { dialogInterface: DialogInterface? ->
            val positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            url.requestFocus()
            positive.setOnClickListener { view: View? ->
                var str_url = url.text.toString().trim { it <= ' ' }
                if (str_url == "") {
                    Utility.showInfoSnackbar(
                        this,
                        getString(R.string.no_url_entered),
                        Snackbar.LENGTH_SHORT
                    )
                } else {
                    if (!(str_url.startsWith("https://")) && !(str_url.startsWith("http://"))) {
                        str_url = "https://$str_url"
                    }
                    val newSite = WebApp(str_url, DataManager.getInstance().incrementedID, DataManager.getInstance().incrementedOrder)
                    newSite.applySettingsForNewWebApp()
                    DataManager.getInstance().addWebsite(newSite)

                    updateWebAppList()
                    dialog.dismiss()
                    if (create_shortcut.isChecked) {
                        val frag = ShortcutDialogFragment.newInstance(newSite)
                        frag.show(supportFragmentManager, "SCFetcher-" + newSite.ID)
                    }
                }
            }
        }
        dialog.show()
    }
}
