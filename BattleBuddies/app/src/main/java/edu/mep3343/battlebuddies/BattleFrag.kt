package edu.mep3343.battlebuddies

import android.hardware.camera2.CameraConstrainedHighSpeedCaptureSession
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
import android.animation.TimeAnimator
import androidx.lifecycle.ViewModelProviders


class BattleFrag(user1: String, user2: String): Fragment() {

    private lateinit var viewModel: MainViewModel
    var lastTimeUpdated = 0.toLong()
    private var whichPlayer: Int = 0
    private lateinit var db: FirebaseFirestore
    private var player1Name = user1
    private var player2Name = user2
   // private var theBattleDoc = battleDoc
    private var health1: Int = 100
    private var health2: Int = 100
    private var mySpeed: Int = 0
    private var myPower: Int = 0
    private var BB1thename = ""
    private var BB2thename = ""
    private lateinit var HP1: TextView
    private lateinit var HP2: TextView
    private lateinit var DMG1: TextView
    private lateinit var DMG2: TextView
    private lateinit var theAnimator: TimeAnimator



    companion object {
        fun newInstance(player1: String, player2: String): BattleFrag {
            return BattleFrag(player1, player2)
        }
    }

    private fun initViews(root:View){
        loadBB1(root)
        loadBB2(root)
    }

    private fun loadBB1(root: View){
        val BB_1_owner = root.findViewById<TextView>(R.id.owner_1)
        val BB_1_name = root.findViewById<TextView>(R.id.BB_name_1)
        val BB_1_speed = root.findViewById<TextView>(R.id.speed_1)
        val BB_1_power = root.findViewById<TextView>(R.id.power_1)
        val BB1 = root.findViewById<ImageView>(R.id.BB_1)
        val docRef = db.collection("battleBuddies").document(player1Name)
        docRef.get()
            .addOnSuccessListener { document->
                if(document.exists()) {
                    val myBB = document.toObject(BattleBuddy::class.java)
                    BB_1_name.text = myBB!!.name
                    BB1thename = myBB!!.name!!
                    BB_1_owner.text = myBB!!.owner
                    val whatType = myBB!!.type
                    if (whatType == "Party Boy")
                        BB1.setImageResource(R.drawable.bb_1)
                    else if (whatType == "Hopper")
                        BB1.setImageResource(R.drawable.bb_3)
                    else if (whatType == "Sad Boy")
                        BB1.setImageResource(R.drawable.bb_2)
                    else if (whatType == "Lover")
                        BB1.setImageResource(R.drawable.bb_5)
                    else if (whatType == "Angry Man")
                        BB1.setImageResource(R.drawable.bb_4)

                    BB_1_speed.text = myBB.Speed.toString()
                    BB_1_power.text = myBB.Power.toString()
                    if(whichPlayer == 1){
                        mySpeed = myBB.Speed!!
                        myPower = myBB.Power!!
                    }
                }
                else{
                    Log.d("In Battle Frag", "BIG PROBLEM, SOMEHOW IN HOME FRAG BUT THERE IS NO BATTLE BUDDY FOR THIS USER")
                }
            }
    }

    private fun loadBB2(root: View){
        val BB_2_owner = root.findViewById<TextView>(R.id.owner_2)
        val BB_2_name = root.findViewById<TextView>(R.id.BB_name_2)
        val BB_2_speed = root.findViewById<TextView>(R.id.speed_2)
        val BB_2_power = root.findViewById<TextView>(R.id.power_2)
        val BB2 = root.findViewById<ImageView>(R.id.BB_2)
        if(player2Name == null)
            return
        val docRef = db.collection("battleBuddies").document(player2Name)
        docRef.get()
            .addOnSuccessListener { document->
                if(document.exists()) {
                    val myBB = document.toObject(BattleBuddy::class.java)
                    BB_2_name.text = myBB!!.name
                    BB2thename = myBB!!.name!!
                    BB_2_owner.text = myBB!!.owner
                    val whatType = myBB!!.type
                    if (whatType == "Party Boy")
                        BB2.setImageResource(R.drawable.bb_1)
                    else if (whatType == "Hopper")
                        BB2.setImageResource(R.drawable.bb_3)
                    else if (whatType == "Sad Boy")
                        BB2.setImageResource(R.drawable.bb_2)
                    else if (whatType == "Lover")
                        BB2.setImageResource(R.drawable.bb_5)
                    else if (whatType == "Angry Man")
                        BB2.setImageResource(R.drawable.bb_4)

                    BB_2_speed.text = myBB.Speed.toString()
                    BB_2_power.text = myBB.Power.toString()
                    if(whichPlayer == 2){
                        mySpeed = myBB.Speed!!
                        myPower = myBB.Power!!
                    }
                }
                else{
                    Log.d("In Battle Frag", "BIG PROBLEM, SOMEHOW IN HOME FRAG BUT THERE IS NO BATTLE BUDDY FOR THIS USER")
                }
            }
    }

    private fun initData(){
        val initData = hashMapOf(
            "HP1" to 100,
            "HP2" to 100,
            "lastPlayer" to "",
            "lastMove" to 0
        )
        val docRef = db.collection("battles").document("battle1")
        docRef.get().addOnSuccessListener { document->
            if(document.data == null)
                docRef.set(initData)
        }
    }

    private fun initListener(){
        val docRef = db.collection("battles").document("battle1")
        docRef.addSnapshotListener{ snapshot, _ ->
            if(snapshot != null && snapshot.exists()){
                val theData = snapshot.data
                var newHP1 = theData!!["HP1"].toString()
                var newHP2 = theData!!["HP2"].toString()
                var playerMove = theData!!["lastPlayer"].toString()
                var dmgDealt = theData!!["lastMove"].toString()
                HP1.text = newHP1
                HP2.text = newHP2
                if(playerMove == player1Name){
                    DMG1.text = dmgDealt
                }
                else if(playerMove == player2Name){
                    DMG2.text = dmgDealt
                }
                if(newHP1.toInt() <= 0)
                    gameOver(player2Name)
                else if(newHP2.toInt() <= 0)
                    gameOver(player1Name)
            }
        }
    }

    private fun gameOver(winner: String){
        Toast.makeText(activity!!, "Game over! Winner is " + winner, Toast.LENGTH_SHORT).show()
        theAnimator.cancel()
        if(whichPlayer == 1) {
            val leaderRef = db.collection("leaderboard").document(winner)
            leaderRef.get()
                .addOnSuccessListener { document ->
                    if (document.data == null) {
                        val forLB = LeaderBoardEntry().apply {
                            player = winner
                            if (winner == player1Name)
                                bbName = BB1thename
                            else if (winner == player2Name)
                                bbName == BB2thename
                            wins = 1
                        }
                        db.collection("leaderboard").document(winner).set(forLB)
                    } else {
                        val LBEntry = document.toObject(LeaderBoardEntry::class.java)
                        var newWins = LBEntry!!.wins!!
                        newWins++
                        LBEntry.wins = newWins
                        db.collection("leaderboard").document(winner).set(LBEntry)
                    }
                }
        }
        val returnFrag = HomeFragment.newInstance()
        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.main_frame, returnFrag)
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ?.commit()
    }


    private fun startGame(){
        var timeToUpdate = (3000 - (mySpeed * 50)).toLong()
        var myTimeAnimator = TimeAnimator()
        myTimeAnimator.setTimeListener{ animation, totalTime, deltaTime ->
            var curTime = totalTime
            //DMG1.text = curTime.toString() + " " +lastTimeUpdated.toString() + " " + timeToUpdate.toString()
            if(curTime - lastTimeUpdated > timeToUpdate) {
                doMove()
                lastTimeUpdated = curTime
            }
        }
        theAnimator = myTimeAnimator
        theAnimator.start()
    }

    private fun doMove() {
        var myDMG = ((myPower*3)..(myPower*5)).random()
        val docRef = db.collection("battles").document("battle1")
        if (whichPlayer == 1) {
            health2 -= myDMG
            docRef.update("HP2", health2)
            docRef.update("lastPlayer", player1Name)
            docRef.update("lastMove", myDMG)
        } else if (whichPlayer == 2){
            health1 -= myDMG
        docRef.update("HP1", health1)
        docRef.update("lastPlayer", player2Name)
        docRef.update("lastMove", myDMG)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_battle, container, false)
        val user = FirebaseAuth.getInstance().currentUser
        if(user == null)
            Log.d("In HomeFrag", "BAD USER SHOULD NOT BE NULL HERE")
        val userName = user!!.displayName!!
        if (userName == player1Name)
            whichPlayer = 1
        else if (userName == player2Name)
            whichPlayer = 2
        db = FirebaseFirestore.getInstance()
        HP1 = root.findViewById<TextView>(R.id.hp_1)
        HP2 = root.findViewById(R.id.hp_2)
        DMG1 = root.findViewById<TextView>(R.id.dmg_1)
        DMG2 = root.findViewById(R.id.dmg_2)
        initViews(root)
        initData()
        initListener()
        startGame()
        return root
    }

}
