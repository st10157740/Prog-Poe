package vcmsa.projects.titanbudget1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MoreInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MoreInfoFragment : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var txtMoreInfoCategory: TextView
    private lateinit var txtMoreInfoName: TextView
    private lateinit var txtMoreInfoDescription: TextView
    private lateinit var txtMoreInfoAmount: TextView
    private lateinit var txtMoreInfoType: TextView
    private lateinit var txtMoreInfoDate: TextView
    private lateinit var txtMoreInfoNoPhoto: TextView
    private lateinit var moreInfoImage: ImageView
    private lateinit var moreInfoYesPhoto: ImageView
    private lateinit var btnCloseMoreInfo: Button

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

        val view = inflater.inflate(R.layout.fragment_more_info, container, false)

        txtMoreInfoName = view.findViewById(R.id.txtMoreInfoName)
        txtMoreInfoDescription = view.findViewById(R.id.txtMoreInfoDescription)
        txtMoreInfoAmount = view.findViewById(R.id.txtMoreInfoAmount)
        txtMoreInfoType = view.findViewById(R.id.txtMoreInfoType)
        txtMoreInfoCategory = view.findViewById(R.id.txtMoreInfoCategory)
        txtMoreInfoDate = view.findViewById(R.id.txtMoreInfoDate)
        txtMoreInfoNoPhoto = view.findViewById(R.id.txtMoreInfoNoPhoto)
        moreInfoImage = view.findViewById(R.id.moreInfoImage)
        moreInfoYesPhoto = view.findViewById(R.id.moreInfoYesPhoto)
        btnCloseMoreInfo = view.findViewById(R.id.btnCloseMoreInfo)

        txtMoreInfoName.text = TempEntryStorage.tempEntryName
        txtMoreInfoDescription.text = TempEntryStorage.tempEntryDescription
        txtMoreInfoAmount.text = "R"+TempEntryStorage.tempEntryAmount
        txtMoreInfoType.text = TempEntryStorage.tempEntryType
        txtMoreInfoCategory.text = TempEntryStorage.tempEntryCategory
        txtMoreInfoDate.text = TempEntryStorage.tempEntryDate
        Glide.with(requireContext())
            .load(TempEntryStorage.tempEntryIcon)
            .into(moreInfoImage)
        if (TempEntryStorage.tempEntryPhotoUri.equals("null")){
            txtMoreInfoNoPhoto.visibility = View.VISIBLE
        }
        else{
            Glide.with(requireContext())
                .load(TempEntryStorage.tempEntryPhotoUri)
                .into(moreInfoYesPhoto)
            moreInfoYesPhoto.visibility = View.VISIBLE
        }

        btnCloseMoreInfo.setOnClickListener {
            dismiss()
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
         * @return A new instance of fragment MoreInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MoreInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}