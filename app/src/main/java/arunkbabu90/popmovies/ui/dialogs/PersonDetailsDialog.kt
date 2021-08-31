package arunkbabu90.popmovies.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.DialogFragment
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.model.Person

class PersonDetailsDialog(private val person: Person) : DialogFragment(R.layout.person_details_dialog) {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }
}