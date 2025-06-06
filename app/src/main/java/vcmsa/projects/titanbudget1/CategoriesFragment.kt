package vcmsa.projects.titanbudget1

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import vcmsa.projects.titanbudget1.placeholder.PlaceholderContent

/**
 * A fragment representing a list of Items.
 */
class CategoriesFragment : BottomSheetDialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var categories: List<Category>
    var listener: SelectedCategoryListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_categories_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewCategories)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())



        loadCategories()

        return view
    }

    private fun loadCategories() {
        val dbHelper = DatabaseHelper(requireContext())
        categories = dbHelper.getAllCategories()

        adapter = CategoryAdapter(requireContext(), categories) { selectedCategory ->
            Toast.makeText(requireContext(), "Clicked: ${selectedCategory.categoryName}", Toast.LENGTH_SHORT).show()
            TempStorage.tempText = selectedCategory.categoryName
            TempStorage.tempIcon = selectedCategory.iconUrl
            listener?.onCategorySelected(selectedCategory.categoryName,selectedCategory.iconUrl)
            dismiss()
        }

        recyclerView.adapter = adapter
    }
}