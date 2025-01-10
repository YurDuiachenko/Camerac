package com.example.musting.ui.fragments

import com.example.musting.data.store.FileStorage
import SettingsStore
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.musting.R
import com.example.musting.databinding.FragmentSettingsBinding
import com.example.musting.ui.MainActivity
import kotlinx.coroutines.launch

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val store: SettingsStore by lazy { SettingsStore(requireContext()) }
    private val fileStorage: FileStorage by lazy { FileStorage(requireContext()) }

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displaySettings()

        binding.saveBtn.setOnClickListener {
            saveSettings()
            (activity as MainActivity).navigateSettingsToHome()
        }
    }

    override fun onResume() {
        super.onResume()
        displayHButton()
    }

    private fun displayHButton() {
        with(binding) {
            val publicFile = fileStorage.publicFile(Environment.DIRECTORY_DOCUMENTS, "currencies.txt")
            val file = fileStorage.file(Environment.DIRECTORY_DOCUMENTS, "currencies.txt")

            if (publicFile.exists()) {
                hBtn.text = "DELETE FILE"

                hBtn.setOnClickListener {
                    fileStorage.writeFile(file, fileStorage.readFile(publicFile))
                    publicFile.delete()

                    Toast.makeText(context, "File deleted", Toast.LENGTH_LONG).show()
                    onResume()
                }

            } else {
                hBtn.text = "RESTORE FILE"

                hBtn.setOnClickListener {
                    fileStorage.writeFile(publicFile, fileStorage.readFile(file))

                    Toast.makeText(context, "File restored", Toast.LENGTH_LONG).show()
                    onResume()
                }

            }
        }
    }

    private fun displaySettings() {
        with(binding) {
            with(store) {
                nameInput.setText(getUserName())
                darkModeSwitch.isChecked = getDarkMode()

                lifecycleScope.launch {
                    getEmail().collect { email ->
                        emailInput.setText(email)
                    }
                }

                lifecycleScope.launch {
                    getLanguage().collect { lang ->
                        langSwitch.isChecked = lang
                    }
                }
            }
        }
    }

    private fun saveSettings() {
        with(binding) {
            with(store) {
                saveUserName(nameInput.text.toString())
                saveDarkMode(darkModeSwitch.isChecked)

                lifecycleScope.launch {
                    saveEmail(emailInput.text.toString())
                }

                lifecycleScope.launch {
                    saveLanguage(langSwitch.isChecked)
                }
            }
        }
    }
}
