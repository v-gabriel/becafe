package hr.ferit.gabrielveselovac.becafe

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import com.google.firebase.database.DatabaseError
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener


class ReviewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_review, container, false)

        val uuid: UUID = UUID.randomUUID()
        val randomUUIDString: String = uuid.toString()

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Users/${randomUUIDString}")

        val textReview = view.findViewById<EditText>(R.id.reviewText)
        val sendButton = view.findViewById<Button>(R.id.sendReviewButton)
        val backButton = view.findViewById<ImageButton>(R.id.backButtonReview)

        sendButton.setOnClickListener {

            if(textReview.text.isNotEmpty()) {

                reference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        reference.setValue(textReview.text.toString())

                        Toast.makeText(requireContext(),"Review sent successfully.", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            requireContext(),
                            "Fail to add data. \nError: $error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
            else {
                Toast.makeText(requireContext(),"Review cannot be blank.", Toast.LENGTH_SHORT).show()
            }
        }

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

        return view;
    }
}