package com.example.appcloner

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap

data class AppInfo(val name: String, val pkg: String, val icon: Bitmap)

class MainActivity : AppCompatActivity() {

    private var pickedIcon: Bitmap? = null
    private var previewView: ImageView? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri ?: return@registerForActivityResult
        contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)?.let {
                pickedIcon = Bitmap.createScaledBitmap(it, 192, 192, true)
                previewView?.setImageBitmap(pickedIcon)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Engine.impl.init(applicationContext)
        title = "Choose an app to clone"

        val list = ListView(this)
        setContentView(list)

        val apps = loadApps(this)
        list.adapter = AppAdapter(apps)
        list.setOnItemClickListener { _, _, pos, _ -> showCloneDialog(apps[pos]) }
    }

    private fun loadApps(ctx: Context): List<AppInfo> {
        val pm = ctx.packageManager
        return pm.getInstalledApplications(0)
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null && it.packageName != packageName }
            .map { AppInfo(it.loadLabel(pm).toString(), it.packageName, it.loadIcon(pm).toBitmap(192, 192)) }
            .sortedBy { it.name.lowercase() }
    }

    private fun showCloneDialog(app: AppInfo) {
        pickedIcon = null
        val pad = (16 * resources.displayMetrics.density).toInt()

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, 0)
        }
        val nameInput = EditText(this).apply {
            setText("${app.name} 2")
            hint = "Clone name"
            setSingleLine()
        }
        val preview = ImageView(this).apply {
            setImageBitmap(app.icon)
            layoutParams = LinearLayout.LayoutParams(pad * 6, pad * 6).apply { topMargin = pad }
        }
        previewView = preview
        val iconBtn = Button(this).apply {
            text = "Change icon"
            setOnClickListener { pickImage.launch("image/*") }
        }
        layout.addView(nameInput)
        layout.addView(preview)
        layout.addView(iconBtn)

        AlertDialog.Builder(this)
            .setTitle("Clone ${app.name}")
            .setView(layout)
            .setPositiveButton("Create") { _, _ ->
                val label = nameInput.text.toString().ifBlank { app.name }
                val icon = pickedIcon ?: app.icon
                createClone(app, label, icon)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createClone(app: AppInfo, label: String, icon: Bitmap) {
        val cloneId = System.currentTimeMillis().toString()
        if (!Engine.impl.install(this, app.pkg, cloneId)) {
            Toast.makeText(this, "Clone failed", Toast.LENGTH_LONG).show()
            return
        }
        val ok = ShortcutHelper.pin(this, app.pkg, cloneId, label, icon)
        Toast.makeText(
            this,
            if (ok) "Confirm the shortcut to add it to your home screen" else "Launcher doesn't support pinned shortcuts",
            Toast.LENGTH_LONG
        ).show()
    }

    inner class AppAdapter(private val items: List<AppInfo>) : BaseAdapter() {
        override fun getCount() = items.size
        override fun getItem(p: Int) = items[p]
        override fun getItemId(p: Int) = p.toLong()
        override fun getView(p: Int, convert: View?, parent: ViewGroup): View {
            val v = convert ?: LayoutInflater.from(parent.context)
                .inflate(android.R.layout.activity_list_item, parent, false)
            val a = items[p]
            v.findViewById<TextView>(android.R.id.text1).text = a.name
            v.findViewById<ImageView>(android.R.id.icon).setImageBitmap(a.icon)
            return v
        }
    }
}
