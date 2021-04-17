package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.database.ElectionDatabase.Companion.getInstance
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VoterInfoViewModel(val election: Election, application: Application) : ViewModel() {

    // Create the database singleton.
    // Define a database variable and assign it to getDatabase(), passing the application.
    private val database = getInstance(application)

    // Create the repository.
    // Instantiate the variable by passing in the singleton ElectionDatabase object.
    private val electionRepository = ElectionRepository(database)

    //TODO: Add live data to hold voter info
    val voterInfo = electionRepository.voterInfo

    private val _isElectionSaved = MutableLiveData<Boolean>()
    val isElectionSaved: LiveData<Boolean>
        get() = _isElectionSaved

    //TODO: Add var and methods to populate voter info
    fun getVoterInfo(electionId: Int, address: String) =
            viewModelScope.launch {
                electionRepository.getVoterInfo(electionId, address)
            }



    //TODO: Add var and methods to support loading URLs
    // Voting Locations
    private val _votingLocationsUrl = MutableLiveData<String>()
    val votingLocationUrl: LiveData<String>
        get() = _votingLocationsUrl

    fun votingLocationsClick() {
        _votingLocationsUrl.value = voterInfo.value?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
    }

    // Ballot Information
    private val _ballotInformationUrl = MutableLiveData<String>()
    val ballotInformationUrl: LiveData<String>
        get() = _ballotInformationUrl

    fun ballotInformationClick() {
        _votingLocationsUrl.value = voterInfo.value?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
    }

    fun ballotInformationNavigated() {
        _ballotInformationUrl.value = null
    }


    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

    private fun saveElectionToDatabase() {
        viewModelScope.launch {
            voterInfo.value?.let {
                voterInfo.value?.let { database.electionDao.insert(it.election) }
                _isElectionSaved.value = true
            }
        }
    }

    private fun removeElectionFromDatabase() {
        viewModelScope.launch {
            voterInfo.value?.let { database.electionDao.deleteElection(it.election.id)}
            _isElectionSaved.value = false
        }
    }

    fun followUnfollowButton() {
        if (_isElectionSaved.value == true) {
            removeElectionFromDatabase()
        } else {
            saveElectionToDatabase()
        }
    }
}