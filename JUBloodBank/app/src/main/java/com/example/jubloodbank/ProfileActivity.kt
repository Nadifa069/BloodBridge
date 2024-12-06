package com.example.jubloodbank

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.jubloodbank.databinding.ActivityMainBinding
import com.example.jubloodbank.databinding.ActivityProfileBinding
import com.example.jubloodbank.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AlertDialog


class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding
    val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val user: FirebaseUser? = auth.currentUser
        val userId=user!!.uid
        val database = FirebaseDatabase.getInstance()
        val userReference: DatabaseReference = database.getReference("users").child(userId)


        binding.requestBack.setOnClickListener {
            onBackPressed()
        }
        binding.support.setOnClickListener {
            showCustomerSupportOptions()
        }


//        binding.history.setOnClickListener {
//            Toast.makeText(this, "Not Yet Implemented", Toast.LENGTH_SHORT).show()
//        }
//        binding.settings.setOnClickListener {
//            Toast.makeText(this, "Not Yet Implemented", Toast.LENGTH_SHORT).show()
//        }


        lifecycleScope.launch {
            val fieldRef = userReference.child("name")

            userReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val fieldValue = dataSnapshot.child("name").getValue(String::class.java)
                        val blood = dataSnapshot.child("blood").getValue(String::class.java)
                        val phone = dataSnapshot.child("phone").getValue(String::class.java)
                        if (fieldValue != null) {
                            // Set the retrieved field value (name) to your TextView
                            binding.personName.text = fieldValue
                            binding.myblood.text=blood
                            binding.myphone.text=phone
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors or exceptions here
                }
            })
        }

        binding.logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this,LoginActivity::class.java))
        }

        binding.editprofile.setOnClickListener {

            startActivity(Intent(this,UpdateProfileActivity::class.java))
        }

    }
    // Fungsi untuk menampilkan opsi Customer Support
    private fun showCustomerSupportOptions() {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@example.com")).apply {
            putExtra(Intent.EXTRA_SUBJECT, "Customer Support Assistance")
        }

        val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:+6285239059393"))
        val whatsappIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/6285239059393"))

        val options = arrayOf("Email Support", "Call Support", "WhatsApp Chat")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Customer Support")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> startActivity(Intent.createChooser(emailIntent, "Send Email"))
                1 -> {
                    // Memastikan izin panggilan sudah diberikan
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(callIntent)
                    } else {
                        // Meminta izin CALL_PHONE jika belum diberikan
                        requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1001)
                    }
                }
                2 -> startActivity(whatsappIntent)
            }
        }
        builder.show()
    }

    // Callback untuk hasil permintaan izin
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) { // ID untuk izin CALL_PHONE
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:+6281234567890"))
                startActivity(callIntent)
            } else {
                Toast.makeText(this, "Izin panggilan tidak diberikan.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}