package vcmsa.projects.titanbudget1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChartGoalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChartGoalFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var goalsBarChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_chart_goal, container, false)

        goalsBarChart = view.findViewById(R.id.goalsBarChart)

        loadGoals()

        return view
    }

    private fun loadGoals() {
        val supabase = createSupabaseClient(
            supabaseUrl = "https://xoelotumrinspbbparpy.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhvZWxvdHVtcmluc3BiYnBhcnB5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU1MTg1OTcsImV4cCI6MjA2MTA5NDU5N30.3stfByRyFhnmYGD2JiNtNEuSccTW9xq0vbJAEkNKuJM"
        ) {
            install(Postgrest)
        }

        lifecycleScope.launch {
            try {
                val response = supabase.from("UserGoals")
                    .select {
                        filter {
                            eq("UserID", FirebaseAuth.getInstance().currentUser?.uid ?: "")
                        }
                    }
                    .decodeList<UserGoal>()

                val entries = supabase.from("IncomeExpenseType")
                    .select {
                        filter { eq("UserID", FirebaseAuth.getInstance().currentUser?.uid ?: "") }
                    }
                    .decodeList<Entry>()

                val groupedSums = entries.groupBy { it.GoalID }
                    .mapValues { entry -> entry.value.sumOf { it.Amount } }

                val goalWithProgresses = response.map { goal ->
                    GoalWithProgress(
                        goalID = goal.GoalID ?: 0,
                        goalTitle = goal.GoalTitle,
                        Category = goal.Category,
                        IconUrl = goal.IconUrl,
                        MinimumAmount = goal.MinimumAmount,
                        MaximumAmount = goal.MaximumAmount,
                        totalAmount = groupedSums[goal.GoalID] ?: 0.0
                    )
                }

                updateBarChart(goalWithProgresses)

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to fetch goals! ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
                Log.i("TAG", "Error: ${e.localizedMessage}")
            }
        }
    }

    private fun updateBarChart(goals: List<GoalWithProgress>) {
        // Prepare entries for each data set
        val minEntries = mutableListOf<BarEntry>()
        val maxEntries = mutableListOf<BarEntry>()
        val totalEntries = mutableListOf<BarEntry>()

        goals.forEachIndexed { index, goal ->
            minEntries.add(BarEntry(index.toFloat(), goal.MinimumAmount.toFloat()))
            maxEntries.add(BarEntry(index.toFloat(), goal.MaximumAmount.toFloat()))
            totalEntries.add(BarEntry(index.toFloat(), goal.totalAmount.toFloat()))
        }

        val minDataSet = BarDataSet(minEntries, "Minimum")
        minDataSet.color = ContextCompat.getColor(requireContext(), R.color.orange) // define in colors.xml

        val maxDataSet = BarDataSet(maxEntries, "Maximum")
        maxDataSet.color = ContextCompat.getColor(requireContext(), R.color.red) // define in colors.xml

        val totalDataSet = BarDataSet(totalEntries, "Progress")
        totalDataSet.color = ContextCompat.getColor(requireContext(), R.color.green) // define in colors.xml

        // Create BarData with all three data sets
        val data = BarData(minDataSet, maxDataSet, totalDataSet)

        // Bar width and group spacing config
        val groupSpace = 0.2f
        val barSpace = 0.05f // space between bars in group
        val barWidth = (1f - groupSpace) / 3f - barSpace

        data.barWidth = barWidth

        goalsBarChart.data = data

        // X-axis config
        val xAxis = goalsBarChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.labelRotationAngle = 90f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return goals.getOrNull(value.toInt())?.goalTitle ?: ""
            }
        }

        // Group bars by x value — THIS IS IMPORTANT
        goalsBarChart.xAxis.axisMinimum = 0f
        goalsBarChart.xAxis.axisMaximum = goals.size.toFloat()
        goalsBarChart.groupBars(0f, groupSpace, barSpace)

        goalsBarChart.axisRight.isEnabled = false
        goalsBarChart.description.isEnabled = false
        goalsBarChart.setFitBars(true)
        goalsBarChart.setExtraOffsets(10f, 10f, 10f, 50f)
        goalsBarChart.animateY(800)

        // Refresh layout
        goalsBarChart.requestLayout()
        goalsBarChart.invalidate()
    }





    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChartGoalFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChartGoalFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}