package vcmsa.projects.titanbudget1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GoalAdapter(private val context: Context) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    private var goalList = listOf<GoalWithProgress>()

    fun submitList(list: List<GoalWithProgress>) {
        goalList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.goal_item, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(goalList[position])
    }

    override fun getItemCount(): Int = goalList.size

    inner class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtGoalTitle: TextView = itemView.findViewById(R.id.txtGoalTitle)
        private val txtCategoryGoal: TextView = itemView.findViewById(R.id.txtCategoryGoal)
        private val txtMinimumAmount: TextView = itemView.findViewById(R.id.txtMinimumAmount)
        private val txtMaximumAmount: TextView = itemView.findViewById(R.id.txtMaximumAmount)
        private val goalIcon: ImageView = itemView.findViewById(R.id.goalIcon)
        private val minimumProgressBar: ProgressBar = itemView.findViewById(R.id.minimumProgressBar)
        private val maximumProgressBar: ProgressBar = itemView.findViewById(R.id.maximumProgressBar)

        fun bind(userGoal: GoalWithProgress) {
            txtGoalTitle.text = userGoal.goalTitle
            txtCategoryGoal.text = userGoal.Category
            Glide.with(context)
                .load(userGoal.IconUrl)
                .into(goalIcon)
            txtMinimumAmount.text = "R"+userGoal.MinimumAmount.toString()
            txtMaximumAmount.text = "R"+userGoal.MaximumAmount.toString()

            maximumProgressBar.max = userGoal.MaximumAmount
            minimumProgressBar.max = userGoal.MinimumAmount
            maximumProgressBar.progress = userGoal.totalAmount.toInt()
            minimumProgressBar.progress = userGoal.totalAmount.toInt()


        }
    }
}
