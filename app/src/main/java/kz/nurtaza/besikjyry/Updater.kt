package kz.nurtaza.besikjyry

import android.app.Activity
import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.widget.Toast
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Checks the stable GitHub Release APK when the app starts. The APK is downloaded
 * directly by Android DownloadManager, without opening GitHub or a browser.
 * Android must still ask the user to approve installation for security.
 */
class Updater(private val activity: Activity) {
    private val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private var downloadId = -1L
    private var receiver: BroadcastReceiver? = null

    fun checkOnLaunch() {
        Thread {
            try {
                val connection = URL("https://api.github.com/repos/NurtazaAIB/BesikJyry/releases/tags/latest").openConnection() as HttpURLConnection
                connection.connectTimeout = 7000
                connection.readTimeout = 7000
                connection.setRequestProperty("Accept", "application/vnd.github+json")
                connection.setRequestProperty("User-Agent", "BesikJyry-Android")
                val json = connection.inputStream.bufferedReader().use { JSONObject(it.readText()) }
                connection.disconnect()
                val asset = json.getJSONArray("assets")
                var url: String? = null
                for (i in 0 until asset.length()) {
                    val item = asset.getJSONObject(i)
                    if (item.getString("name") == "BesikJyry.apk") {
                        url = item.getString("browser_download_url")
                        break
                    }
                }
                val releaseVersion = Regex("""1\.0\.(\d+)""").find(json.optString("name"))?.groupValues?.get(1)?.toIntOrNull()
                val localVersion = activity.packageManager.getPackageInfo(activity.packageName,0).versionCode
                if (url != null && releaseVersion != null && releaseVersion > localVersion) {
                    val downloadUrl = url
                    activity.runOnUiThread { download(downloadUrl) }
                }
            } catch (_: Exception) {
                // Offline playback must work even if update checks fail.
            }
        }.start()
    }

    private fun download(url: String) {
        try {
            Toast.makeText(activity, "Жаңа нұсқа жүктелуде…", Toast.LENGTH_LONG).show()
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle("BesikJyry — обновление")
                .setDescription("Загрузка APK")
                .setMimeType("application/vnd.android.package-archive")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(activity, Environment.DIRECTORY_DOWNLOADS, "BesikJyry-update.apk")
            downloadId = manager.enqueue(request)
            receiver = object: BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1L) != downloadId) return
                    manager.query(DownloadManager.Query().setFilterById(downloadId))?.use { cursor ->
                        if(cursor.moveToFirst() && cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                            install(manager.getUriForDownloadedFile(downloadId))
                        } else {
                            Toast.makeText(activity,"Не удалось скачать обновление",Toast.LENGTH_LONG).show()
                        }
                    }
                    close()
                }
            }
            activity.registerReceiver(receiver,IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        } catch (_: Exception) { Toast.makeText(activity,"Ошибка загрузки обновления",Toast.LENGTH_LONG).show() }
    }

    private fun install(uri: Uri?) {
        if(uri==null) return
        if(Build.VERSION.SDK_INT>=26 && !activity.packageManager.canRequestPackageInstalls()) {
            Toast.makeText(activity,"Разрешите установку из этого приложения, затем повторно откройте его",Toast.LENGTH_LONG).show()
            activity.startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,Uri.parse("package:"+activity.packageName)))
            return
        }
        try {
            val intent=Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri,"application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            activity.startActivity(intent)
        } catch (_: Exception) { Toast.makeText(activity,"Не удалось открыть установщик APK",Toast.LENGTH_LONG).show() }
    }
    fun close() {
        receiver?.let { try { activity.unregisterReceiver(it) } catch (_:Exception) {} }
        receiver=null
    }
}
