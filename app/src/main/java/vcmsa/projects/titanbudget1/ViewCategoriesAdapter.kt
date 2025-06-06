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
import java.net.URL

class ViewCategoriesAdapter(
    private val context: Context,
    private val categories: List<Category>
) : RecyclerView.Adapter<ViewCategoriesAdapter.ViewCategoriesViewHolder>() {

    inner class ViewCategoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.txtViewCategory)
        val categoryIcon: ImageView = itemView.findViewById(R.id.imageViewCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewCategoriesViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.view_categories_item, parent, false)
        return ViewCategoriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewCategoriesViewHolder, position: Int) {
        val category = categories[position]

        holder.categoryName.text = category.categoryName

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        try {
            val url = URL(category.iconUrl)
            val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            holder.categoryIcon.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int {
        return categories.size
    }
}