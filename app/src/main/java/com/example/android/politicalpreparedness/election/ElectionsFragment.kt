package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Election

class ElectionsFragment: Fragment() {

    //TODO: Declare ViewModel
    private lateinit var viewModel: ElectionsViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Add binding values
        //Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this

        //TODO: Add ViewModel values and create ViewModel
        val viewModelFactory = ElectionsViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)
        binding.viewModel = viewModel

        //TODO: Link elections to voter info
        //Observe the navigateToDetailElection LiveData and Navigate when it isn't null.
        //After navigating, call onElectionNavigated() so that the ViewModel is ready
        // for another navigation event.
        //Add an observer on navigateToDetailElection that calls navigate() to go
        // to the detail screen when the Election is not null.
        viewModel.navigateToDetailElection.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                this.findNavController()
                        .navigate(ElectionsFragmentDirections.actionShowDetail(it))
                viewModel.onElectionNavigated()
            }
        })

        //TODO: Initiate recycler adapters

        //TODO: Populate recycler adapters
        val adapterUpcomingElections = ElectionListAdapter(ElectionListener { election ->
            viewModel.navigateToDetailElection
            viewModel.onElectionClicked(election)
        })
        binding.rvUpcomingElections.adapter = adapterUpcomingElections

        // Setup Recycler View for saved elections
        val adapterSavedElections = ElectionListAdapter(ElectionListener { election ->
            viewModel.navigateToDetailElection
            viewModel.onElectionClicked(election)
        })
        binding.rvSavedElections.adapter = adapterSavedElections

        return binding.root

    }

    //TODO: Refresh adapters when fragment loads
    @BindingAdapter("listData")
    fun bindRecyclerView(recyclerView: RecyclerView, data: List<Election>?){
        val adapter = recyclerView.adapter as ElectionListAdapter
        adapter.submitList(data)
    }

}