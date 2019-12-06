package edu.mep3343.battlebuddies

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
import edu.mep3343.battlebuddies.NewPlayer.SpawnBBFrag
import java.util.*

class MatchMakingFrag: Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userName: String
    private lateinit var waitingText: TextView

    companion object {
        fun newInstance(): MatchMakingFrag {
            return MatchMakingFrag()
        }
    }

    private fun initSearch(){
        var foundMatch = false
        var otherPlayerName = ""
        db.collection("match_making")
            .whereEqualTo("player_count", 1)
            .limit(1)
            .get()
            .addOnSuccessListener { documents->
                for(document in documents){
                    foundMatch = true
                    val docRef = db.collection("match_making").document(document.id)
                    docRef.update("player_count", 2)
                    docRef.update("player_2", userName)
                    docRef.get().addOnSuccessListener { docSnapshot ->
                        val docData = docSnapshot.data
                        otherPlayerName = docData!!["player_1"]!!.toString()
                        Toast.makeText(
                            activity,
                            "Match found!, other player is " + otherPlayerName,
                            Toast.LENGTH_SHORT
                        ).show()
                        //docRef.delete()
                        //val docName = "battle"+(1..10000).random().toString()
                        val battleFrag = BattleFrag.newInstance(otherPlayerName, userName)
                        fragmentManager
                            ?.beginTransaction()
                            ?.remove(this)
                            ?.replace(R.id.main_frame, battleFrag)
                            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            ?.commit()
                    }
                }
                if(!foundMatch)
                    postMatch()
            }
    }

    private fun postMatch(){
        val matchToAdd = hashMapOf(
            "player_count" to 1,
            "player_1" to userName,
            "player_2" to ""
        )
        db.collection("match_making").document("open_match").set(matchToAdd)
            .addOnSuccessListener { documentReference ->
                Log.d("MMFrag", "DocumentSnapshot written ")
                waitingText.text="No matches currently posted, waiting for challenger"
            }
            .addOnFailureListener { e ->
                Log.w("MMFrag", "Error adding document", e)
            }

        val docRef = db.collection("match_making").document("open_match")
        docRef.addSnapshotListener{ snapshot, e->
            if (e != null) {
                Log.w("MMFrag", "Listen failed.", e)
                return@addSnapshotListener
            }
            //waitingText.text = "Docref updated"
            if (snapshot != null && snapshot.exists()) {
                val theData = snapshot.data
                var playerCount : Int = theData!!["player_count"].toString().toInt()
                //waitingText.text = "Snapshot exists, data is ${theData}"
                if(playerCount == 2){
                    //waitingText.text = "2 players in lobby! can start battle!"
                    val otherPlayerName = theData!!["player_2"].toString()
                    Log.d("STARTING BATTLE FRAG", "PLAYER 2 NAME IS " + otherPlayerName)
                    if(otherPlayerName != null && otherPlayerName != "") {
                        //waitingText.text = "Other player is " + otherPlayerName
                        val battleFrag = BattleFrag.newInstance(userName, otherPlayerName)
                        fragmentManager
                            ?.beginTransaction()
                            ?.remove(this)
                            ?.replace(R.id.main_frame, battleFrag)
                            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            ?.commit()
                    }
                    else{
                        //waitingText.text = "other player is null"
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_match_making, container, false)
        val user  = FirebaseAuth.getInstance().currentUser
        if(user == null)
            Log.d("In Match Making frag", "BAD USER SHOULD NOT BE NULL HERE")
        userName = user!!.displayName!!
        db = FirebaseFirestore.getInstance()
        waitingText = root.findViewById(R.id.waiting_text)
        initSearch()

        return root
    }

}