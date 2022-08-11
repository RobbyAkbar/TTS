package edu.upi.ttsGisel.ui.home

import edu.upi.ttsGisel.utils.Config
import org.json.JSONArray
import org.json.JSONException

object MateriJSONParser {

    private lateinit var materiList: MutableList<MateriModel>

    fun parseData(content: String, level: String): List<MateriModel> {

        lateinit var materiArray: JSONArray
        lateinit var materi: MateriModel

        try {
            materiArray = JSONArray(content)
            materiList = ArrayList()

            for (i in 0 until materiArray.length()) {
                val `object` = materiArray.getJSONObject(i)
                if (`object`.getString("level") == level){
                    materi = MateriModel(
                            Config.LINK_ICON + `object`.getString("icon"),
                            `object`.getString("lesson_name"),
                            `object`.getString("id_lesson"),
                            `object`.getString("dt_finish"),
                            Config.LINK_MATERI + `object`.getString("content"),
                            Config.LINK_VIDEO + `object`.getString("url_video"),
                            `object`.getString("score")
                    )
                    materiList.add(materi)
                }
            }
            return materiList
        } catch (e: JSONException) {
            e.printStackTrace()
            return emptyList()
        }

    }

}