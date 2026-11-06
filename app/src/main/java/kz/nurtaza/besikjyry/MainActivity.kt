package kz.nurtaza.besikjyry

import android.app.Activity
import android.app.AlertDialog
import android.Manifest
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.os.Environment
import java.io.File
import android.content.Intent
import android.content.Context
import android.graphics.*
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.view.*
import android.widget.Toast
import kotlin.random.Random
import kotlin.math.*

data class Constellation(val kz: String, val ru: String, val points: List<Pair<Float,Float>>, val edges: List<Pair<Int,Int>>)
private fun shape(kz:String, ru:String, coords:String, links:String):Constellation {
    val p=coords.split(" ").map { val a=it.split(","); Pair(a[0].toFloat(),a[1].toFloat()) }
    val e=links.split(" ").map { val a=it.split("-"); Pair(a[0].toInt(),a[1].toInt()) }
    return Constellation(kz,ru,p,e)
}
private val catalog = listOf(
    shape("Жетіқарақшы","Большая Медведица","0.12,0.32 0.28,0.26 0.43,0.31 0.55,0.44 0.73,0.41 0.79,0.59 0.58,0.63","0-1 1-2 2-3 3-4 4-5 5-6 6-3"),
    shape("Кіші аю","Малая Медведица","0.15,0.66 0.28,0.57 0.41,0.46 0.55,0.39 0.74,0.34 0.82,0.53 0.65,0.58","0-1 1-2 2-3 3-4 4-5 5-6 6-3"),
    shape("Аңшы","Орион","0.28,0.18 0.68,0.24 0.40,0.45 0.49,0.47 0.58,0.49 0.29,0.79 0.71,0.80 0.50,0.65","0-2 2-3 3-4 4-1 2-5 4-6 3-7"),
    shape("Кассиопея","Кассиопея","0.12,0.40 0.30,0.64 0.49,0.34 0.66,0.59 0.86,0.28","0-1 1-2 2-3 3-4"),
    shape("Аққу","Лебедь","0.49,0.13 0.50,0.36 0.50,0.58 0.51,0.85 0.18,0.48 0.82,0.48","0-1 1-2 2-3 4-2 2-5"),
    shape("Лира","Лира","0.25,0.20 0.43,0.34 0.65,0.31 0.72,0.66 0.39,0.70","0-1 1-2 2-3 3-4 4-1"),
    shape("Сарышаян","Скорпион","0.19,0.19 0.35,0.26 0.46,0.40 0.51,0.54 0.61,0.67 0.77,0.69 0.82,0.82 0.69,0.86","0-1 1-2 2-3 3-4 4-5 5-6 6-7"),
    shape("Арыстан","Лев","0.18,0.52 0.29,0.31 0.45,0.22 0.56,0.37 0.47,0.55 0.73,0.65 0.82,0.48","0-1 1-2 2-3 3-4 4-0 4-5 5-6"),
    shape("Торпақ","Телец","0.21,0.27 0.43,0.44 0.57,0.54 0.74,0.25 0.66,0.77 0.33,0.67","0-1 1-2 2-3 2-4 1-5"),
    shape("Егіздер","Близнецы","0.30,0.20 0.64,0.22 0.33,0.43 0.65,0.45 0.27,0.77 0.72,0.77","0-2 2-4 1-3 3-5 2-3"),
    shape("Андромеда","Андромеда","0.16,0.48 0.37,0.43 0.57,0.38 0.79,0.27 0.53,0.64","0-1 1-2 2-3 2-4"),
    shape("Пегас","Пегас","0.27,0.25 0.71,0.26 0.73,0.70 0.27,0.70 0.13,0.14 0.85,0.13","0-1 1-2 2-3 3-0 0-4 1-5"),
    shape("Персей","Персей","0.44,0.15 0.49,0.34 0.56,0.51 0.37,0.64 0.23,0.79 0.72,0.73","0-1 1-2 2-3 3-4 2-5"),
    shape("Айдаһар","Дракон","0.16,0.32 0.28,0.20 0.41,0.30 0.54,0.22 0.67,0.36 0.72,0.56 0.61,0.74 0.43,0.68 0.27,0.77","0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8"),
    shape("Бүркіт","Орёл","0.50,0.16 0.49,0.36 0.48,0.59 0.48,0.82 0.20,0.44 0.78,0.45","0-1 1-2 2-3 4-1 1-5"),
    shape("Мерген","Стрелец","0.21,0.39 0.43,0.29 0.62,0.39 0.72,0.58 0.51,0.72 0.32,0.62 0.41,0.49","0-1 1-2 2-3 3-4 4-5 5-0 0-6 6-2"),
    shape("Үлкен төбет","Большой Пёс","0.48,0.17 0.41,0.39 0.56,0.51 0.32,0.70 0.72,0.77 0.58,0.32","0-1 1-2 2-3 2-4 2-5"),
    shape("Сиыршы","Волопас","0.49,0.17 0.31,0.34 0.23,0.57 0.48,0.81 0.72,0.58 0.66,0.32","0-1 1-2 2-3 3-4 4-5 5-0"),
    shape("Солтүстік тәж","Северная Корона","0.15,0.55 0.28,0.39 0.43,0.29 0.58,0.31 0.73,0.42 0.84,0.59","0-1 1-2 2-3 3-4 4-5"),
    shape("Оңтүстік крест","Южный Крест","0.50,0.13 0.50,0.86 0.20,0.48 0.81,0.50","0-1 2-3")
)

class MainActivity: Activity() {
    private val prefs by lazy { getSharedPreferences("settings", Context.MODE_PRIVATE) }
    private var player: MediaPlayer? = null
    private var tracks = mutableListOf<Uri>()
    private var queue = mutableListOf<Uri>()
    private var history = mutableListOf<Uri>()
    private var current: Uri? = null
    private val random = Random(System.currentTimeMillis())
    private lateinit var sky: SkyView
    private lateinit var updater: Updater
    private val handler = Handler(Looper.getMainLooper())
    private var startedAt = 0L
    private val sessionMs=60*60*1000L
    private val fadeMs=5*60*1000L
    private val stop = Runnable { player?.stop(); player?.release(); player=null; finish() }
    private val fadeAudio = object:Runnable {
        override fun run() {
            val remaining=(startedAt+sessionMs-SystemClock.elapsedRealtime()).coerceAtLeast(0L)
            val volume=(remaining.toFloat()/fadeMs).coerceIn(0f,1f)
            player?.setVolume(volume,volume)
            if(remaining>0L) handler.postDelayed(this,1000L)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        sky = SkyView(this)
        setContentView(sky)
        updater = Updater(this)
        updater.checkOnLaunch()
        startedAt = SystemClock.elapsedRealtime()
        sky.setSessionEnd(startedAt+sessionMs,fadeMs)
        handler.postDelayed(stop,sessionMs)
        handler.postDelayed(fadeAudio,sessionMs-fadeMs)
        ensureMusicAccess()
        /* val stored=prefs.getString("folder",null)
        if(stored==null) promptFolder() else {
            try { loadFolder(Uri.parse(stored)); if(tracks.isEmpty()) promptFolder("MP3 не найдены. Выберите Music/BesikJyry.") else playNext() }
            catch (e:Exception) { promptFolder("Нет доступа к папке: ${e.message}") }
        } */
    }
    private fun audioPermission():String =
        if(Build.VERSION.SDK_INT>=33) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
    private fun ensureMusicAccess() {
        if(Build.VERSION.SDK_INT>=23 && checkSelfPermission(audioPermission())!=PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(audioPermission()),72)
        } else startLocalMusic()
    }
    override fun onRequestPermissionsResult(requestCode:Int,permissions:Array<out String>,grantResults:IntArray) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults)
        if(requestCode==72) {
            if(grantResults.firstOrNull()==PackageManager.PERMISSION_GRANTED) startLocalMusic()
            else AlertDialog.Builder(this).setTitle("Доступ к музыке")
                .setMessage("Разрешите доступ к аудиофайлам в настройках приложения. Без этого MP3 не прочитать.")
                .setPositiveButton("Повторить") { _, _ -> ensureMusicAccess() }
                .setNegativeButton("Позже",null).show()
        }
    }
    private fun startLocalMusic() {
        try {
            loadLocalMusic()
            if(tracks.isEmpty()) {
                AlertDialog.Builder(this).setTitle("MP3 не найдены")
                    .setMessage("Положите MP3 во внутреннюю память: Music/BesikJyry (без вложенных папок). Затем нажмите «Повторить».")
                    .setPositiveButton("Повторить") { _, _ -> startLocalMusic() }
                    .setNegativeButton("Позже",null).show()
            } else {
                queue.clear()
                Toast.makeText(this,"Найдено MP3: ${tracks.size}",Toast.LENGTH_LONG).show()
                playNext()
            }
        } catch(e:Exception) {
            AlertDialog.Builder(this).setTitle("Ошибка поиска MP3")
                .setMessage(e.message ?: "Не удалось прочитать музыку")
                .setPositiveButton("Повторить") { _, _ -> startLocalMusic() }
                .setNegativeButton("Позже",null).show()
        }
    }
    private fun loadLocalMusic() {
        val found=mutableListOf<Uri>()
        val collection=if(Build.VERSION.SDK_INT>=29) MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            else MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val columns=arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Audio.Media.RELATIVE_PATH)
        val projection=if(Build.VERSION.SDK_INT>=29) columns else columns.take(2).toTypedArray()
        contentResolver.query(collection,projection,null,null,null)?.use { cursor ->
            val idColumn=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val pathColumn=if(Build.VERSION.SDK_INT>=29) cursor.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH) else -1
            while(cursor.moveToNext()) {
                val name=cursor.getString(nameColumn) ?: continue
                if(!name.endsWith(".mp3",true)) continue
                val id=cursor.getLong(idColumn)
                if(pathColumn>=0) {
                    val relative=(cursor.getString(pathColumn) ?: "").trim('/').lowercase()
                    if(relative!="music/besikjyry") continue
                }
                found.add(android.content.ContentUris.withAppendedId(collection,id))
            }
        }
        if(Build.VERSION.SDK_INT<29) {
            found.clear()
            val folder=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),"BesikJyry")
            folder.listFiles()?.filter { it.isFile && it.extension.equals("mp3",true) }?.forEach { found.add(Uri.fromFile(it)) }
        }
        tracks=found.distinct().toMutableList()
    }
    private fun promptFolder(message:String="Выберите папку Music/BesikJyry с MP3 и разрешите доступ.") {
        AlertDialog.Builder(this).setTitle("Музыка для Бесік жыры").setMessage(message)
            .setPositiveButton("Выбрать папку") { _, _ -> chooseFolder() }
            .setNegativeButton("Позже",null).show()
    }
    private fun chooseFolder() {
        val intent=Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        startActivityForResult(intent, 41)
    }
    override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?) {
        super.onActivityResult(requestCode,resultCode,data)
        if(requestCode==41 && resultCode==RESULT_OK && data?.data!=null) {
            val uri=data.data!!
            try {
                contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION)
                prefs.edit().putString("folder",uri.toString()).apply()
                loadFolder(uri)
                queue.clear()
                if(tracks.isEmpty()) promptFolder("В этой папке MP3 не найдены. Выберите Music/BesikJyry.")
                else { Toast.makeText(this,"Найдено MP3: ${tracks.size}",Toast.LENGTH_LONG).show(); playNext() }
            } catch(e:Exception) { promptFolder("Папка недоступна: ${e.message}") }
        }
    }
    private fun loadFolder(folder:Uri) {
        val id=DocumentsContract.getTreeDocumentId(folder)
        val children=DocumentsContract.buildChildDocumentsUriUsingTree(folder,id)
        val result=mutableListOf<Uri>()
        val docs=contentResolver.query(children,arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID,DocumentsContract.Document.COLUMN_DISPLAY_NAME,DocumentsContract.Document.COLUMN_MIME_TYPE),null,null,null)
            ?: throw IllegalStateException("Не удалось прочитать папку")
        docs.use { cursor ->
            while(cursor.moveToNext()) {
                val docId=cursor.getString(0)
                val name=cursor.getString(1) ?: ""
                val mime=cursor.getString(2) ?: ""
                if(name.endsWith(".mp3",true) || mime=="audio/mpeg") result.add(DocumentsContract.buildDocumentUriUsingTree(folder,docId))
            }
        }
        tracks=result
    }
    private fun refill() {
        try { loadLocalMusic() } catch (_:Exception) {}
        queue=tracks.shuffled(random).toMutableList()
        if(queue.size>1 && queue.first()==current) queue.add(queue.removeAt(0))
    }
    private fun playNext() {
        if(queue.isEmpty()) refill()
        if(queue.isEmpty()) { startLocalMusic(); return }
        val next=queue.removeAt(0)
        play(next,true)
    }
    private var consecutiveFailures=0
    private fun play(uri:Uri,record:Boolean) {
        try {
            player?.release()
            val mp=MediaPlayer()
            mp.setDataSource(this,uri)
            mp.setOnCompletionListener { playNext() }
            mp.prepare()
            mp.start()
            consecutiveFailures=0
            player=mp
            current=uri
            if(record) history.add(uri)
        } catch (e:Exception) {
            player?.release(); player=null
            consecutiveFailures++
            if(consecutiveFailures<3 && queue.isNotEmpty()) playNext()
            else { consecutiveFailures=0; AlertDialog.Builder(this).setTitle("Ошибка MP3").setMessage(e.message ?: "Не удалось воспроизвести файл").setPositiveButton("OK",null).show() }
        }
    }
    override fun onKeyDown(keyCode:Int,event:KeyEvent?):Boolean {
        when(keyCode) {
            KeyEvent.KEYCODE_MENU -> { startLocalMusic(); return true }
            KeyEvent.KEYCODE_DPAD_CENTER -> { if(player==null) { ensureMusicAccess(); return true } }
            KeyEvent.KEYCODE_DPAD_UP -> { sky.requestComet(); return true }
            KeyEvent.KEYCODE_DPAD_DOWN -> { sky.toggleMilkyWay(); return true }
            KeyEvent.KEYCODE_DPAD_RIGHT -> { playNext(); sky.advance(); return true }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if(history.size>1) { history.removeAt(history.lastIndex); val prev=history.last(); play(prev,false) }
                sky.previous()
                return true
            }
        }
        return super.onKeyDown(keyCode,event)
    }
    override fun onDestroy() {
        updater.close()
        handler.removeCallbacks(stop)
        handler.removeCallbacks(fadeAudio)
        player?.release()
        player=null
        super.onDestroy()
    }
}

private data class Star(val x:Float,val y:Float,val size:Float,val alpha:Float,val phase:Float,val period:Float,val warm:Boolean)
class SkyView(context:Context): View(context) {
    private val rng=Random(SystemClock.elapsedRealtimeNanos())
    private val paint=Paint(Paint.ANTI_ALIAS_FLAG)
    // Keep star density consistent from 1080p through native 4K.
    private var stars: List<Star> = emptyList()
    override fun onSizeChanged(w:Int,h:Int,oldw:Int,oldh:Int) {
        super.onSizeChanged(w,h,oldw,oldh)
        val pixels = w.toLong() * h.toLong()
        val count = (1250.0 * pixels / (1920.0 * 1080.0)).toInt().coerceIn(600,5000)
        stars = List(count) {
        Star(rng.nextFloat(),rng.nextFloat(),0.45f+rng.nextFloat()*1.5f,
            0.12f+rng.nextFloat()*0.65f,rng.nextFloat()*6.283f,
            40f+rng.nextFloat()*55f,rng.nextFloat()<0.15f)
        }
    }
    private var milkyWayEnabled=false
    private var milkyWaySeed=0
    private data class Dust(val x:Float,val y:Float,val radius:Float,val alpha:Int)
    private var dust:List<Dust> = emptyList()
    private var sessionEnd=Long.MAX_VALUE
    private var sessionFadeMs=300000L
    fun setSessionEnd(end:Long,fade:Long) { sessionEnd=end; sessionFadeMs=fade }
    fun toggleMilkyWay() {
        milkyWayEnabled=!milkyWayEnabled
        if(milkyWayEnabled) {
            milkyWaySeed++
            val slope=(rng.nextFloat()-.5f)*0.7f
            val offset=.35f+rng.nextFloat()*.3f
            dust=List(1400) {
                val x=rng.nextFloat()
                val across=(rng.nextFloat()+rng.nextFloat()+rng.nextFloat()-1.5f)*.12f
                Dust(x,(offset+slope*(x-.5f)+across).coerceIn(0f,1f),
                    .28f+rng.nextFloat()*.65f,8+rng.nextInt(28))
            }
        }
        invalidate()
    }
    private fun drawMilkyWay(canvas:Canvas,w:Float,h:Float) {
        if(!milkyWayEnabled) return
        for(d in dust) {
            paint.color=Color.rgb(188,205,230)
            paint.alpha=d.alpha
            canvas.drawCircle(d.x*w,d.y*h,(d.radius*w/1920f).coerceAtLeast(.3f),paint)
        }
    }
    private data class Satellite(val start:Long,val duration:Long,val x:Float,val y:Float,val dx:Float,val dy:Float)
    private var satellite:Satellite?=null
    private var nextSatelliteAt=SystemClock.elapsedRealtime()+rng.nextLong(420000L,900001L)
    private fun drawSatellite(canvas:Canvas,now:Long,w:Float,h:Float) {
        if(satellite==null && now>=nextSatelliteAt) {
            val dx=(if(rng.nextBoolean()) 1f else -1f)*(.65f+rng.nextFloat()*.3f)
            satellite=Satellite(now,rng.nextLong(80000L,125001L),
                if(dx>0) -.04f else 1.04f,.15f+rng.nextFloat()*.7f,
                dx,(rng.nextFloat()-.5f)*.16f)
        }
        val sat=satellite ?: return
        val progress=(now-sat.start).toFloat()/sat.duration
        if(progress>=1f) {
            satellite=null
            nextSatelliteAt=now+rng.nextLong(420000L,900001L)
            return
        }
        val visibility=(min(1f,progress*10f)*min(1f,(1f-progress)*10f)).coerceIn(0f,1f)
        paint.color=Color.rgb(196,213,232)
        paint.alpha=(visibility*95f).toInt()
        canvas.drawCircle((sat.x+sat.dx*progress)*w,(sat.y+sat.dy*progress)*h,
            (1.0f*w/1920f).coerceAtLeast(.7f),paint)
    }
    private val order=catalog.indices.shuffled().toMutableList()
    private var index=0
    private var sceneStart=SystemClock.elapsedRealtime()
    private var outgoing:Int?=null
    private var transitionStart=0L
    private val transitionDuration=18000L
    // Rare, dim shooting stars: randomized path and 1–3 minute gaps.
    private data class Comet(val start:Long,val duration:Long,val x:Float,val y:Float,
        val dx:Float,val dy:Float,val brightness:Float,val tail:Float)
    private var comet:Comet?=null
    private var nextCometAt=SystemClock.elapsedRealtime()+rng.nextLong(60000L,180001L)
    fun requestComet() {
        // A new UP press schedules a random comet five seconds later.
        // The existing sky, music and constellation controls are unchanged.
        nextCometAt=SystemClock.elapsedRealtime()+5000L
        invalidate()
    }
    private val cometPaint=Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style=Paint.Style.STROKE
        strokeCap=Paint.Cap.ROUND
    }
    private fun drawComet(canvas:Canvas,now:Long,w:Float,h:Float) {
        if(comet==null && now>=nextCometAt) {
            val angle=(if(rng.nextBoolean()) 1f else -1f)*(0.25f+rng.nextFloat()*0.65f)
            val dx=(0.12f+rng.nextFloat()*0.16f)*(if(rng.nextBoolean()) 1f else -1f)
            val dy=dx*angle
            comet=Comet(now,rng.nextLong(4500L,7001L),
                0.12f+rng.nextFloat()*0.70f,0.15f+rng.nextFloat()*0.65f,
                dx,dy,0.48f+rng.nextFloat()*0.22f,0.018f+rng.nextFloat()*0.022f)
        }
        val c=comet ?: return
        val p=(now-c.start).toFloat()/c.duration
        if(p>=1f) {
            comet=null
            if(nextCometAt<=c.start) nextCometAt=now+rng.nextLong(60000L,180001L)
            return
        }
        val envelope=(min(1f,p*5f)*min(1f,(1f-p)*5f)).coerceIn(0f,1f)
        val x=(c.x+c.dx*p)*w
        val y=(c.y+c.dy*p)*h
        val magnitude=sqrt((c.dx*w)*(c.dx*w)+(c.dy*h)*(c.dy*h)).coerceAtLeast(1f)
        val tailLength=c.tail*w
        val tx=x-c.dx*w/magnitude*tailLength
        val ty=y-c.dy*h/magnitude*tailLength
        val alpha=(c.brightness*envelope*255f).toInt().coerceIn(0,255)
        cometPaint.strokeWidth=(1.35f*w/1920f).coerceAtLeast(1.0f)
        cometPaint.shader=LinearGradient(tx,ty,x,y,
            intArrayOf(Color.TRANSPARENT,Color.argb(alpha/2,170,196,230),
                Color.argb(alpha,205,222,246)),null,Shader.TileMode.CLAMP)
        canvas.drawLine(tx,ty,x,y,cometPaint)
        cometPaint.shader=null
        paint.color=Color.rgb(205,222,246)
        paint.alpha=alpha
        canvas.drawCircle(x,y,(1.5f*w/1920f).coerceAtLeast(1.1f),paint)
    }
    private val bg=Paint(Paint.ANTI_ALIAS_FLAG)
    private val glow=Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint=Paint(Paint.ANTI_ALIAS_FLAG).apply { color=Color.rgb(159,177,207); typeface=Typeface.create("sans-serif-light",Typeface.NORMAL) }
    private val linePaint=Paint(Paint.ANTI_ALIAS_FLAG).apply { color=Color.rgb(126,165,218); strokeWidth=1.1f; style=Paint.Style.STROKE }
    fun advance() { change(1) }
    fun previous() { change(-1) }
    private fun change(direction:Int) {
        outgoing=order[index]
        index+=direction
        if(index>=order.size) { order.shuffle(); index=0 }
        if(index<0) index=order.lastIndex
        sceneStart=SystemClock.elapsedRealtime()
        transitionStart=sceneStart
        invalidate()
    }
    private fun drawConstellation(canvas:Canvas,item:Int,opacity:Float,labelOpacity:Float,lineOpacity:Float=labelOpacity) {
        if(opacity<=0f) return
        val w=width.toFloat(); val h=height.toFloat()
        val c=catalog[item]
        val scale=min(w*.58f,h*.75f)
        val originX=w*.5f-scale*.5f; val originY=h*.5f-scale*.5f
        fun xy(p:Pair<Float,Float>)=Pair(originX+p.first*scale,originY+p.second*scale)
        linePaint.strokeWidth=(1.1f*w/1920f).coerceAtLeast(0.75f)
        linePaint.alpha=(lineOpacity*105).toInt().coerceIn(0,255)
        for((a,b) in c.edges) {
            val p=xy(c.points[a]); val q=xy(c.points[b])
            canvas.drawLine(p.first,p.second,q.first,q.second,linePaint)
        }
        for((i,p) in c.points.withIndex()) {
            val pos=xy(p)
            val radius=(if(i==0) 3.4f else 2.5f+(i%3)*.35f)*w/1920f
            paint.color=Color.rgb(226,236,255)
            paint.alpha=(opacity*230).toInt().coerceIn(0,255)
            canvas.drawCircle(pos.first,pos.second,radius,paint)
        }
        if(labelOpacity>0f) {
            textPaint.textSize=14f*w/1920f
            textPaint.alpha=(labelOpacity*140).toInt().coerceIn(0,255)
            canvas.drawText("${c.kz} (${c.ru})",w*.025f,h*.95f,textPaint)
        }
    }
    override fun onDraw(canvas:Canvas) {
        super.onDraw(canvas)
        val w=width.toFloat(); val h=height.toFloat()
        val now=SystemClock.elapsedRealtime()
        if(now-sceneStart>=240000L) advance()
        val seconds=(now-sceneStart)/1000f
        val t=now/1000f
        bg.shader=LinearGradient(0f,0f,w,h, intArrayOf(Color.rgb(2,5,15),Color.rgb(5,12,29),Color.rgb(1,3,11)),null,Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,bg)
        bg.shader=null
        // Very subtle, low-contrast Milky Way haze, never bright enough to disturb sleep.
        glow.shader=RadialGradient(w*.54f,h*.48f,w*.62f,intArrayOf(Color.argb(25,35,57,100),Color.argb(8,21,43,80),Color.TRANSPARENT),null,Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,glow)
        glow.shader=null
        drawMilkyWay(canvas,w,h)
        // Every two minutes a different ~5% cohort of background stars
        // slowly vanishes completely and returns. The other 95% keep
        // their existing independent, subtle breathing animation.
        val fadeCycle=(now/120000L).toInt()
        val fadeSeconds=(now%120000L)/1000f
        val fadeGroup=fadeCycle%20
        for((starIndex,s) in stars.withIndex()) {
            val breathing=(0.5f+0.5f*cos((t/s.period)*6.283185f+s.phase)).toFloat()
            val baseAlpha=(s.alpha*(0.28f+0.72f*breathing)).coerceIn(0f,1f)
            val fadeFactor=if(starIndex%20==fadeGroup) {
                when {
                    fadeSeconds<50f -> {
                        val p=fadeSeconds/50f
                        1f-p*p*(3f-2f*p)
                    }
                    fadeSeconds<70f -> 0f
                    fadeSeconds<120f -> {
                        val p=(fadeSeconds-70f)/50f
                        p*p*(3f-2f*p)
                    }
                    else -> 1f
                }
            } else 1f
            val a=baseAlpha*fadeFactor
            val x=s.x*w; val y=s.y*h
            paint.color=when {
                s.warm -> Color.rgb(242,224,207)
                starIndex%17==0 -> Color.rgb(255,231,198)
                starIndex%11==0 -> Color.rgb(171,199,244)
                starIndex%7==0 -> Color.rgb(238,240,246)
                else -> Color.rgb(207,222,246)
            }
            paint.alpha=(a*255).toInt()
            canvas.drawCircle(x,y,s.size*w/1920f,paint)
        }
        drawComet(canvas,now,w,h)
        drawSatellite(canvas,now,w,h)
        val elapsed=now-transitionStart
        val transitioning=outgoing!=null && elapsed<transitionDuration
        if(transitioning) {
            val half=transitionDuration/2f
            if(elapsed<half) {
                val p=(elapsed/half).coerceIn(0f,1f)
                val eased=p*p*(3f-2f*p)
                drawConstellation(canvas,outgoing!!,1f-eased,0f,0f)
            } else {
                val p=((elapsed-half)/half).coerceIn(0f,1f)
                val eased=p*p*(3f-2f*p)
                drawConstellation(canvas,order[index],eased,eased)
            }
        } else {
            outgoing=null
            // Lines and label remain for 11 seconds after the new constellation
            // is fully visible, then fade together over the next 4 seconds.
            val fullyVisibleAt=if(transitionStart==0L) sceneStart else transitionStart+transitionDuration
            val visibleSeconds=((now-fullyVisibleAt).coerceAtLeast(0L))/1000f
            val guideOpacity=if(visibleSeconds<11f) 1f else ((15f-visibleSeconds)/4f).coerceIn(0f,1f)
            drawConstellation(canvas,order[index],1f,guideOpacity,guideOpacity)
        }
        // Dim the entire sky during the final five minutes.
        val remaining=sessionEnd-now
        if(remaining<sessionFadeMs) {
            val opacity=(1f-remaining.toFloat()/sessionFadeMs).coerceIn(0f,1f)
            paint.color=Color.BLACK
            paint.alpha=(opacity*255f).toInt()
            canvas.drawRect(0f,0f,w,h,paint)
        }
        // Refresh at display cadence; actual frame rate depends on projector hardware.
        postInvalidateDelayed(100L)
    }
}
