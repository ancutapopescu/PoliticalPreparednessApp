package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val dataSource: ElectionDao,
                         private val electionId: Int,
                         private val division: Division) : ViewModel() {

    //TODO: Add live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    var savedElection: Election? = null

    private val _isElectionSaved = MutableLiveData<Boolean>()
    val isElectionSaved: LiveData<Boolean>
        get() = _isElectionSaved

    //TODO: Add var and methods to populate voter info
    init {
        getVoterInfo()
        getSavedElection()
    }

    private fun getVoterInfo() {
        viewModelScope.launch {
            var address = "country:${division.country}"
            if(division.state.isNotEmpty()) {
                address += "/state:${division.state}"
            }
            _voterInfo.value = CivicsApi.retrofitService.getVoterinfo(address, electionId)
        }
    }

    fun getSavedElection() {
        viewModelScope.launch {
            savedElection = dataSource.getElectionById(electionId)
            _isElectionSaved.value = savedElection != null
        }
    }

    //TODO: Add var and methods to support loading URLs
    // Voting Locations
    private val _votingLocationsUrl = MutableLiveData<String>()
    val votingLocationUrl: LiveData<String>
        get() = _votingLocationsUrl

    fun votingLocationsClick() {
        _votingLocationsUrl.value = _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
    }

    fun votingLocationNavigated() {
        _votingLocationsUrl.value = null
    }

    // Ballot Information
    private val _ballotInformationUrl = MutableLiveData<String>()
    val ballotInformationUrl: LiveData<String>
        get() = _ballotInformationUrl

    fun ballotInformationClick() {
        _votingLocationsUrl.value = _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
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
                voterInfo.value?.let { dataSource.insert(it.election) }
                _isElectionSaved.value = true
            }
        }
    }

    private fun removeElectionFromDatabase() {
        viewModelScope.launch {
            voterInfo.value?.let { dataSource.deleteElection(it.election.id)}
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

    /*fun getElectionFromDb() {
        viewModelScope.launch {
            savedElection = dataSource.getElectionById(electionId)
            _isElectionSaved.value = savedElection != null
        }
    }*/
}