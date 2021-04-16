package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(application: Application) : AndroidViewModel(application) {

    //TODO: Create live data val for upcoming elections
    // Create the database singleton.
    // Define a database variable and assign it to getDatabase(), passing the application.
    private val database = ElectionDatabase.getInstance(application)

    // Create the repository.
    // Instantiate the variable by passing in the singleton AsteroidsDatabase object.
    private val electionRepository = ElectionRepository(database)

    // Create live data val for upcoming elections
    val upcomingElections: LiveData<List<Election>>
        get() = electionRepository.allElections


    //TODO: Create live data val for saved elections
    val followedElections: LiveData<List<Election>>
        get() = electionRepository.allFollowedElections


    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    // Refresh the elections using the repository.
    // Create an init block and launch a coroutine to call electionRepository.refreshElections().
    init {
        viewModelScope.launch {
            electionRepository.refreshElections()
        }
    }
    //TODO: Create functions to navigate to saved or upcoming election voter info
    private val _navigateToDetailElection = MutableLiveData<Election>()
    val navigateToDetailElection: LiveData<Election>
        get() = _navigateToDetailElection

    fun onElectionClicked(election: Election) {
        _navigateToDetailElection.value = election
    }

    fun onElectionNavigated() {
        _navigateToDetailElection.value = null
    }

}