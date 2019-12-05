package edu.mep3343.battlebuddies

import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class Auth(private val activity: MainActivity) {
    companion object {
        val rcSignIn = 1
        // Choose authentication providers
        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )
        var user: FirebaseUser? = null
    }

    init {
        user = FirebaseAuth.getInstance().currentUser
        FirebaseAuth.AuthStateListener {
            user = FirebaseAuth.getInstance().currentUser
        }
        if (user == null) {
            // Create and launch sign-in intent
            activity.startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    // Was creating problems
                    .setIsSmartLockEnabled(false)
                    .setAvailableProviders(providers)
                    .build(),
                rcSignIn
            )
        }
    }
    fun getAccountName(): String? {
        return user?.displayName
    }
    fun getEmail(): String? {
        return user?.email
    }
    fun getUid(): String? {
        return user?.uid
    }

    fun setAccountName(displayName: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if( user == null ) return
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Auth", "User profile updated.")
                }
            }
    }
}
