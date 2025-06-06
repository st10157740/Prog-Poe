package vcmsa.projects.titanbudget1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var editLoginEmail: EditText
    private lateinit var editLoginPassword: EditText
    private lateinit var txtRegisterUser: TextView
    private lateinit var btnLogin: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        editLoginEmail = findViewById(R.id.editLoginEmail)
        editLoginPassword = findViewById(R.id.editLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtRegisterUser = findViewById(R.id.txtRegisterUser)

        val db = DatabaseHelper(this).writableDatabase
        databaseHelper = DatabaseHelper(this)

        val itemList = databaseHelper.getAllCategories()

        Log.d("DATABASE_TEST", "Fetched ${itemList.size} items")

        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            login()
        }
        txtRegisterUser.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    private fun login() {
        val email = editLoginEmail.text.toString()
        val password = editLoginPassword.text.toString()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                val itemList = databaseHelper.getAllCategories()

                Log.d("DATABASE_TEST", "Fetched ${itemList.size} items")
                Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, BudgetActivity::class.java)
                startActivity(intent)
            } else
                Toast.makeText(this, "Log In failed. Please enter 6 or more characters for password ", Toast.LENGTH_SHORT).show()
        }
    }
}