package vcmsa.projects.titanbudget1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddCategoriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddCategoriesFragment : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var editCategoryName: EditText
    private lateinit var btnCreateCategory: Button
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton: RadioButton
    private lateinit var selectedIconImage: ImageView
    private lateinit var recyclerIcons: RecyclerView
    private lateinit var adapter: IconAdapter
    private lateinit var icons: List<Icon>
    var listener: OnIconSelectedListener? = null
    interface OnCategoryAddedListener {
        fun onCategoryAdded()
    }

    var categoryAddedListener: OnCategoryAddedListener? = null

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

        val view = inflater.inflate(R.layout.fragment_add_categories, container, false)

        editCategoryName = view.findViewById(R.id.editCategoryName)
        btnCreateCategory = view.findViewById(R.id.btnCreateCategory)
        selectedIconImage = view.findViewById(R.id.selectedIconImage)
        radioGroup = view.findViewById(R.id.radioGroup)
        recyclerIcons= view.findViewById(R.id.recyclerIcons)
        recyclerIcons.layoutManager = GridLayoutManager(requireContext(),3)

        loadIcons()

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            radioButton = view.findViewById(checkedId)
        }

        btnCreateCategory.setOnClickListener {
            val dbHelper = DatabaseHelper(requireContext())
            dbHelper.insertCategory(editCategoryName.text.toString(),radioButton.text.toString(),TempIconStorage.tempIconID.toString().toInt())
            Toast.makeText(requireContext(), "Category created", Toast.LENGTH_SHORT).show()
            categoryAddedListener?.onCategoryAdded()
            dismiss()
        }

        return view
    }

    private fun loadIcons() {
        val dbHelper = DatabaseHelper(requireContext())
        icons = dbHelper.getAllIcons()

        adapter = IconAdapter(requireContext(), icons) { selectedIcon ->
            Toast.makeText(requireContext(), "Clicked: ${selectedIcon.IconID}", Toast.LENGTH_SHORT).show()
            TempIconStorage.tempIconID = selectedIcon.IconID
            TempIconStorage.tempIconUrl = selectedIcon.IconUrl
            listener?.onIconSelected(selectedIcon.IconID,selectedIcon.IconUrl)
            Glide.with(requireContext())
                .load(TempIconStorage.tempIconUrl)
                .into(selectedIconImage)
        }

        recyclerIcons.adapter = adapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddCategoriesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddCategoriesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}