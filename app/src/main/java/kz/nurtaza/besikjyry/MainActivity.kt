package kz.nurtaza.besikjyry

import android.app.Activity
import android.app.AlertDialog
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
    private val stop = Runnable { player?.stop(); player?.release(); player=null; finish() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        sky = SkyView(this)
        setContentView(sky)
        updater = Updater(this)
        updater.checkOnLaunch()
        startedAt = SystemClock.elapsedRealtime()
        handler.postDelayed(stop, 60*60*1000L)
        val stored=prefs.getString("folder",null)
        if(stored==null) promptFolder() else {
            try { loadFolder(Uri.parse(stored)); if(tracks.isEmpty()) promptFolder("MP3 не найдены. Выберите Music/BesikJyry.") else playNext() }
            catch (e:Exception) { promptFolder("Нет доступа к папке: ${e.message}") }
        }
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
        prefs.getString("folder",null)?.let { try { loadFolder(Uri.parse(it)) } catch (_:Exception) {} }
        queue=tracks.shuffled(random).toMutableList()
        if(queue.size>1 && queue.first()==current) queue.add(queue.removeAt(0))
    }
    private fun playNext() {
        if(queue.isEmpty()) refill()
        if(queue.isEmpty()) { promptFolder("MP3 не найдены. Выберите Music/BesikJyry."); return }
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
            else { consecutiveFailures=0; promptFolder("Ошибка воспроизведения MP3: ${e.message}. Выберите папку снова.") }
        }
    }
    override fun onKeyDown(keyCode:Int,event:KeyEvent?):Boolean {
        when(keyCode) {
            KeyEvent.KEYCODE_MENU -> { chooseFolder(); return true }
            KeyEvent.KEYCODE_DPAD_CENTER -> { if(player==null) { chooseFolder(); return true } }
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
        player?.release()
        player=null
        super.onDestroy()
    }
}

private data class Star(val x:Float,val y:Float,val size:Float,val alpha:Float,val phase:Float,val period:Float,val warm:Boolean)
class SkyView(context:Context): View(context) {
    private val rng=Random(78239)
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
    private val order=catalog.indices.shuffled().toMutableList()
    private var index=0
    private var sceneStart=SystemClock.elapsedRealtime()
    private var outgoing:Int?=null
    private var transitionStart=0L
    private val transitionDuration=18000L
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
    private fun drawConstellation(canvas:Canvas,item:Int,opacity:Float,labelOpacity:Float) {
        if(opacity<=0f) return
        val w=width.toFloat(); val h=height.toFloat()
        val c=catalog[item]
        val scale=min(w*.58f,h*.75f)
        val originX=w*.5f-scale*.5f; val originY=h*.5f-scale*.5f
        fun xy(p:Pair<Float,Float>)=Pair(originX+p.first*scale,originY+p.second*scale)
        linePaint.strokeWidth=(1.1f*w/1920f).coerceAtLeast(0.75f)
        linePaint.alpha=(opacity*105).toInt().coerceIn(0,255)
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
        for(s in stars) {
            val breathing=(0.5f+0.5f*cos((t/s.period)*6.283185f+s.phase)).toFloat()
            val a=(s.alpha*(0.28f+0.72f*breathing)).coerceIn(0f,1f)
            val x=s.x*w; val y=s.y*h
            paint.color=if(s.warm) Color.rgb(242,224,207) else Color.rgb(198,220,255)
            paint.alpha=(a*255).toInt()
            canvas.drawCircle(x,y,s.size*w/1920f,paint)
        }
        val elapsed=now-transitionStart
        val transitioning=outgoing!=null && elapsed<transitionDuration
        if(transitioning) {
            val progress=(elapsed.toFloat()/transitionDuration).coerceIn(0f,1f)
            val smooth=progress*progress*(3f-2f*progress)
            drawConstellation(canvas,outgoing!!,1f-smooth,0f)
            drawConstellation(canvas,order[index],smooth,smooth)
        } else {
            outgoing=null
            val labelOpacity=if(seconds<11f) 1f else ((15f-seconds)/4f).coerceIn(0f,1f)
            drawConstellation(canvas,order[index],1f,labelOpacity)
        }
        // Refresh at display cadence; actual frame rate depends on projector hardware.
        postInvalidateDelayed(100L)
    }
}
