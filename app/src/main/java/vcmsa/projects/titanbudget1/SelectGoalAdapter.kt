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

class SelectGoalAdapter(
    private val context: Context,
    private val goals: List<UserGoal>,
    private val onItemClick: (UserGoal) -> Unit
) : RecyclerView.Adapter<SelectGoalAdapter.SelectGoalViewHolder>() {

    private var selectGoalList = listOf<UserGoal>()

    fun submitList(list: List<UserGoal>) {
        selectGoalList = list
    }

    inner class SelectGoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val goalTitle: TextView = itemView.findViewById(R.id.selectGoalTitleTextView)
        val goalIcon: ImageView = itemView.findViewById(R.id.selectIconImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectGoalViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_select_goal, parent, false)
        return SelectGoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectGoalViewHolder, position: Int) {
        val goal = goals[position]

        holder.goalTitle.text = goal.GoalTitle

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        try {
            val url = URL(goal.IconUrl)
            val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            holder.goalIcon.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.itemView.setOnClickListener {
            onItemClick(goal)
        }
    }

    override fun getItemCount(): Int {
        return goals.size
    }
}