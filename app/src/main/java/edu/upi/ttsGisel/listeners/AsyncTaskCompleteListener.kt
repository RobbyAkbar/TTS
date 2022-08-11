package edu.upi.ttsGisel.listeners

interface AsyncTaskCompleteListener {
    fun onTaskCompleted(response: String, serviceCode: Int)
}