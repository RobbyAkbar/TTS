package edu.upi.ttsGisel.ui.leaderboard

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LeadModel(
        var name: String,
        var point: String,
        var photo: String
): Parcelable