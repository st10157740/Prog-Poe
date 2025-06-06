package vcmsa.projects.titanbudget1

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import yuku.ambilwarna.AmbilWarnaDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ThemeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ThemeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var btnSelectTheme: Button
    private lateinit var mainLayout: View
    private lateinit var prefs: SharedPreferences

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
        val view = inflater.inflate(R.layout.fragment_theme, container, false)

        btnSelectTheme = view.findViewById(R.id.btnSelectTheme)

        mainLayout = requireActivity().findViewById(R.id.drawer_layout)
        prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

        val savedColor = prefs.getInt("themeColor", Color.parseColor("#6200EE"))
        applyThemeColor(savedColor)

        btnSelectTheme.setOnClickListener {
            openColorPicker(savedColor)
        }

        return view
    }

    private fun openColorPicker(currentColor: Int) {
        val colorPicker = AmbilWarnaDialog(requireContext(), currentColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    prefs.edit().putInt("themeColor", color).apply()
                    applyThemeColor(color)
                }

                override fun onCancel(dialog: AmbilWarnaDialog?) {}
            })
        colorPicker.show()
    }

    private fun applyThemeColor(color: Int) {
        val adjustedColor = if (isDarkMode()) darkenColor(color, 0.8f) else color
        mainLayout.setBackgroundColor(adjustedColor)

        // Set status bar color via activity
        activity?.window?.statusBarColor = adjustedColor
    }

    private fun isDarkMode(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
    }

    private fun darkenColor(color: Int, factor: Float): Int {
        val r = (Color.red(color) * factor).toInt()
        val g = (Color.green(color) * factor).toInt()
        val b = (Color.blue(color) * factor).toInt()
        return Color.rgb(r, g, b)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ThemeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ThemeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}