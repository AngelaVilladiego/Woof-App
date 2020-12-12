package com.example.prog20082_project_av_jh

import android.R.*
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.prog20082_project_av_jh.model.User
import com.example.prog20082_project_av_jh.utils.CheckViewable
import com.example.prog20082_project_av_jh.views.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.swipe_half_fragment.view.*
import java.text.SimpleDateFormat


class HalfSwipeFragment : Fragment(), View.OnClickListener, CheckViewable {

    private lateinit var cardView: CardView
    private lateinit var fabLike: FloatingActionButton
    private lateinit var fabDislike: FloatingActionButton
    private lateinit var tvDogName: TextView
    private lateinit var tvDogAge: TextView
    private lateinit var tvDogGender: TextView
    private lateinit var tvDogSize: TextView
    private lateinit var tvDogBreed: TextView
    private lateinit var mainActivity: MainActivity
    private lateinit var currentUser: User

    private val TAG =  this@HalfSwipeFragment.toString()

    private lateinit var viewModel: SwipeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.swipe_half_fragment, container, false)

        cardView = root.card_view
        fabLike = root.fabLike
        fabDislike = root.fabDislike
        tvDogName = root.tvDogName
        tvDogAge = root.tvDogAge
        tvDogSize = root.tvDogSize
        tvDogGender = root.tvDogGender
        tvDogBreed= root.tvBreed

        cardView.setOnClickListener(this)
        fabLike.setOnClickListener(this)
        fabDislike.setOnClickListener(this)

        //get main activity for access to public fields and functions
        mainActivity = activity as MainActivity

        //get index of currently showing user to continue iterating through at same spot
//        currentIndex = mainActivity.userList.indexOf(mainActivity.showingProfile)
//
        if (mainActivity.currUserEmail != null){
            mainActivity.userViewModel.getUserByEmail(mainActivity.currUserEmail!!)?.observe(this.requireActivity(), {matchedUser ->

                if (matchedUser != null) {
                    this.currentUser = matchedUser

                    Log.d("Profile Fragment", "Matched user : " + matchedUser.toString())
                }
            })
        }

        this.changeInfo()

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SwipeViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                cardView.id -> {
                    Log.e(
                       TAG,"CARD FRAGMENT CLICKED ++++++++++++++++++++++++++++"
                    )
                    changeToFullFragment()
                }
                fabLike.id -> {
                    this.likeProfile()
                    this.switchCard()
                }
                fabDislike.id -> {
                    Log.e(TAG, "++++++++++++++ DISLIKED +++++++++++++++++++")
                    this.switchCard()
                }
            }
        }
    }

    private fun changeToFullFragment() {
        Navigation.findNavController(requireView()).navigate(R.id.action_nav_swipe_half_to_nav_swipe_full)
    }

    private fun likeProfile() {
        Log.e(TAG, "+++++++++++++++ LIKED ++++++++++++++++")
    }

    private fun switchCard() {
        Log.e(TAG, "++++++ switching cards.......  ++++++++++")

        //Animate out, when finished change info, when changed animate back in
        var anim = AnimationUtils.loadAnimation(this.requireContext(), R.anim.slide_out)

        anim.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                //not needed
            }

            override fun onAnimationEnd(animation: Animation?) {
                //after finished, change data, then animate in.
                this@HalfSwipeFragment.changeInfo()
                this@HalfSwipeFragment.slideViewIn()
            }

            override fun onAnimationRepeat(animation: Animation?) {
                //not needed
            }
        })

        cardView.startAnimation(anim)

    }

    private fun slideViewIn() {
        var anim = AnimationUtils.loadAnimation(this.requireContext(), R.anim.slide_in)
        cardView.startAnimation(anim)
    }

    private fun changeInfo() {

        mainActivity.userViewModel.allUsers.observe(viewLifecycleOwner, {users ->

            try {
                //if user is not viewable, then try again with the next user
                if (users[mainActivity.index] != null) {
                    if (isViewable(users[mainActivity.index], currentUser)) {
                        tvDogName.setText(users[mainActivity.index].dName)
                        tvDogAge.setText(users[mainActivity.index].age.toString() + " yrs")
                        tvDogGender.setText(users[mainActivity.index].gender)
                        tvDogBreed.setText(users[mainActivity.index].breed)
                        if (users[mainActivity.index].dogSize.equals("")) {
                            tvDogSize.setText("?")
                        } else {
                            tvDogSize.setText(users[mainActivity.index].dogSize + " lbs")
                        }
                        mainActivity.index += 1
                    }  else {
                        mainActivity.index += 1
                        changeInfo()
                    }
                }
            } catch (ex: IndexOutOfBoundsException) {
                Toast.makeText(this.requireActivity(), "Last dog reached", Toast.LENGTH_SHORT)
            }

        })


//        //populate info based on thisuser
//        tvDogName.setText(mainActivity.showingProfile.dName)
//        tvDogAge.setText(mainActivity.showingProfile.age.toString())
//        tvDogGender.setText(mainActivity.showingProfile.gender)
//        tvDogBreed.setText(mainActivity.showingProfile.breed)
//        tvDogSize.setText(mainActivity.showingProfile.breed)
//
//        //update index, update showing profile
//        currentIndex++
//        mainActivity.showingProfile = mainActivity.userList[currentIndex]
    }

}