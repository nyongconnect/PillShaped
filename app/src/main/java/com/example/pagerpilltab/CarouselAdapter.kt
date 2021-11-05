package com.example.pagerpilltab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView

class CarouselAdapter : RecyclerView.Adapter<CarouselAdapter.ViewHolder>() {

    enum class Model constructor(@StringRes val titleResId: Int, @StringRes val bodyResId: Int) {
        INTRO_MODEL_1(
            R.string.app_intro_usp_1_title,
            R.string.app_intro_usp_1_body
        ),
        INTRO_MODEL_2(
            R.string.app_intro_usp_2_title,
            R.string.app_intro_usp_2_body
        ),
        INTRO_MODEL_3(
            R.string.app_intro_usp_3_title,
            R.string.app_intro_usp_3_body
        ),
        INTRO_MODEL_4(
            R.string.app_intro_usp_4_title,
            R.string.app_intro_usp_4_body
        ),
    }

    var items: List<Model> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.item_title)
        val body: TextView = v.findViewById(R.id.item_body)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.inflate(R.layout.item_usp))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        with(holder) {
            items[position].let {
                title.setText(it.titleResId)
                body.setText(it.bodyResId)
            }
        }

    override fun getItemCount(): Int = items.size
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}