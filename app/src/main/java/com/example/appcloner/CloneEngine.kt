package com.example.appcloner

import android.content.Context
import android.widget.Toast

/**
 * The single place where "cloning" actually happens.
 *
 * DefaultEngine below does NOT isolate anything: it just launches the original app
 * through your renamed/re-iconed shortcut. That makes the whole UI flow work out of the box.
 *
 * For a REAL independent copy (separate data/login), implement this interface with a
 * virtualization library (VirtualApp / NewBlackbox fork) - see README, "Plugging in a real engine".
 */
interface CloneEngine {
    /** Called once at app start (e.g. Engine.startup(context)). */
    fun init(context: Context) {}

    /** Install [pkg] into the virtual space. Return true on success. */
    fun install(context: Context, pkg: String, cloneId: String): Boolean

    /** Launch the clone identified by [cloneId]. */
    fun launch(context: Context, pkg: String, cloneId: String)
}

object DefaultEngine : CloneEngine {
    override fun install(context: Context, pkg: String, cloneId: String) = true

    override fun launch(context: Context, pkg: String, cloneId: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(pkg)
        if (intent == null) {
            Toast.makeText(context, "App not found: $pkg", Toast.LENGTH_SHORT).show()
            return
        }
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}

/** Swap this line to your real engine once integrated. */
object Engine {
    var impl: CloneEngine = DefaultEngine
}
