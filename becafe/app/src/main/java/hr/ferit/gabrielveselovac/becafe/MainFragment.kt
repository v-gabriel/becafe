package hr.ferit.gabrielveselovac.becafe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val goButton = view.findViewById<Button>(R.id.findACafeButton)
        val helpButton = view.findViewById<Button>(R.id.helpButton)
        val rateButton = view.findViewById<Button>(R.id.rateButton)

        goButton.setOnClickListener {

            goButton.background = resources.getDrawable(R.drawable.primary_button_background_active)
            goButton.setTextColor(resources.getColor(R.color.primary_button_idle))
            val handler = Handler()
            handler.postDelayed(Runnable {
                goButton.setTextColor(resources.getColor(R.color.primary_button_pressed))
                goButton.background = resources.getDrawable(R.drawable.primary_button_background)
            }, 50)

            Dexter.withContext(requireContext())
                .withPermissions(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(object : MultiplePermissionsListener {

                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                        if(p0!!.areAllPermissionsGranted())
                        {
                            val mapFragment = MapFragment()

                            val fragmentTransaction: FragmentTransaction? =
                                activity?.supportFragmentManager?.beginTransaction()
                            fragmentTransaction?.setCustomAnimations(R.anim.enter_from_above,R.anim.exit_to_below)
                            fragmentTransaction?.replace(R.id.mainFragment, mapFragment)
                            fragmentTransaction?.commit()
                        }
                        if (p0.isAnyPermissionPermanentlyDenied) {
                            showExplanation()
                        }

                    }
                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        p1!!.continuePermissionRequest()
                    }
                }).check()
        }

        helpButton.setOnClickListener {

            helpButton.background = resources.getDrawable(R.drawable.secondary_button_background_active)
            helpButton.setTextColor(resources.getColor(R.color.secondary_button_idle))
            val handler = Handler()
            handler.postDelayed(Runnable {
                helpButton.setTextColor(resources.getColor(R.color.secondary_button_pressed))
                helpButton.background = resources.getDrawable(R.drawable.secondary_button_background)
            }, 50)

            val helpFragment = HelpFragment()

            val fragmentTransaction: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.setCustomAnimations(R.anim.enter_from_below,R.anim.exit_to_above)
            fragmentTransaction?.replace(R.id.mainFragment, helpFragment)
            fragmentTransaction?.commit()
        }

        rateButton.setOnClickListener {

            rateButton.background = resources.getDrawable(R.drawable.secondary_button_background_active)
            rateButton.setTextColor(resources.getColor(R.color.secondary_button_idle))
            val handler = Handler()
            handler.postDelayed(Runnable {
                rateButton.setTextColor(resources.getColor(R.color.secondary_button_pressed))
                rateButton.background = resources.getDrawable(R.drawable.secondary_button_background)
            }, 50)

            val reviewFragment = ReviewFragment()

            val fragmentTransaction: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.setCustomAnimations(R.anim.enter_from_below,R.anim.exit_to_above)
            fragmentTransaction?.replace(R.id.mainFragment, reviewFragment)
            fragmentTransaction?.commit()
        }

        return view
    }

    private fun showExplanation()
    {
        val builder = AlertDialog.Builder(requireContext())

        builder.apply {
            setMessage("Permission access is required for the app to function.")
            setTitle("Permission/s required")
            setPositiveButton("Settings") {
                    dialog, which ->

                val settingsIntent = Intent()

                val uri: Uri = Uri.fromParts("package", requireContext().packageName,null)

                settingsIntent.data = uri
                settingsIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                requireActivity().startActivity(settingsIntent)
            }
            setNegativeButton("Cancel"){
                    dialog, _ -> dialog.cancel()
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}


