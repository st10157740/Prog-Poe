package vcmsa.projects.titanbudget1

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
 * Use the [TotalCategorySummaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TotalCategorySummaryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var startDateButton: Button
    private lateinit var endDateButton: Button
    private lateinit var loadSummaryButton: Button
    private lateinit var expenseRecyclerView: RecyclerView
    private lateinit var loadingSpinner: ProgressBar
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

        val view = inflater.inflate(R.layout.fragment_total_category_summary, container, false)

        startDateButton = view.findViewById(R.id.startDateButton)
        endDateButton = view.findViewById(R.id.endDateButton)
        loadSummaryButton = view.findViewById(R.id.loadSummaryButton)
        expenseRecyclerView = view.findViewById(R.id.expenseSummaryRecyclerView)
        loadingSpinner = view.findViewById(R.id.loadingSpinner)

        expenseRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        startDateButton.setOnClickListener {
            showDatePicker { date ->
                startDate = date
                startDateButton.text = date.toString()
            }
        }

        endDateButton.setOnClickListener {
            showDatePicker { date ->
                endDate = date
                endDateButton.text = date.toString()
            }
        }

        loadSummaryButton.setOnClickListener {
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
        ){
            install(Postgrest)
        }


        lifecycleScope.launch {
            try {
                loadingSpinner.visibility = View.VISIBLE

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
                        Log.e(TAG, "Error parsing date: ${category.Date}", e)
                        false
                    }
                }

                val categoryTotals = filteredCategory.groupBy { it.Category}
                    .mapValues { entry ->
                        entry.value.sumOf { it.Amount }
                    }

                val categoryTotalList = categoryTotals.map { (category, total) ->
                    category to total
                }

                expenseRecyclerView.adapter = TotalCategorySummaryAdapter(categoryTotalList)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to load total categories", Toast.LENGTH_SHORT).show()
            } finally {
                loadingSpinner.visibility = View.GONE
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TotalCategorySummaryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TotalCategorySummaryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

/*
   Code found in Chat-GPT
   Author: OpenAI
   Link: https://chatgpt.com/
   Accessed: 28 April 2025

   val supabase = createSupabaseClient(
    supabaseUrl = "your-url",
    supabaseKey = "your-key"
) {
    install(Postgrest)
}

lifecycleScope.launch {
    try {
        val result = supabase.from("Expense")
            .select(columns = "Category, ExpenseAmount, UserID, ExpenseDate")
            .decodeList<Expense>()

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val startDate = LocalDate.parse("2025-04-01") // example start
        val endDate = LocalDate.parse("2025-04-30")   // example end

        val totalsByCategory = result
            .filter {
                it.UserID == userId &&
                it.expenseDate.toLocalDate().isAfter(startDate.minusDays(1)) &&
                it.expenseDate.toLocalDate().isBefore(endDate.plusDays(1))
            }
            .groupBy { it.expenseCategory }
            .mapValues { entry ->
                entry.value.sumOf { it.expenseAmount }
            }

        totalsByCategory.forEach { (category, totalAmount) ->
            Log.i("Totals", "Category: $category, Total: $totalAmount")
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
)

*/

/*
   Code found in Chat-GPT
   Author: OpenAI
   Link: https://chatgpt.com/
   Accessed: 28 April 2025

   val expenses = supabase.from("expenses")
                .select()
                .gte("date", startDateStr)
                .lte("date", endDateStr)
                .decodeList<Expense>()

*/

/*
   Code found in Chat-GPT
   Author: OpenAI
   Link: https://chatgpt.com/
   Accessed: 28 April 2025

   private fun showDatePicker(onDateSelected: (LocalDate) -> Unit) {
        val today = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
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
*/