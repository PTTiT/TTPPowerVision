package com.ttp.powervision.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Franz on 11/8/2017.
 */
data class VisionResponse(
        @SerializedName("web")
        @Expose
        var web: Web)

data class Web(
        @SerializedName("webEntities")
        @Expose
        var webEntities: List<WebEntity>)

data class WebEntity(
        @SerializedName("score")
        @Expose
        var score: Double,
        @SerializedName("entityId")
        @Expose
        var entityId: String,
        @SerializedName("description")
        @Expose
        var description: String)