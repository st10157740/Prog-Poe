package vcmsa.projects.titanbudget1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EducationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EducationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var articleButton: Button
    private lateinit var articleButton1: Button
    private lateinit var articleButton2: Button
    private lateinit var videoButton: Button

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

        val view = inflater.inflate(R.layout.fragment_education, container, false)

        articleButton = view.findViewById(R.id.articleButton)
        articleButton1 = view.findViewById(R.id.articleButton1)
        articleButton2 = view.findViewById(R.id.articleButton2)
        videoButton = view.findViewById(R.id.videoButton)

        articleButton.setOnClickListener {
            val articleUrl = "https://www.usa.gov/features/budgeting-to-meet-financial-goals"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl))
            startActivity(browserIntent)
        }

        articleButton1.setOnClickListener {
            val articleUrl = "https://srfs.upenn.edu/financial-wellness/browse-topics/budgeting/popular-budgeting-strategies"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl))
            startActivity(browserIntent)
        }

        articleButton2.setOnClickListener {
            val articleUrl = "https://www.northwestern.edu/financial-wellness/money-101/budgeting.html"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl))
            startActivity(browserIntent)
        }

        videoButton.setOnClickListener {
            val youtubeUrl = "https://youtu.be/_JBxP_oz2kQ?si=62v8YVcZQ0y8ex8c"
            val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
            startActivity(youtubeIntent)
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
         * @return A new instance of fragment EducationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EducationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}