package com.example.appcloner

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.drawable.Icon

object ShortcutHelper {
    fun pin(context: Context, pkg: String, cloneId: String, label: String, icon: Bitmap): Boolean {
        val sm = context.getSystemService(ShortcutManager::class.java)
        if (sm == null || !sm.isRequestPinShortcutSupported) return false

        val intent = Intent(context, LaunchCloneActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra("pkg", pkg)
            putExtra("cloneId", cloneId)
        }
        val info = ShortcutInfo.Builder(context, "clone_$cloneId")
            .setShortLabel(label)
            .setLongLabel(label)
            .setIcon(Icon.createWithBitmap(icon))
            .setIntent(intent)
            .build()
        return sm.requestPinShortcut(info, null)
    }
}
