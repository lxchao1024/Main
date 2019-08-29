package com.lxchao.app

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lxchao.app.bean.MovieBean
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.FileCallBack
import okhttp3.Call
import java.io.File
import java.lang.Exception

/**
 * http://git.jufan.tv/tangpengxiang/MarsLive.git
 */
class MainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var adapter: Adapter? = null
    private var gridLayoutManager: GridLayoutManager? = null
    val mUrl = "https://storage.19gofun.com/vas/goods/2019-03-22/1553240053055.jpg"
    val downLoadUrl = "https://storage.19gofun.com/vas/goods/2019-03-29/1553840339868.jpg"


    val list = arrayListOf<String>(downLoadUrl, downLoadUrl, downLoadUrl, downLoadUrl, downLoadUrl, downLoadUrl, downLoadUrl, downLoadUrl)

    lateinit var dataSource: MutableList<MovieBean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gridLayoutManager = GridLayoutManager(this, 3)
//        gridLayoutManager!!.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                return if (position == 0) {
//                    4
//                } else {
//                    1
//                }
//            }
//        }
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView!!.layoutManager = gridLayoutManager
        adapter = Adapter()
        recyclerView!!.adapter = adapter
    }


    private inner class Adapter : RecyclerView.Adapter<Adapter.Holder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(LayoutInflater.from(this@MainActivity).inflate(R.layout.item, parent, false))
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.imageView.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.rotate))
            holder.bind(list[position], position)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
            var imageView: ProgressBar
            var textView: TextView
            init {
                imageView = view.findViewById(R.id.icon)
                textView = view.findViewById(R.id.tv)
            }

            private val handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)
                    val progress = msg.obj as Float
                    if (100 * progress >= 100) {
                        textView.visibility = View.GONE
                        imageView.visibility = View.VISIBLE
                    } else {
                        textView.visibility = View.VISIBLE
                        imageView.visibility = View.GONE
                    }
                    textView.text = "${progress * 100}%"
                }
            }

            var pos = 0

            fun bind(url: String, position: Int) {
                pos = position;
                download(baseContext, position, url, object : OnMainCallBack {
                    override fun onProgress(progress: Float) {
                        val message = handler.obtainMessage()
                        message.obj = progress
                        handler.sendMessage(message)
                    }
                })
                download(baseContext, position, mUrl, null)
            }
        }
    }


    interface OnMainCallBack {
        fun onProgress(progress: Float)
    }
}
