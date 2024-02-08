package net.grandcentrix.backend.models

import io.ktor.http.*
import io.ktor.server.util.*

class FormManager() {

    companion object {
        val FormManagerInstance: FormManager = FormManager()
    }

    private var youtubeUrls = listOf("https://www.youtube.com/watch?v=", "https://youtube.com/watch?v=", "youtube.com/watch?v=", "www.youtube.com/watch?v=")
    private var actionTitle = FormActionType.getFormTitle(FormActionType.ADD)
    private var formAction = FormActionType.getFormAction(FormActionType.ADD)
    private var formActionType = FormActionType.ADD.name
    private var formAttributes = mutableMapOf("name" to actionTitle, "link" to formAction, "type" to formActionType)
    private var status = String()
    private lateinit var video: Video
    private var updatedVideoValues = mutableMapOf<String,String>()
    private var link = String()

    fun getFormActionType(): Map<String,Any> {
        return formAttributes
    }

    fun setActionTitle(actionTitle: String) {
        this.actionTitle = actionTitle
    }

    fun setFormAction(formAction: String) {
        this.formAction = formAction
    }

    fun getStatus(): String {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }

    fun setVideoParameters(formParameters: Parameters) {
        val id = formParameters.getOrFail("link").substringAfter("v=").substringBefore("&")
        val link = formParameters.getOrFail("link")
        val title = formParameters.getOrFail("title")
        val videoType = formParameters.getOrFail("videoTypes")
        val assignedType = VideoType.assignType(videoType)

        if (id.isBlank() || title.isBlank()) {
            status = "Video link and title cannot be blank!"
        } else if (!
            (link.startsWith(youtubeUrls[0]) || link.startsWith(youtubeUrls[1]) || link.startsWith(youtubeUrls[2]) || link.startsWith(youtubeUrls[3]))
            )
        {
            status = "Video link is not supported!"
        } else {
            video = Video(id, title, link, assignedType)
        }
    }

    fun getVideo(): Video {
        return video
    }

    fun setUpdatedVideoParameters(id: String, formParameters: Parameters) {
        val newTitle = formParameters.getOrFail("title")
        val newType = formParameters.getOrFail("videoTypes")

        if (newTitle.isBlank()) {
            status = "Video title cannot be blank!"
        } else {
            updatedVideoValues.apply {
                put("id", id)
                put("newTitle", newTitle)
                put("newType", newType)
            }

            actionTitle = FormActionType.getFormTitle(FormActionType.ADD)
            formAction = FormActionType.getFormAction(FormActionType.ADD)
            formActionType = FormActionType.ADD.name
            formAttributes.apply{
                put("name", actionTitle)
                put("link", formAction)
                put("type", formActionType)
            }
        }
    }

     fun updateFormAction(id: String, video: Video) {
         actionTitle = FormActionType.getFormTitle(FormActionType.UPDATE)
         formAction = FormActionType.getFormAction(FormActionType.UPDATE, id)
         formActionType = FormActionType.UPDATE.name
         formAttributes.apply{
             put("name", actionTitle)
             put("link", formAction)
             put("type", formActionType)
         }
         link = video.link
    }

    fun getLink(): String {
        return link
    }

    fun getUpdatedVideoValues(): Map<String,String> {
        return updatedVideoValues
    }

}