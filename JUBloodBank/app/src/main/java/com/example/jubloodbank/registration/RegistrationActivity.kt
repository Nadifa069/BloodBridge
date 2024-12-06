package com.example.jubloodbank.registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.jubloodbank.HomePage.HomePageActivity
import com.example.jubloodbank.R
import com.example.jubloodbank.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengambil data dari strings.xml
        val bloodGroups = resources.getStringArray(R.array.blood)
        val regions = resources.getStringArray(R.array.hall)

        // Mengatur adapter untuk Spinner
        val bloodAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroups)
        val hallAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, regions)

        binding.bloodGroup.setAdapter(bloodAdapter)
        binding.edRegHall.setAdapter(hallAdapter)

        // Listener untuk tombol Submit
        binding.submit.setOnClickListener {
            val name = binding.edRegName.text.toString()
            val email = binding.edRegEmail.text.toString()
            val phone = binding.edPhoneNumber.text.toString()
            val hall = binding.edRegHall.text.toString()
            val batch = binding.edRegBatch.text.toString()
            val bloodGroup = binding.bloodGroup.text.toString()
            val password = binding.edRegPassword.text.toString()
            val confirmPassword = binding.edRegConfirmPassword.text.toString()

            // Validasi input
            when {
                name.isEmpty() -> binding.edRegName.error = "This field is required."
                email.isEmpty() -> binding.edRegEmail.error = "This field is required."
                phone.isEmpty() -> binding.edPhoneNumber.error = "This field is required."
                hall.isEmpty() -> binding.edRegHall.error = "This field is required."
                batch.isEmpty() -> binding.edRegBatch.error = "This field is required."
                bloodGroup.isEmpty() -> binding.bloodGroup.error = "This field is required."
                password.isEmpty() -> binding.edRegPassword.error = "This field is required."
                confirmPassword.isEmpty() -> binding.edRegConfirmPassword.error = "This field is required."
                !regions.contains(hall) -> binding.edRegHall.error = "Please select a valid region."
                password != confirmPassword -> Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                else -> {
                    val user = User(
                        name = name,
                        email = email,
                        phone = phone,
                        hallName = hall,
                        batch = batch,
                        Blood = bloodGroup
                    )

                    Registration(
                        user,
                        password,
                        this@RegistrationActivity
                    ) { success ->
                        if (success) {
                            startActivity(Intent(this, HomePageActivity::class.java))
                        }
                    }
                }
            }
        }
    }
}
