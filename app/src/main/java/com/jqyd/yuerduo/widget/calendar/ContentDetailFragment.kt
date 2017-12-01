package com.jqyd.yuerduo.widget.calendar

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.widget.camera.CameraLayout
import kotlinx.android.synthetic.main.layout_itinerary_detail.view.*

/**
 * Created by gjc on 17-9-20.
 */

class ContentDetailFragment : Fragment() {

    var ID_CAMERALAYOUT = 10030

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.layout_itinerary_detail, container, false)
        var cameraLayout = CameraLayout(context)
        cameraLayout.id = ID_CAMERALAYOUT
        cameraLayout.editable = false
        view?.lv_camera?.addView(cameraLayout)
        return view
    }

}
