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
import com.example.android.politicalpreparedness.network.models.toFormattedString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VoterInfoViewModel(private val dataSource: ElectionDao,
                         private val electionId: Int,
                         private val division: Division) : ViewModel() {

    var electionFromDatabase: Election? = null


    //TODO: Add live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo


    //TODO: Add var and methods to populate voter info
    init {
        getVoterInfo()
    }

    // The state can be sometimes missing from the API call, but it has to add some state to the voterinfo API call or it will be rejected.
    private fun getVoterInfo() {
        viewModelScope.launch {
            var address = "country:${division.country}"
            if (!division.state.isBlank() && !division.state.isEmpty()) {
                address += "/state:${division.state}"
            } else {
                address += "/state:ca"
            }
            _voterInfo.value = CivicsApi.retrofitService.getVoterInfo(
                    address, electionId)
        }
    }


    //TODO: Add var and methods to support loading URLs
    // Voting Locations
    private val _votingLocationsUrl = MutableLiveData<String?>()
    val votingLocationUrl: LiveData<String?>
        get() = _votingLocationsUrl

    fun votingLocationsClick() {
        _votingLocationsUrl.value = _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
    }

    fun votingLocationsNavigated() {
        _votingLocationsUrl.value = null
    }

    // Ballot Information
    private val _ballotInformationUrl = MutableLiveData<String?>()
    val ballotInformationUrl: LiveData<String?>
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

    private val _isElectionFollowed: LiveData<Int>
        get() = dataSource.isElectionFollowed(electionId)

    val isElectionFollowed =
            Transformations.map(_isElectionFollowed) { followValue ->
                followValue?.let {
                    followValue == 1
                }
            }


    fun followUnfollowButton() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (isElectionFollowed.value == true) {
                    dataSource.unfollowElection(electionId)
                } else {
                    dataSource.followElection(electionId)
                }
            }
        }
    }
}