package edu.upi.ttsGisel.lesson

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class LessonTimeLineModel(
        var message: String,
        var type: String,
        var status: String?
) : Parcelable