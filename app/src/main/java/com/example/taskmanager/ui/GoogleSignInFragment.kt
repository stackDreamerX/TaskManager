package com.example.taskmanager.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.taskmanager.CalendarFragment
import com.example.taskmanager.R
import com.example.taskmanager.TaskApplication
import com.example.taskmanager.databinding.FragmentGoogleSigninBinding
import com.example.taskmanager.viewmodel.TaskViewModel
import com.example.taskmanager.viewmodel.TaskViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

class GoogleSignInFragment : Fragment() {

    private var _binding: FragmentGoogleSigninBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModels {
        val app = requireActivity().application as TaskApplication
        TaskViewModelFactory(app.repository, app)
    }

    companion object {
        private const val TAG = "GoogleSignInFragment"
    }

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)

            // Handle successful sign-in
            viewModel.handleSignInResult(task)
            Toast.makeText(requireContext(), "Sign-in successful", Toast.LENGTH_SHORT).show()

            // Navigate to calendar fragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, CalendarFragment())
                .commit()
        } catch (e: ApiException) {
            Log.e(TAG, "Sign-in failed: ${e.statusCode}", e)
            Toast.makeText(requireContext(), "Sign-in failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoogleSigninBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInButton.setOnClickListener {
            val signInIntent = viewModel.getSignInIntent()
            signInLauncher.launch(signInIntent)
        }

        binding.fixGooglePlayButton.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms")))
            } catch (e: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms")))
            }
        }

        checkGooglePlayServices()
    }

    private fun checkGooglePlayServices() {
        val app = requireActivity().application as TaskApplication
        val isAvailable = app.authManager.isGooglePlayServicesAvailable(requireActivity())

        if (!isAvailable) {
            Log.d(TAG, "Google Play Services not available or outdated")
            binding.errorText.visibility = View.VISIBLE
            binding.errorText.text = "Google Play Services cần được cập nhật"
            binding.fixGooglePlayButton.visibility = View.VISIBLE
        } else {
            Log.d(TAG, "Google Play Services is available and up to date")
            binding.fixGooglePlayButton.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}