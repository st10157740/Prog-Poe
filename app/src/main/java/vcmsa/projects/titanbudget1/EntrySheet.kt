package vcmsa.projects.titanbudget1

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import androidx.activity.result.ActivityResultLauncher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import org.slf4j.MDC.put

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EntrySheet.newInstance] factory method to
 * create an instance of this fragment.
 */
class EntrySheet : BottomSheetDialogFragment()  {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var btnSelectCategory: ConstraintLayout
    private lateinit var btnSelectGoal: ConstraintLayout
    private lateinit var editCamera: ImageView
    private lateinit var editGallery: ImageView
    private lateinit var displayImage: ImageView
    private lateinit var editEntryTitle: EditText
    private lateinit var editEntryDescription: EditText
    private lateinit var editEntryAmount: EditText
    private lateinit var txtEntryDate: TextView
    private lateinit var txtEntryCategory: TextView
    private lateinit var txtGoalSelectedEntry: TextView
    private lateinit var editDate: ImageView
    private lateinit var imageView3: ImageView
    private lateinit var imageView5: ImageView
    private lateinit var btnAddEntry: Button
    private lateinit var radioGroup2: RadioGroup
    private lateinit var radioButton: RadioButton
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2
    private lateinit var publicUrl: String
    private lateinit var cameraImageUri: Uri
    interface OnEntryAddedListener {
        fun onEntryAdded()
    }

    var entryAddedListener: OnEntryAddedListener? = null

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

        val view  = inflater.inflate(R.layout.fragment_entry_sheet, container, false)

        btnSelectCategory = view.findViewById(R.id.btnSelectCategory)
        btnSelectGoal = view.findViewById(R.id.btnSelectGoal)
        editCamera = view.findViewById(R.id.editCamera)
        editGallery = view.findViewById(R.id.editGallery)
        displayImage = view.findViewById(R.id.displayImage)
        editEntryTitle = view.findViewById(R.id.editEntryTitle)
        editEntryDescription = view.findViewById(R.id.editEntryDescription)
        editEntryAmount = view.findViewById(R.id.editEntryAmount)
        editDate = view.findViewById(R.id.editDate)
        btnAddEntry = view.findViewById(R.id.btnAddEntry)
        txtEntryDate = view.findViewById(R.id.txtEntryDate)
        txtEntryCategory = view.findViewById(R.id.textView8)
        txtGoalSelectedEntry = view.findViewById(R.id.txtGoalSelectedEntry)
        imageView3 = view.findViewById(R.id.imageView3)
        imageView5 = view.findViewById(R.id.imageView5)
        radioGroup2 = view.findViewById(R.id.radioGroup2)



        editCamera.setOnClickListener {
            cameraImageUri = createImageUri()!!
            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            }

            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE)
        }

        editGallery.setOnClickListener {
            val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE)
        }

        btnSelectCategory.setOnClickListener {
            val categorySheet = CategoriesFragment()
            categorySheet.listener = object : SelectedCategoryListener {
                override fun onCategorySelected(categoryName: String, iconUrl: String) {
                    txtEntryCategory.text= categoryName
                    Glide.with(requireContext())
                        .load(iconUrl)
                        .into(imageView3)
                }
            }
            categorySheet.show(parentFragmentManager, "CategorySheet")
        }

        btnSelectGoal.setOnClickListener{
            val selectGoalSheet = SelectGoalFragment()
            selectGoalSheet.listener = object : SelectedGoalListener {
                override fun onGoalSelected(goalTitle: String, iconUrl: String) {
                    txtGoalSelectedEntry.text= goalTitle
                    Glide.with(requireContext())
                        .load(iconUrl)
                        .into(imageView5)
                    lifecycleScope.launch{
                        val supabase = createSupabaseClient(
                            supabaseUrl = "https://xoelotumrinspbbparpy.supabase.co",
                            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhvZWxvdHVtcmluc3BiYnBhcnB5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU1MTg1OTcsImV4cCI6MjA2MTA5NDU5N30.3stfByRyFhnmYGD2JiNtNEuSccTW9xq0vbJAEkNKuJM"
                        ) {
                            install(Postgrest)
                            install(io.github.jan.supabase.storage.Storage)
                        }
                        val result = supabase.from("UserGoals")
                            .select {
                                filter {
                                    eq("UserID", FirebaseAuth.getInstance().currentUser?.uid ?: "")
                                    eq("GoalTitle", goalTitle)
                                }
                            }
                            .decodeSingle<UserGoal>()

                        TempEntryStorage.tempGoalID = result.GoalID
                    }
                }
            }
            selectGoalSheet.show(parentFragmentManager, "SelectGoalSheet")
        }

        editDate.setOnClickListener {
            val datePickerSheet = DatePickerFragment()
            datePickerSheet.listener = object : SelectedDateListener {
                override fun onDateSelected(date: String) {
                    txtEntryDate.text= date
                }
            }
            datePickerSheet.show(parentFragmentManager, "DatePickerSheet")
        }

        radioGroup2.setOnCheckedChangeListener { group, checkedId ->
            radioButton = view.findViewById(checkedId)
            TempEntryStorage.tempEntryType = radioButton.text.toString()
        }

        btnAddEntry.setOnClickListener {
            if (editEntryTitle.text.toString().isBlank() || editEntryAmount.text.toString().isBlank() ||
                editEntryDescription.text.toString().isBlank() || txtEntryDate.text.toString().equals("Select Date") ||
                TempEntryStorage.tempEntryType == null) {
                Toast.makeText(requireContext(), "Please fill all fields and image", Toast.LENGTH_SHORT).show()
            }
            else{
                val supabase = createSupabaseClient(
                    supabaseUrl = "https://xoelotumrinspbbparpy.supabase.co",
                    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhvZWxvdHVtcmluc3BiYnBhcnB5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU1MTg1OTcsImV4cCI6MjA2MTA5NDU5N30.3stfByRyFhnmYGD2JiNtNEuSccTW9xq0vbJAEkNKuJM"
                ) {
                    install(Postgrest)
                    install(io.github.jan.supabase.storage.Storage)
                }

                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val bucketName = "titan-budget-bucket"
                val fileName = "${userId}_${System.currentTimeMillis()}.jpg"

                try {

                } catch (e: Exception) {
                    e.printStackTrace()
                }



                lifecycleScope.launch {
                    try {
                        val inputStream = context?.contentResolver?.openInputStream(TempStorage.getUri)
                        val bytes = inputStream?.readBytes()
                        if (bytes != null) {

                            supabase.storage.from(bucketName).upload(fileName, bytes)
                            publicUrl = supabase.storage.from(bucketName).publicUrl(fileName)
                        }

                        val entry = Entry(
                            Name = editEntryTitle.text.toString(),
                            Description = editEntryDescription.text.toString(),
                            PhotoUrl = publicUrl,
                            Amount = editEntryAmount.text.toString().toDouble(),
                            Date = txtEntryDate.text.toString(),
                            Category = txtEntryCategory.text.toString(),
                            IconUrl = TempStorage.tempIcon.toString(),
                            UserID = userId,
                            Type = TempEntryStorage.tempEntryType.toString(),
                            GoalID = TempEntryStorage.tempGoalID
                        )


                        supabase.from("IncomeExpenseType").insert(entry)
                        Toast.makeText(requireContext(), "Entry Added!", Toast.LENGTH_SHORT).show()
                        entryAddedListener?.onEntryAdded()
                        dismiss()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Entry not added! ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                        Log.i(TAG, "Entry not added! ${e.localizedMessage}")
                    }
                }
            }

        }


        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    displayImage.setImageURI(cameraImageUri)
                    TempStorage.getUri = cameraImageUri
                }
                REQUEST_PICK_IMAGE -> {
                    val imageUri = data?.data
                    displayImage.setImageURI(imageUri)
                    TempStorage.tempExpensePhotoUri = imageUri.toString()
                    if (imageUri != null) {
                        TempStorage.getUri = imageUri
                    }
                }
            }
        }
    }

    private fun createImageUri(): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExpenseSheet.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EntrySheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
/* Code found in GeeksForGeeks
   Author: GeeksForGeeks
   Link: https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
   Accessed: 24 April 2025

ActivityResultLauncher<Intent> launchSomeActivity
    = registerForActivityResult(
        new ActivityResultContracts
            .StartActivityForResult(),
        result -> {
            if (result.getResultCode()
                == Activity.RESULT_OK) {
                Intent data = result.getData();
                // do your operation from here....
                if (data != null
                    && data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    Bitmap selectedImageBitmap;
                    try {
                        selectedImageBitmap
                            = MediaStore.Images.Media.getBitmap(
                                this.getContentResolver(),
                                selectedImageUri);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageView.setImageBitmap(
                        selectedImageBitmap);
                }
            }
        });
*/

/* Code found in GeeksForGeeks
   Author: GeeksForGeeks
   Link: https://www.geeksforgeeks.org/how-to-open-camera-through-intent-and-display-captured-image-in-android/
   Accessed: 24 April 2025

   class MainActivity : AppCompatActivity() {
    // Define the button and imageview type variable
    private lateinit var cameraOpenId: Button
    lateinit var clickImageId: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // By ID we can get each component which id is assigned in XML file get Buttons and imageview.
        cameraOpenId = findViewById(R.id.camera_button)
        clickImageId = findViewById(R.id.click_image)

        // Camera_open button is for open the camera and add the setOnClickListener in this button
        cameraOpenId.setOnClickListener(View.OnClickListener { v: View? ->
            // Create the camera_intent ACTION_IMAGE_CAPTURE it will open the camera for capture the image
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // Start the activity with camera_intent, and request pic id
            startActivityForResult(cameraIntent, pic_id)
        })
    }*/

/* Code found in Chat-GPT
   Author: OpenAI
   Link: https://chatgpt.com/
   Accessed: 24 April 2025

   class EntrySheet : BottomSheetDialogFragment() {

    interface OnEntryAddedListener {
        fun onEntryAdded()
    }

    var entryAddedListener: OnEntryAddedListener? = null

    // ...
}
val entrySheet = EntrySheet()
entrySheet.entryAddedListener = object : EntrySheet.OnEntryAddedListener {
    override fun onEntryAdded() {
        loadEntries() // Reload the entries from Supabase
    }
}
entrySheet.show(parentFragmentManager, "EntrySheet")
*/

/*
 Code found in Supabase
 Author: Supabase
 Link: https://supabase.com/docs/guides/getting-started/quickstarts/kotlin
 Accessed 28 April 2025

 import ...

val supabase = createSupabaseClient(
    supabaseUrl = "https://xyzcompany.supabase.co",
    supabaseKey = "your_public_anon_key"
  ) {
    install(Postgrest)
}
...
*/

/*
 Code found in Supabase
 Author: Supabase
 Link: https://supabase.com/docs/reference/kotlin/insert
 Accessed 28 April 2025

val city = City(name = "The Shire", countryId = 554)
supabase.from("cities").insert(city)
*/

/*  Code found in Supabase
    Author: Supabase
    Link: https://supabase.com/docs/reference/kotlin/select
 Accessed 28 April 2025

val city = supabase.from("cities").select().decodeSingle<City>()
*/

