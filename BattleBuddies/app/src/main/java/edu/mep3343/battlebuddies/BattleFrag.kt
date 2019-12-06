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

class BattleFrag(user1: String, user2: String): Fragment() {

    private lateinit var db: FirebaseFirestore
    private var player1Name = user1
    private var player2Name = user2
    private var health1: Int = 100
    private var health2: int = 100
    private lateinit var HP1: TextView
    private lateinit var HP2: TextView
    private lateinit var DMG1: TextView
    private lateinit var DMG2: TextView



    companion object {
        fun newInstance(player1: String, player2: String): BattleFrag {
            return BattleFrag(player1, player2)
        }
    }

    initViews(root:View){
        loadBB1(root)
    }

    loadBB1(root: View){
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

                    BB_1_Speed.text = myBB.Speed.toString()
                    BB_1_Power.text = myBB.Power.toString()
                }
                else{
                    Log.d("In Battle Frag", "BIG PROBLEM, SOMEHOW IN HOME FRAG BUT THERE IS NO BATTLE BUDDY FOR THIS USER")
                }
            }
    }


}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_battle, container, false)
        val user  = FirebaseAuth.getInstance().currentUser
        if(user == null)
            Log.d("In Match Making frag", "BAD USER SHOULD NOT BE NULL HERE")
        db = FirebaseFirestore.getInstance()
        HP1 = root.findViewById<TextView>(R.id.hp_1)
        HP2 = root.findViewById(R.id.hp_2)
        DMG1 = root.findViewById<TextView>(R.id.dmg_1)
        DMG2 = root.findViewById(R.id.dmg_2)
        initViews(root)

        return root
    }

}
