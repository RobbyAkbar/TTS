package edu.upi.ttsGisel.ui.leaderboard

import org.json.JSONArray
import org.json.JSONException

object LeadJSONParser {

    private lateinit var leadList: MutableList<LeadModel>

    fun parseData(content: String): List<LeadModel> {

        lateinit var leadArray: JSONArray
        lateinit var lead: LeadModel

        try {
            leadArray = JSONArray(content)
            leadList = ArrayList()

            for (i in 0 until leadArray.length()) {
                val `object` = leadArray.getJSONObject(i)
                lead = LeadModel(
                        `object`.getString("full_name"),
                        `object`.getString("score")+" xp",
                        `object`.getString("img_photo")
                )
                leadList.add(lead)
            }
            return leadList
        } catch (e: JSONException) {
            e.printStackTrace()
            return emptyList()
        }

    }

}