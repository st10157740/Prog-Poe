package vcmsa.projects.titanbudget1

import android.content.Context
import android.graphics.BitmapFactory
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.net.URL

class IconAdapter(
    private val context: Context,
    private val icons: List<Icon>,
    private val onItemClick: (Icon) -> Unit
) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    inner class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val selectedIcon: ImageView = itemView.findViewById(R.id.imgIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.icon_item, parent, false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val icon = icons[position]

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        try {
            val url = URL(icon.IconUrl)
            val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            holder.selectedIcon.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.itemView.setOnClickListener {
            onItemClick(icon)
        }
    }

    override fun getItemCount(): Int {
        return icons.size
    }
}

/*
   Code found in Chat-GPT
   Author: OpenAI
   Link: https://chatgpt.com/
   Accessed: 28 April 2025

   class IconAdapter(private val icons: List<Int>) :
    RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iconImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_icon, parent, false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.imageView.setImageResource(icons[position])
    }

    override fun getItemCount(): Int = icons.size
}
*/