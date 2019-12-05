package edu.mep3343.battlebuddies.NewPlayer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.text.trimmedLength
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import edu.mep3343.battlebuddies.Auth
import edu.mep3343.battlebuddies.R
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_name.*
import kotlin.random.Random
import edu.mep3343.battlebuddies.BattleBuddy

class SpawnBBFrag: Fragment() {

    private lateinit var db: FirebaseFirestore
    private var user: FirebaseUser? = null
    private var bbType: String? = null

    companion object {
        fun newInstance(): SpawnBBFrag {
            return SpawnBBFrag()
        }
    }

    private fun initSpawnTitle(root: View){
        val theTitle = root.findViewById<TextView>(R.id.spawner_title)
        if(bbType == null){
            Log.d("IN SPAWN FRAG", "THIS SHOULD NEVER HAPPEN, SHOULD HAVE ALWAYS ROLLED FOR BB TYPE BY NOW")
            activity?.finish()
        }
        theTitle.text= "Congratulations! You hatched a " + bbType + "\n Enter a name below!"
    }
    private fun initBB(root: View){
        if(bbType == null){
            Log.d("IN SPAWN FRAG", "THIS SHOULD NEVER HAPPEN, SHOULD HAVE ALWAYS ROLLED FOR BB TYPE BY NOW")
            activity?.finish()
        }
        val theBBImage = root.findViewById<ImageView>(R.id.spawned_BB)
        if(bbType == "Party Boy")
            theBBImage.setImageResource(R.drawable.bb_1)
        else if(bbType == "Hopper")
            theBBImage.setImageResource(R.drawable.bb_3)
        else if(bbType == "Sad Boy")
            theBBImage.setImageResource(R.drawable.bb_2)
        else if(bbType == "Lover")
            theBBImage.setImageResource(R.drawable.bb_5)
        else if(bbType == "Angry Man")
            theBBImage.setImageResource(R.drawable.bb_4)
        else{
            Log.d("IN SPAWN FRAG", "THIS SHOULD NEVER HAPPEN, SHOULD HAVE A VALID BB TYPE AND SET IMAGE")
            activity?.finish()
        }
    }

    private fun initContinueBut(root: View){
        val theBut = root.findViewById<Button>(R.id.continue_but)
        val theET = root.findViewById<EditText>(R.id.bb_namer)
        theBut.setOnClickListener {
            val bbName = theET.text.toString()
            if(bbName == null || checkEmpty(bbName))
                Toast.makeText(activity, "Battle Buddy name must not be empty", Toast.LENGTH_SHORT).show()
            else{
                Toast.makeText(activity, "Success!", Toast.LENGTH_SHORT).show()
                val ownerName = user!!.displayName.toString()
                val bbCreated = BattleBuddy(ownerName, bbName!!, bbType, 0, 0)
                db.collection("battleBuddies").document(ownerName)
                    .set(bbCreated)
                    .addOnSuccessListener {
                        Log.d("SpawnFrag", "DocumentSnapshot successfully written!")
                        //READY TO GO TO HOME FRAGMENT
                        val homeFrag = HomeFragment.newInstance()
                        fragmentManager
                            ?.beginTransaction()
                            ?.replace(R.id.main_frame, homeFrag)
                            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            ?.commit()
                    }
                    .addOnFailureListener {
                            e -> Log.w("Spanwn Frag", "Error writing document", e)
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
    private fun generateBB(){
        var dieRoll = (0..100).random()
        if(dieRoll >= 0 && dieRoll <=25)
            bbType = "Party Boy"
        else if(dieRoll >= 26 && dieRoll <= 50)
            bbType = "Hopper"
        else if(dieRoll >= 51 &&  dieRoll <=60)
            bbType = "Sad Boy"
        else if(dieRoll >= 61 && dieRoll <=65)
            bbType = "Lover"
        else if(dieRoll >= 66)
            bbType = "Angry Man"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_spawner, container, false)
        user = FirebaseAuth.getInstance().currentUser
        db = FirebaseFirestore.getInstance()
        if(user == null){
            //BAD SHOULD NOT HAPPEN
            Log.d("In spawner", "NO AUTHENTICATED USER!")
            activity?.finish()
        }
        generateBB()
        initSpawnTitle(root)
        initBB(root)
        initContinueBut(root)
        return root
    }

}