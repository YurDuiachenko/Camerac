package com.example.musting.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.musting.R
import com.example.musting.databinding.FragmentSigninBinding
import com.example.musting.ui.MainActivity

class SignInFragment : Fragment(R.layout.fragment_signin) {
    private lateinit var binding: FragmentSigninBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSigninBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailInput = binding.emailInput
        val passInput = binding.passInput
        val logInBtn = binding.logInBtn
        val signUpBtn = binding.signUpBtn

        setDefaultInputValues(emailInput, passInput)

        logInBtn.setOnClickListener {
            val email = emailInput.text.toString()
            if (!isValidEmail(email) || passInput.text.isBlank()) {
                warnUser(logInBtn)
            } else if (email == "d.yurok22@mail.ru") {
                (activity as MainActivity).navigateSignInToHome()
            }
        }

        signUpBtn.setOnClickListener {
            (activity as MainActivity).navigateSignInToSignUp()
        }
    }

    private fun setDefaultInputValues(emailInput: EditText, passInput: EditText) {
        val args: SignInFragmentArgs by navArgs()
        if (args.user != null) {
            val user = args.user
            emailInput.setText(user?.email)
            passInput.setText(user?.pass)
        }
    }

    private fun warnUser(logInBtn: Button) {
        logInBtn.text = "Creds are invalid. Retype and login."
        logInBtn.setTextColor(Color.parseColor("#A02A38"))
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex())
    }
}
