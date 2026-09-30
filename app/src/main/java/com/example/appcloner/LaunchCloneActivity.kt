package com.example.appcloner

import android.app.Activity
import android.os.Bundle

class LaunchCloneActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pkg = intent.getStringExtra("pkg")
        val cloneId = intent.getStringExtra("cloneId") ?: "0"
        if (pkg != null) Engine.impl.launch(this, pkg, cloneId)
        finish()
    }
}
