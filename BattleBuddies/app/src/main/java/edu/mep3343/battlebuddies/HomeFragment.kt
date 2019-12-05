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

class HomeFragment: Fragment(){

    private lateinit var db: FirebaseFirestore
    private lateinit var userName: String
    private  var currentClicks: Int = 0
    private  var clicksNeeded: Int = 0
    private  var lastTimeFed: Date ?= null
    private var currentSpeed: Int = 0
    private var currentPower: Int = 0

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private fun loadBB(root: View){
        val theBBName = root.findViewById<TextView>(R.id.BB_name)
        val myBBImage = root.findViewById<ImageView>(R.id.my_BattleBuddy)
        val bbSpeed = root.findViewById<TextView>(R.id.speed_stat)
        val bbPower = root.findViewById<TextView>(R.id.power_stat)

        val docRef = db.collection("battleBuddies").document(userName)
        docRef.get()
            .addOnSuccessListener { document->
                if(document.exists()) {
                    val myBB = document.toObject(BattleBuddy::class.java)
                    theBBName.text = myBB!!.name
                    val whatType = myBB!!.type
                    if (whatType == "Party Boy")
                        myBBImage.setImageResource(R.drawable.bb_1)
                    else if (whatType == "Hopper")
                        myBBImage.setImageResource(R.drawable.bb_3)
                    else if (whatType == "Sad Boy")
                        myBBImage.setImageResource(R.drawable.bb_2)
                    else if (whatType == "Lover")
                        myBBImage.setImageResource(R.drawable.bb_5)
                    else if (whatType == "Angry Man")
                        myBBImage.setImageResource(R.drawable.bb_4)

                    bbSpeed.text = myBB.Speed.toString()
                    currentSpeed = myBB.Speed!!
                    bbPower.text = myBB.Power.toString()
                    currentPower = myBB.Power!!
                    currentClicks = myBB.speedClicks!!
                    clicksNeeded = myBB.speedClicksNeeded!!
                    lastTimeFed = myBB.lastFed
                }
                else{
                    Log.d("In Home Frag", "BIG PROBLEM, SOMEHOW IN HOME FRAG BUT THERE IS NO BATTLE BUDDY FOR THIS USER")
                }
            }

    }

    private fun initSpeedClicks(root: View){
        val speedStat = root.findViewById<TextView>(R.id.speed_stat)
        val myBBImage = root.findViewById<ImageView>(R.id.my_BattleBuddy)
        myBBImage.setOnClickListener {
            currentClicks++
            if(currentClicks >= clicksNeeded)
                updateSpeed()
            speedStat.text = currentSpeed.toString()
            val docRef = db.collection("battleBuddies").document(userName)
            docRef.update("speedClicks", currentClicks)
        }
    }

    private fun updateSpeed(){
        Toast.makeText(activity, "Your Battle Buddy got faster!", Toast.LENGTH_SHORT).show()
        currentSpeed++
        currentClicks = 0
        clicksNeeded *= 2
        val docRef = db.collection("battleBuddies").document(userName)
        docRef.update("speed", currentSpeed)
        docRef.update("speedClicksNeeded", clicksNeeded)
    }

    private fun initFeedBut(root: View){
        val theFeedBut = root.findViewById<Button>(R.id.feed_but)
        val powerStat = root.findViewById<TextView>(R.id.power_stat)
        theFeedBut.setOnClickListener {
            val currentTime = Calendar.getInstance().time
            if(lastTimeFed == null){
                updatePower(currentTime)
                powerStat.text = currentPower.toString()
            }
            else{
                if(currentTime.date > lastTimeFed!!.date) {
                    updatePower(currentTime)
                    powerStat.text = currentPower.toString()
                }
                else{
                    Toast.makeText(activity, "You can only feed your Battle Buddy once per day!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updatePower(currentDate: Date){
        currentPower++
        Toast.makeText(activity, "Your Battle Buddy got more powerful!", Toast.LENGTH_SHORT).show()
        val docRef = db.collection("battleBuddies").document(userName)
        docRef.update("power", currentPower)
        lastTimeFed = currentDate
        docRef.update("lastFed", lastTimeFed)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val user  = FirebaseAuth.getInstance().currentUser
        if(user == null)
            Log.d("In HomeFrag", "BAD USER SHOULD NOT BE NULL HERE")
        userName = user!!.displayName!!
        db = FirebaseFirestore.getInstance()
        loadBB(root)
        initSpeedClicks(root)
        initFeedBut(root)
        return root
    }

}