package edu.upi.ttsGisel.ui.home

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MateriModel(
        var icon: String,
        var judul: String,
        var idMateri: String,
        var dtFinish: String,
        var content: String,
        var urlVideo: String,
        var score: String?
): Parcelable
