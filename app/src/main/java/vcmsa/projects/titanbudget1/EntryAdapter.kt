package vcmsa.projects.titanbudget1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class EntryAdapter(private val context: Context,
                   private val expenses: List<Entry>,
                   private val onItemClick: (Entry) -> Unit) : RecyclerView.Adapter<EntryAdapter.ExpenseViewHolder>() {

    private var entryList = listOf<Entry>()

    fun submitList(list: List<Entry>) {
        entryList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.entry_item, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(entryList[position])
        holder.itemView.setOnClickListener {
            onItemClick(entryList[position])
        }
    }

    override fun getItemCount(): Int = entryList.size

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtExpenseName: TextView = itemView.findViewById(R.id.txtExpenseName)
        private val txtExpenseCategory: TextView = itemView.findViewById(R.id.txtExpenseCategory)
        private val txtExpenseAmount: TextView = itemView.findViewById(R.id.txtExpenseAmount)
        private val expenseImage: ImageView = itemView.findViewById(R.id.expenseImage)

        fun bind(entry: Entry) {
            txtExpenseName.text = entry.Name
            txtExpenseCategory.text = entry.Category
            txtExpenseAmount.text = "R"+entry.Amount.toString()
            Glide.with(context)
                .load(entry.IconUrl)
                .into(expenseImage)
        }
    }
}