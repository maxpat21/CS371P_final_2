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

class HatchEggFrag: Fragment() {

    private lateinit var background: FrameLayout
    private  var tapsToHatch: Int = 0
    private  var currentTaps: Int = 0
    private var currentEggPhase: Int? = null
    private lateinit var db: FirebaseFirestore
    private  var user: FirebaseUser? = null
    private var theEgg: ImageView? = null

    companion object {
        fun newInstance(): HatchEggFrag {
            return HatchEggFrag()
        }
    }

    private fun initEgg(root: View){
        theEgg!!.setOnClickListener{
            currentTaps++
            if(currentTaps >= tapsToHatch)
                updateEgg()

        }
    }

    private fun updateEgg() {
        val random = Random(3)
        currentTaps = 0
        if (currentEggPhase == null) {
            tapsToHatch = random.nextInt(1, 10)
            theEgg!!.setImageResource(R.drawable.egg_1_nobg)
            currentEggPhase = 1
        } else if (currentEggPhase == 1) {
            tapsToHatch = random.nextInt(5, 20)
            theEgg!!.setImageResource(R.drawable.egg_2_nobg)
            currentEggPhase = 2
        } else if (currentEggPhase == 2) {
            tapsToHatch = random.nextInt(10, 30)
            theEgg!!.setImageResource(R.drawable.egg_3_nobg)
            currentEggPhase = 3
        }  else if (currentEggPhase == 3) {
            tapsToHatch = random.nextInt(15, 40)
            theEgg!!.setImageResource(R.drawable.egg_4_nobg)
            currentEggPhase = 4
        }  else if (currentEggPhase == 4) {
            tapsToHatch = random.nextInt(20, 50)
            theEgg!!.setImageResource(R.drawable.egg_5_nobg)
            currentEggPhase = 5
        }  else if (currentEggPhase == 5) {
            tapsToHatch = random.nextInt(25, 50)
            theEgg!!.setImageResource(R.drawable.egg_6_nobg)
            currentEggPhase = 6
        }  else if (currentEggPhase == 6) {
            theEgg!!.isClickable=false
            hatchEgg()
        }
    }

    private fun hatchEgg(){
        val spawnFrag = SpawnBBFrag.newInstance()
        background.foreground.alpha = 200
        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.popup_box, spawnFrag)
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ?.commit()
    }

    private fun initTitle(root: View){
        val title_box = root.findViewById<TextView>(R.id.hatch_title)
        title_box.text = "Welcome " + user!!.displayName + "!\nTap to hatch egg"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_hatch, container, false)
        user = FirebaseAuth.getInstance().currentUser
        if(user == null){
            //BAD SHOULD NOT HAPPEN
            Log.d("In Hatch egg", "NO AUTHENTICATED USER!")
            activity?.finish()
        }
        theEgg = root.findViewById<ImageView>(R.id.egg)
        updateEgg()
        background = root.findViewById<FrameLayout>(R.id.background_frame)
        background.foreground.alpha = 0
        initTitle(root)
        initEgg(root)

        return root
    }


}



