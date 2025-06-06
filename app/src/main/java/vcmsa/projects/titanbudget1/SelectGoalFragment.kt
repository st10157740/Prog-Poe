package vcmsa.projects.titanbudget1

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
 * Use the [SelectGoalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectGoalFragment : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var goalAdapter: SelectGoalAdapter
    private lateinit var recyclerViewSelectGoal: RecyclerView
    private lateinit var selectGoals: List<UserGoal>
    var listener: SelectedGoalListener? = null

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

        val view = inflater.inflate(R.layout.fragment_select_goal_list, container, false)

        recyclerViewSelectGoal = view.findViewById(R.id.recyclerViewSelectGoal)

        loadGoals()

        return view
    }

    private fun loadGoals(){
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

                goalAdapter = SelectGoalAdapter(requireContext(), response) { selectedGoal ->
                    Toast.makeText(requireContext(), "Clicked: ${selectedGoal.GoalTitle}", Toast.LENGTH_SHORT).show()
                    listener?.onGoalSelected(selectedGoal.GoalTitle, selectedGoal.IconUrl)
                    dismiss()
                }

                recyclerViewSelectGoal.adapter = goalAdapter
                recyclerViewSelectGoal.layoutManager = LinearLayoutManager(requireContext())

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to fetch goals! ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "Goal not added! ${e.localizedMessage}")
                e.printStackTrace()
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
         * @return A new instance of fragment SelectGoalFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SelectGoalFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}