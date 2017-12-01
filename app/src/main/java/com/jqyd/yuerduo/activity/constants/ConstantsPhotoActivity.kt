package com.jqyd.yuerduo.activity.constants

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jqyd.yuerduo.R
import kotlinx.android.synthetic.main.activity_constants_image.*

class ConstantsPhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_constants_image)
        val photoUrl = intent.getStringExtra("url")
        Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.no_avatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(photoView)
    }
}
