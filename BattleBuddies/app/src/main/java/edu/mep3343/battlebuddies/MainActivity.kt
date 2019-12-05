package edu.mep3343.battlebuddies

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.FragmentTransaction
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import edu.mep3343.battlebuddies.NewPlayer.ChooseNameFrag
import edu.mep3343.battlebuddies.NewPlayer.HatchEggFrag

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var authAccount: Auth
    private lateinit var chooseNameFrag: ChooseNameFrag
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        db = FirebaseFirestore.getInstance()
        authAccount = Auth(this)
        val user = FirebaseAuth.getInstance().currentUser
        if(user == null)
            Log.d("BAD", "BAAAAAAAAAAAAAAAAAAAAD")
        else {
            Log.d("NOT NULL ", "USER IS STILL LOGGED IN")
            if(user.displayName == null)
                newPlayer()
            else {
                val userName = user.displayName
                val docRef = db.collection("battleBuddies").document(userName!!)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (!document.exists()) {
                            val hatchFrag = HatchEggFrag.newInstance()
                            supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.main_frame, hatchFrag)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .commit()
                        }
                        else{
                            val playFrag = HomeFragment.newInstance()
                            supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.main_frame, playFrag)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .commit()
                        }
                    }
                    .addOnFailureListener{ e ->
                            Log.w("MainActivity", "Error reading db", e)
                            this.finish()
                    }
            }
        }
    }

    private fun newPlayer(){
        chooseNameFrag = ChooseNameFrag.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_frame, chooseNameFrag)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Auth.rcSignIn) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                if(user != null) {
                    if(user.displayName == null)
                        newPlayer()
                    else {
                        val userName = user.displayName
                        val docRef = db.collection("battleBuddies").document(userName!!)
                        docRef.get()
                            .addOnSuccessListener { document ->
                                if (!document.exists()) {
                                    val hatchFrag = HatchEggFrag.newInstance()
                                    supportFragmentManager
                                        .beginTransaction()
                                        .replace(R.id.main_frame, hatchFrag)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                        .commit()
                                }
                                else{
                                    val playFrag = HomeFragment.newInstance()
                                    supportFragmentManager
                                        .beginTransaction()
                                        .replace(R.id.main_frame, playFrag)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                        .commit()
                                }
                            }
                            .addOnFailureListener{ e ->
                                Log.w("MainActivity", "Error reading db", e)
                                this.finish()
                            }
                    }
                }
            } else {
                Log.d("main", "login failed")
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.sign_out) {
            AuthUI.getInstance().signOut(this)
                .addOnCompleteListener{
                    this.startActivityForResult(
                        AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            // Was creating problems
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(Auth.providers)
                            .build(),
                        Auth.rcSignIn
                    )
                }
        }
        return super.onOptionsItemSelected(item)
    }
}
