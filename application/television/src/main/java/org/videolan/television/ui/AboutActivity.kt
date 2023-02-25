package org.videolan.television.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import org.videolan.resources.util.applyOverscanMargin
import org.videolan.television.R
import org.videolan.vlc.gui.helpers.UiTools

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about)
        UiTools.fillAboutView(this, window.decorView.rootView)
        applyOverscanMargin(this)
        this.registerTimeView(findViewById(R.id.tv_time))
    }
}
