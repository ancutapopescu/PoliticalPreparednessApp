package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private lateinit var _viewModel: VoterInfoViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        //TODO: Add binding values
        val binding = FragmentVoterInfoBinding.inflate(inflater)

        // This line gets the selected Election object from the Safe Args.
        val selectedElection = VoterInfoFragmentArgs.fromBundle(requireArguments()).selectedElection
        binding.lifecycleOwner = this
        binding.election = selectedElection


        //TODO: Add ViewModel values and create ViewModel
        val application = requireNotNull(activity).application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        val electionId = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
        val division = VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision
        val viewModelFactory = VoterInfoViewModelFactory(dataSource, electionId, division)
        _viewModel = ViewModelProvider(this, viewModelFactory).get(VoterInfoViewModel::class.java)



        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */


        //TODO: Handle loading of URLs
        // Voting Locations
        _viewModel.votingLocationUrl.observe(viewLifecycleOwner, Observer {
            it?.let {
            }
        })

        // Ballot Information
        _viewModel.ballotInformationUrl.observe(viewLifecycleOwner, Observer {

            it?.let {
                loadUrl(it)
                _viewModel.ballotInformationNavigated()
            }
        })

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks
        _viewModel.isElectionSaved.observe(viewLifecycleOwner, Observer {  isElectionSaved ->
            if (isElectionSaved) {
                binding.followUnfollowButton.text = getString(R.string.follow_button)
            } else {
                binding.followUnfollowButton.text = getString(R.string.unfollow_button)
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