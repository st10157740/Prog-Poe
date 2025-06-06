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
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
 * Use the [EntryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EntryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var startEntryDateButton: Button
    private lateinit var endEntryDateButton: Button
    private lateinit var entryAdapter: EntryAdapter
    private lateinit var addEntry: FloatingActionButton
    private lateinit var btnLoadEntry: Button
    private lateinit var btnResetEntry: Button
    private lateinit var viewEntries: RecyclerView
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_entry, container, false)

        addEntry = view.findViewById(R.id.addEntry)
        btnLoadEntry = view.findViewById(R.id.btnLoadEntry)
        btnResetEntry = view.findViewById(R.id.btnResetEntry)
        viewEntries = view.findViewById(R.id.viewEntries)
        startEntryDateButton = view.findViewById(R.id.startEntryDateButton)
        endEntryDateButton = view.findViewById(R.id.endEntryDateButton)

        addEntry.setOnClickListener {
            val entrySheet = EntrySheet()
            entrySheet.entryAddedListener = object : EntrySheet.OnEntryAddedListener {
                override fun onEntryAdded() {
                    loadEntries()
                }
            }
            entrySheet.show(parentFragmentManager, "ExpenseSheet")
        }

        startEntryDateButton.setOnClickListener {
            showDatePicker { date ->
                startDate = date
                startEntryDateButton.text = date.toString()
            }
        }

        endEntryDateButton.setOnClickListener {
            showDatePicker { date ->
                endDate = date
                endEntryDateButton.text = date.toString()
            }
        }

        btnLoadEntry.setOnClickListener {
            if (startDate != null && endDate != null) {
                loadExpenseInAPeriod(startDate!!, endDate!!)
            } else {
                Toast.makeText(requireContext(), "Please select start and end dates", Toast.LENGTH_SHORT).show()
            }
        }


        btnResetEntry.setOnClickListener {
            loadEntries()
        }

        loadEntries()

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

    private fun loadEntries(){
        val supabase = createSupabaseClient(
            supabaseUrl = "https://xoelotumrinspbbparpy.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhvZWxvdHVtcmluc3BiYnBhcnB5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU1MTg1OTcsImV4cCI6MjA2MTA5NDU5N30.3stfByRyFhnmYGD2JiNtNEuSccTW9xq0vbJAEkNKuJM"
        ) {
            install(Postgrest)
        }

        lifecycleScope.launch {
            try {
                val response = supabase.from("IncomeExpenseType")
                    .select {
                        filter {
                            eq("UserID", FirebaseAuth.getInstance().currentUser?.uid ?: "")
                        }
                    }
                    .decodeList<Entry>()
                entryAdapter = EntryAdapter(requireContext(),response) { selectedExpense ->
                    Toast.makeText(requireContext(), "Clicked: ${selectedExpense.Name}", Toast.LENGTH_SHORT).show()
                    TempEntryStorage.tempEntryName = selectedExpense.Name
                    TempEntryStorage.tempEntryDescription = selectedExpense.Description
                    TempEntryStorage.tempEntryAmount = selectedExpense.Amount.toString()
                    TempEntryStorage.tempEntryCategory = selectedExpense.Category
                    TempEntryStorage.tempEntryIcon = selectedExpense.IconUrl
                    TempEntryStorage.tempEntryDate = selectedExpense.Date
                    TempEntryStorage.tempEntryPhotoUri = selectedExpense.PhotoUrl
                    TempEntryStorage.tempEntryType = selectedExpense.Type
                    val moreInfoSheet = MoreInfoFragment()
                    moreInfoSheet.show(parentFragmentManager,"MoreInfoSheet")
                }
                entryAdapter.submitList(response)
                entryAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to fetch entries! ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
                Log.i(TAG, "Entry not added! ${e.localizedMessage}")
            }
            viewEntries.adapter = entryAdapter
            viewEntries.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun loadExpenseInAPeriod(start: LocalDate, end: LocalDate){
        val supabase = createSupabaseClient(
            supabaseUrl = "https://xoelotumrinspbbparpy.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhvZWxvdHVtcmluc3BiYnBhcnB5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU1MTg1OTcsImV4cCI6MjA2MTA5NDU5N30.3stfByRyFhnmYGD2JiNtNEuSccTW9xq0vbJAEkNKuJM"
        ) {
            install(Postgrest)
        }

        lifecycleScope.launch {
            try {
                val allEntries = supabase.from("IncomeExpenseType")
                    .select {
                        filter {
                            eq("UserID", FirebaseAuth.getInstance().currentUser?.uid ?: "")
                        }
                    }
                    .decodeList<Entry>()

                val filteredEntries = allEntries.filter { entries ->
                    try {
                        val expenseDate = LocalDate.parse(entries.Date)
                        !expenseDate.isBefore(start) && !expenseDate.isAfter(end)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing date: ${entries.Date}", e)
                        false
                    }
                }
                entryAdapter = EntryAdapter(requireContext(),filteredEntries) { selectedEntry ->
                    Toast.makeText(requireContext(), "Clicked: ${selectedEntry.Name}", Toast.LENGTH_SHORT).show()
                    TempEntryStorage.tempEntryName = selectedEntry.Name
                    TempEntryStorage.tempEntryDescription = selectedEntry.Description
                    TempEntryStorage.tempEntryAmount = selectedEntry.Amount.toString()
                    TempEntryStorage.tempEntryCategory = selectedEntry.Category
                    TempEntryStorage.tempEntryIcon = selectedEntry.IconUrl
                    TempEntryStorage.tempEntryDate = selectedEntry.Date.toString()
                    TempEntryStorage.tempEntryPhotoUri = selectedEntry.PhotoUrl
                    val moreInfoSheet = MoreInfoFragment()
                    moreInfoSheet.show(parentFragmentManager,"MoreInfoSheet")
                }
                entryAdapter.submitList(filteredEntries)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to fetch entries! ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
                Log.i(TAG, "Entries not added! ${e.localizedMessage}")
            }
            viewEntries.adapter = entryAdapter
            viewEntries.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExpenseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EntryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}