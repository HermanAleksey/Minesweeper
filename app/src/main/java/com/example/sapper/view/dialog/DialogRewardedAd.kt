package com.example.sapper.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.sapper.R
import com.example.sapper.view.activity.MinefieldActivity.activity.MinefieldActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class DialogRewardedAd : DialogFragment() {

    lateinit var listener: AdDialogListener

    private var mRewardedAd: RewardedAd? = null
    private var TAG = "DialogRewardedAd"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.layout_dialog_ad_watcher, null)

        val buttonConfirm = view.findViewById<Button>(R.id.button_ad_fragment_accept)
        val buttonCancel = view.findViewById<Button>(R.id.button_ad_fragment_cancel)

        val builder = AlertDialog.Builder(activity)

        builder.setView(view)

        /**-----------------------------------------------------------AD-------------------------------------------------*/
        var adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            context,
            "ca-app-pub-3940256099942544/5224354917",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    mRewardedAd = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mRewardedAd = rewardedAd
                }
            })

        mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d(TAG, "Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")
                // Called when ad is dismissed.
                // Don't set the ad reference to null to avoid showing the ad a second time.
                mRewardedAd = null
            }
        }
        /**--------------------------------------------------------------------------------------------------------------*/

        buttonConfirm.setOnClickListener {
            if (mRewardedAd != null) {
                mRewardedAd?.show(activity) {
                    listener.sendResponse(true)
                    dismiss()
                    Log.d(TAG, "User earned the reward.")
                }
            } else {
                Log.d(TAG, "The rewarded ad wasn't ready yet.")
            }
        }

        buttonCancel.setOnClickListener {
            listener.sendResponse(false)
            dismiss()
        }

        return builder.create()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as AdDialogListener
        } catch (ex: Exception) {
            throw ClassCastException(
                context.toString() +
                        "must implement AdDialogListener"
            );
        }
    }

    interface AdDialogListener {
        fun sendResponse(success: Boolean)
    }
}