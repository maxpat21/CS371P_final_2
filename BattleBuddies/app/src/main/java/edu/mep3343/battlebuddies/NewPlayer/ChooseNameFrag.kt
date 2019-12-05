package edu.mep3343.battlebuddies.NewPlayer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.text.trimmedLength
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import edu.mep3343.battlebuddies.R
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_name.*

class ChooseNameFrag: Fragment(){

    private lateinit var db: FirebaseFirestore

    companion object {
        fun newInstance(): ChooseNameFrag {
            return ChooseNameFrag()
        }
    }

    private fun initSubmitBut(root: View){
        val submitBut = root.findViewById<Button>(R.id.submit_name)
        val nameET = root.findViewById<EditText>(R.id.enter_name)
        submitBut.setOnClickListener{
            val name = nameET.text.toString()
            if(name == null || checkEmpty(name))
                Toast.makeText(activity, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            else {
                val chosenName = db.collection("accountNames").document(name)
                chosenName.get()
                    .addOnSuccessListener { document ->
                        Log.d("CHECKING FIRESTORE COILLECTION ACCOUNT NAMES", "SUCCESSSSSSSSSSSSSSSSSSSSSSSS")
                        if (document.data != null) {
                            Log.d("DOCUMENT SHOULD ALREADY EXIST IN THE COLLECTION", "WE SHOULD NOT BE HERRRRRRRRRRRRRRRRRRRRE doc name is ${name}")
                            Log.d("DOCDATA IS ", "${document.data}")
                            Toast.makeText(activity, "Account Name already taken, choose another", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, "Success!", Toast.LENGTH_SHORT).show()
                            var data = hashMapOf("taken" to true)
                            chosenName
                                .set(data)
                                .addOnSuccessListener { Log.d("adding", "DocumentSnapshot successfully written!")
                                    //Player has selected their username
                                    //now we need to connect it to display name, neither should ever be changed later
                                    val user = FirebaseAuth.getInstance().currentUser
                                    if( user == null ){
                                        //THIS SHOULD NEVER HAPPEN, CAN NEVER BE AT THIS STATE UNLESS YOU ARE SIGNED IN
                                        activity?.finish()
                                    }
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build()
                                    user!!.updateProfile(profileUpdates)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Log.d("In Choose name frag", "Display name successfuly changed, should never be done again")
                                                //Now that you have an account name, ready to hatch an egg, we know you don't have a battle buddy because you were not registered
                                                val hatchFrag = HatchEggFrag.newInstance()
                                                fragmentManager
                                                    ?.beginTransaction()
                                                    ?.replace(R.id.main_frame, hatchFrag)
                                                    ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                                    ?.commit()
                                            }
                                        }
                                }
                                .addOnFailureListener { e -> Log.w("failed add", "Error writing document", e) }

                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("get failed with ", "exception")
                        activity?.finish()
                    }
            }
        }
    }

    private fun checkEmpty(name: String): Boolean{
        if(name.length == 0)
            return true
        for(letter in name){
            if(letter != ' ')
                return false
        }
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_name, container, false)
        db = FirebaseFirestore.getInstance()
        initSubmitBut(root)
        return root
    }
}