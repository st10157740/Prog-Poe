package vcmsa.projects.titanbudget1

import android.content.ContentValues.TAG
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
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
 * Use the [CreateGoalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateGoalFragment : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var txtGoalCategory: TextView
    private lateinit var imageView2: ImageView
    private lateinit var btnGoalCategory: ConstraintLayout
    private lateinit var btnCreateGoal: Button
    private lateinit var editAddGoalTitle: EditText
    private lateinit var editMinimumGoal: EditText
    private lateinit var editMaximumGoal: EditText
    interface OnGoalAddedListener {
        fun onGoalAdded()
    }

    var goalAddedListener: OnGoalAddedListener? = null

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
        val view = inflater.inflate(R.layout.fragment_create_goal, container, false)


        txtGoalCategory = view.findViewById(R.id.txtGoalCategory)
        imageView2 = view.findViewById(R.id.imageView2)
        btnCreateGoal = view.findViewById(R.id.btnCreateGoal)
        editAddGoalTitle = view.findViewById(R.id.editAddGoalTitle)
        editMinimumGoal = view.findViewById(R.id.editMinimumGoal)
        editMaximumGoal = view.findViewById(R.id.editMaximumGoal)
        btnGoalCategory = view.findViewById(R.id.btnGoalCategory)

        btnGoalCategory.setOnClickListener {
            val categorySheet = CategoriesFragment()
            categorySheet.listener = object : SelectedCategoryListener {
                override fun onCategorySelected(categoryName: String, iconUrl: String) {
                    txtGoalCategory.text= categoryName
                    Glide.with(requireContext())
                        .load(iconUrl)
                        .into(imageView2)
                }
            }
            categorySheet.show(parentFragmentManager, "CategorySheet")
            txtGoalCategory.text = TempStorage.tempText ?: "Select Category"
        }

        btnCreateGoal.setOnClickListener {
            val supabase = createSupabaseClient(
                supabaseUrl = "https://xoelotumrinspbbparpy.supabase.co",
                supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhvZWxvdHVtcmluc3BiYnBhcnB5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU1MTg1OTcsImV4cCI6MjA2MTA5NDU5N30.3stfByRyFhnmYGD2JiNtNEuSccTW9xq0vbJAEkNKuJM"
            ) {
                install(Postgrest)
            }
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            val userGoal = UserGoal(
                GoalTitle = editAddGoalTitle.text.toString(),
                Category = TempStorage.tempText.toString(),
                IconUrl = TempStorage.tempIcon.toString(),
                MinimumAmount = Integer.parseInt(editMinimumGoal.text.toString()),
                MaximumAmount = Integer.parseInt(editMaximumGoal.text.toString()),
                UserID = userId
            )

            lifecycleScope.launch {
                try {
                    supabase.from("UserGoals").insert(userGoal)
                    Toast.makeText(requireContext(), "Goal Added!", Toast.LENGTH_SHORT).show()
                    goalAddedListener?.onGoalAdded()
                    dismiss()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Goal not added! ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                    Log.i(TAG, "Goal not added! ${e.localizedMessage}")
                }
            }
        }


        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateGoalFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateGoalFragment().apply {
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

   val userGoal = UserGoal(
    GoalTitle = "The Shire",
    Category = "Exercise",
    IconUrl = "url",
    MinimumAmount = 50,
    MaximumAmount = 100,
    UserID = userId
)

*/