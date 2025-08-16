package gautam.projects.crm_assignment.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import gautam.projects.crm_assignment.R
import gautam.projects.crm_assignment.databinding.ActivityLoginScreenBinding

class LoginScreen : AppCompatActivity() {

    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth= FirebaseAuth.getInstance()

        binding.signUpButton.setOnClickListener {
            handleSignUp()
        }

        binding.loginButton.setOnClickListener {
            handleLogin()
        }
    }
    override fun onStart() {
        super.onStart()
        checkUserStatus()
    }

    private fun checkUserStatus(){
        val user = auth.currentUser
        if(user != null){
            navigateToHomeScreen()
        }
    }

    private fun handleSignUp(){
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this) {task ->

            if(task.isSuccessful){
                //toast to indicate successful account creation
                Toast.makeText(this,"Account Created Successfully",Toast.LENGTH_SHORT).show()
                navigateToHomeScreen()
            }else{
                //toast to indicate account creation failed
                Toast.makeText(this,"Account Creation Failed",Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun handleLogin(){
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        //TODO: validate email and password


        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this) {task->

            if(task.isSuccessful){
                //toast to indicate successful login
                Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show()
                navigateToHomeScreen()
            }else{
                //toast to indicate login failed
                Toast.makeText(this,"Login Failed",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToHomeScreen(){

        val intent= Intent(this, CustomerListActivity::class.java)

        startActivity(intent)

        //the best practice is to finish the activity once the user is logged in
        finish()
    }



}