package vcmsa.projects.titanbudget1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewCategoriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewCategoriesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var btnAddCategory: Button
    private lateinit var recyclerCategories: RecyclerView
    private lateinit var adapter: ViewCategoriesAdapter
    private lateinit var categories: List<Category>

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

        val view = inflater.inflate(R.layout.fragment_view_categories, container, false)

        btnAddCategory = view.findViewById(R.id.btnAddCategory)
        recyclerCategories = view.findViewById(R.id.recyclerCategories)

        recyclerCategories.layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(
            recyclerCategories.context,
            LinearLayoutManager.VERTICAL
        )
        recyclerCategories.addItemDecoration(dividerItemDecoration)

        btnAddCategory.setOnClickListener {
            val addCategoriesSheet = AddCategoriesFragment()
            addCategoriesSheet.categoryAddedListener = object : AddCategoriesFragment.OnCategoryAddedListener {
                override fun onCategoryAdded() {
                    loadCategories()
                }
            }
            addCategoriesSheet.show(parentFragmentManager,"Add Categories Sheet")
        }

        loadCategories()

        return view
    }

    private fun loadCategories() {
        val dbHelper = DatabaseHelper(requireContext())
        categories = dbHelper.getAllCategories()

        adapter = ViewCategoriesAdapter(requireContext(), categories)
        adapter.notifyDataSetChanged()

        recyclerCategories.adapter = adapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewCategoriesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewCategoriesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}