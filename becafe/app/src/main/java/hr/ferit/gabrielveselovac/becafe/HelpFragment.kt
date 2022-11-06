package hr.ferit.gabrielveselovac.becafe

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class HelpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_help, container, false)

        val backButton = view.findViewById<ImageButton>(R.id.backButtonHelp)

        backButton.setOnClickListener {

            backButton.setBackgroundColor(resources.getColor(R.color.gray))
            val handler = Handler()
            handler.postDelayed(Runnable {
                backButton.setBackgroundColor(resources.getColor(R.color.transparent))
            }, 50)

            val mainFragment = MainFragment()

            val fragmentTransaction: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.setCustomAnimations(R.anim.enter_from_above,R.anim.exit_to_below)
            fragmentTransaction?.replace(R.id.mainFragment, mainFragment)
            fragmentTransaction?.commit()

        }

        return view
    }
}