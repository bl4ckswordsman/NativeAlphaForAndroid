package com.cylonid.nativealpha.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cylonid.nativealpha.BuildConfig
import com.cylonid.nativealpha.R
import com.cylonid.nativealpha.databinding.NewsActivityBinding
import com.cylonid.nativealpha.model.DataManager
import com.cylonid.nativealpha.util.LocaleUtils
import com.cylonid.nativealpha.util.Utility


class NewsActivity : AppCompatActivity(), View.OnTouchListener,
    ViewTreeObserver.OnScrollChangedListener {

    private var btnDefaultBackgroundColor: Drawable? = null
    private var btnDefaultTextColor: Int = android.R.color.black

    private lateinit var binding: NewsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_activity)
        binding = NewsActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initializeUI()
        setButtonState()
    }


    override fun onBackPressed() {}

    @SuppressLint("ResourceAsColor")
    private fun disableAcceptButton() {
        binding.btnNewsConfirm.isActivated = false
        btnDefaultTextColor = binding.btnNewsConfirm.currentTextColor
        btnDefaultBackgroundColor = binding.btnNewsConfirm.background

        binding.btnNewsConfirm.setBackgroundColor(
            ContextCompat.getColor(
                baseContext,
                R.color.disabled_background_color
            )
        )
        binding.btnNewsConfirm.setTextColor(
            ContextCompat.getColor(
                baseContext,
                R.color.disabled_text_color
            )
        )
        binding.btnNewsConfirm.setOnClickListener {
            Utility.showToast(
                this,
                getString(R.string.scroll_to_bottom),
                Toast.LENGTH_SHORT
            )
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun enableAcceptButton() {
        binding.btnNewsConfirm.isActivated = true
        binding.btnNewsConfirm.setTextColor(btnDefaultTextColor)
        binding.btnNewsConfirm.background = btnDefaultBackgroundColor

        binding.btnNewsConfirm.setOnClickListener { confirm() }
    }

    private fun initializeUI() {
        setText()
        binding.btnNewsConfirm.setOnClickListener {
            confirm()
        }
    }

    private fun setButtonState() {
        val vto: ViewTreeObserver = binding.newsScrollchild.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val height: Int = binding.newsScrollchild.measuredHeight
                if (height > 0) {
                    binding.newsScrollchild.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    if (binding.newsScrollchild.canScrollVertically(1) || binding.newsScrollview.canScrollVertically(
                            -1
                        )
                    ) {
                        binding.newsScrollview.setOnTouchListener(this@NewsActivity)
                        binding.newsScrollview.viewTreeObserver.addOnScrollChangedListener(this@NewsActivity)
                        disableAcceptButton()
                    }
                }
            }
        })
    }

    private fun setText() {
        val fileId = intent.extras?.getString("text") ?: "latestUpdate"

        binding.newsContent.loadUrl("file:///android_asset/news/" + fileId + "_" + LocaleUtils.fileEnding + ".html")
        if (DataManager.getInstance().eulaData) {
            binding.btnNewsConfirm.isEnabled = true
            binding.newsContent.settings.javaScriptEnabled = true
            binding.newsContent.webViewClient = NewsWebViewClient()
        }
    }

    private fun confirm() {
        DataManager.getInstance().eulaData = true
        DataManager.getInstance().lastShownUpdate = BuildConfig.VERSION_CODE
        finish()
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }

    override fun onScrollChanged() {
        val view = binding.newsScrollview.getChildAt(binding.newsScrollview.childCount - 1)
        val bottomDetector: Int =
            view.bottom - (binding.newsScrollview.height + binding.newsScrollview.scrollY)

        if (bottomDetector < 30) {
            enableAcceptButton()
        }
    }
}

private class NewsWebViewClient : WebViewClient() {
    override fun onPageFinished(view: WebView, url: String) {
        view.evaluateJavascript("hideById('eula')", null)
        view.settings.javaScriptEnabled = false
    }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        view.context.startActivity(Intent(Intent.ACTION_VIEW, request.url))
        return true
    }

}