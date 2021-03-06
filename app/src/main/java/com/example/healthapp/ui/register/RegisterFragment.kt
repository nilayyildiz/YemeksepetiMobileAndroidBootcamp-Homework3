package com.example.healthapp.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthapp.data.entity.user.User
import com.example.healthapp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private lateinit var mFireStore: FirebaseFirestore

    private var bloodGroups = arrayOf<String?>("A+", "A-", "B-", "B+", "AB-", "AB+", "0-", "0+")

    private lateinit var bloodGroup: String

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        binding.apply {
            btnRegister.setOnClickListener {
                val email = editTextEmail.text.toString()
                if (email.isEmpty()) {
                    editTextEmail.error = "please enter your email"
                }
                val password = editTextPassword.text.toString()
                if (password.isEmpty()) {
                    editTextPassword.error = "please enter your password"
                }
                val name = editTextName.text.toString()
                val weight = editTextWeight.text.toString()
                val height = editTextHeight.text.toString()
                val bloodGroup = bloodGroup
                if (!(email.isEmpty() || password.isEmpty())) {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val firebaseUser: FirebaseUser = it.result!!.user!!
                            val user =
                                User(firebaseUser.uid, name, email, height, weight, bloodGroup)
                            viewModel.saveUserToFirebase(user)
                            val action =
                                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                            findNavController().navigate(action)
                        }
                    }.addOnFailureListener {
                        println("not successful")
                        println(it)
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.spinner.onItemSelectedListener = this

        val ad: ArrayAdapter<*> =
            ArrayAdapter<Any?>(requireContext(), android.R.layout.simple_spinner_item, bloodGroups)

        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = ad
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        bloodGroup = bloodGroups[position].toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        println("On Nothing Selected")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}