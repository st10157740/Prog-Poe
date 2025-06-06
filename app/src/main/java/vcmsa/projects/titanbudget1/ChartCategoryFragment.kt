package vcmsa.projects.titanbudget1

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChartCategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChartCategoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var expenseSummaryPieChart: PieChart
    private lateinit var startDateButtonChart: Button
    private lateinit var endDateButtonChart: Button
    private lateinit var loadSummaryButtonChart: Button
    private lateinit var loadingSpinnerChart: ProgressBar
    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null

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
        val view = inflater.inflate(R.layout.fragment_chart_category, container, false)

        expenseSummaryPieChart = view.findViewById(R.id.expenseSummaryPieChart)
        startDateButtonChart = view.findViewById(R.id.startDateButtonChart)
        endDateButtonChart = view.findViewById(R.id.endDateButtonChart)
        loadSummaryButtonChart = view.findViewById(R.id.loadSummaryButtonChart)
        loadingSpinnerChart = view.findViewById(R.id.loadingSpinnerChart)

        startDateButtonChart.setOnClickListener {
            showDatePicker { date ->
                startDate = date
                startDateButtonChart.text = date.toString()
            }
        }

        endDateButtonChart.setOnClickListener {
            showDatePicker { date ->
                endDate = date
                endDateButtonChart.text = date.toString()
            }
        }

        loadSummaryButtonChart.setOnClickListener {
            if (startDate != null && endDate != null) {
                loadExpenseSummary(startDate!!, endDate!!)
            } else {
                Toast.makeText(requireContext(), "Please select start and end dates", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun showDatePicker(onDateSelected: (LocalDate) -> Unit) {
        val today = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                onDateSelected(selectedDate)
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun loadExpenseSummary(start: LocalDate, end: LocalDate) {
        val supabase = createSupabaseClient(
            supabaseUrl = "https://xoelotumrinspbbparpy.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhvZWxvdHVtcmluc3BiYnBhcnB5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU1MTg1OTcsImV4cCI6MjA2MTA5NDU5N30.3stfByRyFhnmYGD2JiNtNEuSccTW9xq0vbJAEkNKuJM"
        ) {
            install(Postgrest)
        }

        lifecycleScope.launch {
            try {
                loadingSpinnerChart.visibility = View.VISIBLE

                val allCategory = supabase.from("IncomeExpenseType")
                    .select {
                        filter {
                            eq("UserID", FirebaseAuth.getInstance().currentUser?.uid ?: "")
                        }
                    }
                    .decodeList<Entry>()

                val filteredCategory = allCategory.filter { category ->
                    try {
                        val expenseDate = LocalDate.parse(category.Date)
                        !expenseDate.isBefore(start) && !expenseDate.isAfter(end)
                    } catch (e: Exception) {
                        Log.e("TotalCategorySummary", "Error parsing date: ${category.Date}", e)
                        false
                    }
                }

                val categoryTotals = filteredCategory.groupBy { it.Category }
                    .mapValues { entry ->
                        entry.value.sumOf { it.Amount }
                    }

                val categoryTotalList = categoryTotals.map { (category, total) ->
                    category to total
                }

                displayDataInPieChart(categoryTotalList)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to load total categories", Toast.LENGTH_SHORT).show()
            } finally {
                loadingSpinnerChart.visibility = View.GONE
            }
        }
    }

    private fun displayDataInPieChart(data: List<Pair<String, Double>>) {
        val entries = data.map { (category, amount) ->
            PieEntry(amount.toFloat(), category)
        }

        val dataSet = PieDataSet(entries, "Amount by Category Totals").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 14f
            valueTextColor = Color.WHITE
        }

        val pieData = PieData(dataSet)
        pieData.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "R%.2f".format(value)
            }
        })

        expenseSummaryPieChart.data = pieData
        expenseSummaryPieChart.description.isEnabled = false
        expenseSummaryPieChart.centerText = "Total Amount by Category"
        expenseSummaryPieChart.setEntryLabelColor(Color.BLACK)
        expenseSummaryPieChart.setUsePercentValues(false)
        expenseSummaryPieChart.animateY(1000)
        expenseSummaryPieChart.invalidate()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChartCategoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChartCategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}