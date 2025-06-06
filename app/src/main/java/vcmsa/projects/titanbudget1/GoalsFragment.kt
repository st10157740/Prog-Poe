package vcmsa.projects.titanbudget1

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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GoalsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GoalsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var btnAddGoals: Button
    private lateinit var goalAdapter: GoalAdapter
    private lateinit var GoalsView: RecyclerView
    private lateinit var minimumProgressBar: ProgressBar
    private lateinit var maximumProgressBar: ProgressBar

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
        val view =  inflater.inflate(R.layout.fragment_goals, container, false)

        btnAddGoals = view.findViewById(R.id.btnAddGoals)
        GoalsView = view.findViewById(R.id.GoalsView)


        btnAddGoals.setOnClickListener {
            val goalSheet = CreateGoalFragment()
            goalSheet.goalAddedListener = object : CreateGoalFragment.OnGoalAddedListener {
                override fun onGoalAdded() {
                    loadGoals()
                }
            }
            goalSheet.show(parentFragmentManager, "GoalSheet")
        }

        loadGoals()

        return view
    }

    private fun loadGoals(){
        goalAdapter = GoalAdapter(requireContext())

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

                goalAdapter.submitList(goalWithProgresses)
                goalAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to fetch goals! ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
                Log.i(TAG, "Goal not added! ${e.localizedMessage}")
            }
            GoalsView.adapter = goalAdapter
            GoalsView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GoalsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GoalsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}