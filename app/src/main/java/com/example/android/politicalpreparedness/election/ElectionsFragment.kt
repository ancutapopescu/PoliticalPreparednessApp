package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Election

class ElectionsFragment: Fragment() {

    //TODO: Declare ViewModel
    private lateinit var viewModel: ElectionsViewModel

    //TODO: Initiate recycler adapters
    private lateinit var adapterUpcomingElections: ElectionListAdapter
    private lateinit var adapterSavedElections: ElectionListAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        //TODO: Add binding values
        //Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        val binding: FragmentElectionBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_election,
                container,
                false)
        binding.lifecycleOwner = this

        //TODO: Add ViewModel values and create ViewModel
        val viewModelFactory = ElectionsViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)
        binding.viewModel = viewModel

        //TODO: Populate recycler adapters
        adapterUpcomingElections = ElectionListAdapter(ElectionListener {
            findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it.id, it.division))
        })
        binding.rvUpcomingElections.adapter = adapterUpcomingElections

        // Setup Recycler View for saved elections
        adapterSavedElections = ElectionListAdapter(ElectionListener {
            findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it.id, it.division))
        })
        binding.rvSavedElections.adapter = adapterSavedElections

        //TODO: Link elections to voter info
        //Observe the navigateToDetailElection LiveData and Navigate when it isn't null.
        //After navigating, call onElectionNavigated() so that the ViewModel is ready
        // for another navigation event.
        //Add an observer on navigateToDetailElection that calls navigate() to go
        // to the detail screen when the Election is not null.
        viewModel.upcomingElections.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapterUpcomingElections.submitList(it)
            }
        })

        viewModel.followedElections.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapterSavedElections.submitList(it)
            }
        })

        return binding.root

    }

    //TODO: Refresh adapters when fragment loads

    /*@BindingAdapter("listData")
    fun bindRecyclerView(recyclerView: RecyclerView, data: List<Election>?){
        val adapter = recyclerView.adapter as ElectionListAdapter
        adapter.submitList(data)
    }*/

}