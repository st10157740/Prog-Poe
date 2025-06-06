package vcmsa.projects.titanbudget1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TotalCategorySummaryAdapter(private val items: List<Pair<String, Double>>) :
    RecyclerView.Adapter<TotalCategorySummaryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryText: TextView = view.findViewById(R.id.categoryText)
        val totalText: TextView = view.findViewById(R.id.totalText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.total_category_summary_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (category, total) = items[position]
        holder.categoryText.text = category
        holder.totalText.text = "R$total"
    }

    override fun getItemCount() = items.size
}