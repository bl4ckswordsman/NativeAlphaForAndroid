package com.cylonid.nativealpha

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

abstract class EdgeToEdgeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupEdgeToEdge()
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Handle AppBarLayout if present
            findViewById<AppBarLayout?>(com.cylonid.nativealpha.R.id.appBarLayout)?.let { appBar ->
                appBar.updatePadding(top = systemBars.top)

                // Handle Toolbar if present
                findViewById<Toolbar?>(com.cylonid.nativealpha.R.id.toolbar)?.apply {
                    layoutParams = (layoutParams as AppBarLayout.LayoutParams).apply {
                        height = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
                            .use { it.getDimensionPixelSize(0, 0) }
                    }
                }
            }

            // Handle FAB if present
            findViewById<FloatingActionButton?>(com.cylonid.nativealpha.R.id.fab)?.let { fab ->
                fab.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = systemBars.bottom + resources.getDimensionPixelSize(com.cylonid.nativealpha.R.dimen.fab_margin)
                    rightMargin = systemBars.right + resources.getDimensionPixelSize(com.cylonid.nativealpha.R.dimen.fab_margin)
                }
            }

            // Handle standard content padding if no specific handlers above
            if (findViewById<AppBarLayout?>(com.cylonid.nativealpha.R.id.appBarLayout) == null) {
                view.updatePadding(
                    left = systemBars.left,
                    top = systemBars.top,
                    right = systemBars.right,
                    bottom = systemBars.bottom
                )
            }

            windowInsets
        }
    }
}
