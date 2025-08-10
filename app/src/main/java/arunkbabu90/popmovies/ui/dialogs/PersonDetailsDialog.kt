package arunkbabu90.popmovies.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.data.api.IMG_SIZE_MID
import arunkbabu90.popmovies.data.api.TMDBClient
import arunkbabu90.popmovies.data.model.Person
import arunkbabu90.popmovies.data.repository.NetworkState
import arunkbabu90.popmovies.data.repository.PersonDetailsRepository
import arunkbabu90.popmovies.databinding.PersonDetailsDialogBinding
import arunkbabu90.popmovies.getImageUrl
import arunkbabu90.popmovies.ui.viewmodel.PersonDetailsViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.SlideDistanceProvider

class PersonDetailsDialog(private val person: Person,
                          private val isCast: Boolean) : DialogFragment(R.layout.person_details_dialog) {
    private var _binding: PersonDetailsDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: PersonDetailsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialFadeThrough().apply {
            duration = 500L
            secondaryAnimatorProvider = SlideDistanceProvider(Gravity.TOP)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.person_details_dialog, container, false)
        _binding = PersonDetailsDialogBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = TMDBClient.getClient()
        repository = PersonDetailsRepository(apiService)

        val bioBuilder = StringBuilder()
//        bioBuilder.append("Popularity: ${person.popularity}\n")

        val viewModel: PersonDetailsViewModel = getViewModel(person.id)
        viewModel.personDetails.observe(this, { personDetails ->
            val birthday = personDetails.birthday
            val deathDay = personDetails.deathDay
            val birthPlace = personDetails.placeOfBirth
            val bio = personDetails.biography

            if (birthday != "null" && !birthday.isNullOrBlank())
                bioBuilder.append("Born: ${birthday}\n")

            if (deathDay != "null" && !deathDay.isNullOrBlank())
                bioBuilder.append("Death: ${deathDay}\n")

            if (birthPlace != "null" && !birthPlace.isNullOrBlank())
                bioBuilder.append("Birth Place: ${birthPlace}\n\n")

            if (bio != "null" && bio.isNotBlank())
                bioBuilder.append(bio)

            binding.tvBio.text = bioBuilder.toString()
        })

        viewModel.networkState.observe(this, { state ->
            if (state == NetworkState.ERROR) {
                binding.pbBio.isVisible = false
            }

            if (state == NetworkState.LOADED) {
                binding.pbBio.isVisible = false
            }

            if (state == NetworkState.LOADING) {
                binding.pbBio.isVisible = true
            }
        })

        val dpUrl = getImageUrl(person.dpPath ?: "", IMG_SIZE_MID)

        Glide.with(this)
            .load(dpUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.default_dp)
            .into(binding.ivProfilePicture)

        binding.tvName.text = person.name

        if (isCast) {
            binding.tvSub.text = person.characterName
        } else {
            binding.tvSub.text = person.department
        }

        binding.btnBack.setOnClickListener {
            dismiss()
        }
    }

    private fun getViewModel(personId: Int): PersonDetailsViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PersonDetailsViewModel(repository, personId) as T
            }
        })[PersonDetailsViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}