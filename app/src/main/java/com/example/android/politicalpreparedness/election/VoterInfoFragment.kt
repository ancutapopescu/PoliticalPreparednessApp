package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private lateinit var viewModel: VoterInfoViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {


//TODO: Add ViewModel values and create ViewModel
    val bundle = VoterInfoFragmentArgs.fromBundle(requireArguments())
    val electionId = bundle.argumentElectionId
    val division = bundle.argumentDivision

    val application = requireNotNull(this.activity).application
    val dataSource = ElectionDatabase.getInstance(application).electionDao
    val viewModelFactory = VoterInfoViewModelFactory(dataSource, electionId, division)
    viewModel = ViewModelProvider(this, viewModelFactory).get(VoterInfoViewModel::class.java)

//TODO: Add binding values
     val binding: FragmentVoterInfoBinding =
             DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

//TODO: Populate voter info -- hide views without provided data.
/**
Hint: You will need to ensure proper data is provided from previous fragment.
*/

//TODO: Handle loading of URLs
// Voting Locations
    viewModel.votingLocationUrl.observe(viewLifecycleOwner, Observer {
            it?.let {
                loadUrl(it)
                viewModel.votingLocationsNavigated()
            }
        })

    // Ballot Information
    viewModel.ballotInformationUrl.observe(viewLifecycleOwner, Observer {
            it?.let {
                loadUrl(it)
                viewModel.ballotInformationNavigated()
            }
        })

//TODO: Handle save button UI state
//TODO: cont'd Handle save button clicks
    viewModel.isElectionFollowed.observe(viewLifecycleOwner, Observer {  isElectionFollowed ->
            if (isElectionFollowed == true) {
                binding.followUnfollowButton.text = getString(R.string.unfollow_button)
            } else {
                binding.followUnfollowButton.text = getString(R.string.follow_button)
            }
        })

        return binding.root

    }


//TODO: Create method to load URL intents
    private fun loadUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}