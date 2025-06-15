package com.example.taskmanager.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.taskmanager.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.calendar.CalendarScopes

class GoogleSignInManager(private val context: Context) {

    private var googleSignInClient: GoogleSignInClient? = null

    companion object {
        private const val TAG = "GoogleSignInManager"
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        if (googleSignInClient == null) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(CalendarScopes.CALENDAR))
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .build()

            googleSignInClient = GoogleSignIn.getClient(context, gso)
        }
        return googleSignInClient!!
    }

    fun revokeAccess(onComplete: () -> Unit) {
        Log.d(TAG, "Revoking access")
        getGoogleSignInClient().revokeAccess().addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "Access revoked successfully")

                googleSignInClient = null
            } else {
                Log.e(TAG, "Revoking access failed: ${it.exception?.message}", it.exception)
            }
            onComplete()
        }
    }
}