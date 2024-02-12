package net.grandcentrix.backend.models

import io.ktor.http.*
import io.ktor.server.util.*

class FormManager() {

    companion object {
        val FormManagerInstance: FormManager = FormManager()
    }

    private var actionTitle = getFormTitle(FormActionType.ADD)
    private var formAction = getFormAction(FormActionType.ADD)
    private var formActionType = FormActionType.ADD.name
    var formAttributes = mutableMapOf("name" to actionTitle, "link" to formAction, "type" to formActionType)

    var video = Video("","","",VideoType.NONEXISTENT)
    var status = String()
    var updatedVideoValues = mutableMapOf<String,String>()
    private var youtubeUrls = listOf("https://www.youtube.com/watch?v=", "https://youtube.com/watch?v=", "youtube.com/watch?v=", "www.youtube.com/watch?v=")

    private fun getFormTitle(actionName: FormActionType): String {
        when (actionName) {
            FormActionType.ADD -> return "Add a new video:"
            FormActionType.UPDATE -> return "Update video:"
            else -> {
                print("Error")
                return "Wrong form action!"
            }
        }
    }

    private fun getFormAction(actionName: FormActionType, id: String = ""): String {
        when (actionName) {
            FormActionType.ADD -> return "/add-video"
            FormActionType.UPDATE -> return "/${id}/update"
            else -> {
                print("Error")
                return "Wrong form action!"
            }
        }
    }

    fun revertForm() {
        actionTitle = getFormTitle(FormActionType.ADD)
        formAction = getFormAction(FormActionType.ADD)
        formActionType = FormActionType.ADD.name
        formAttributes.apply{
            put("name", actionTitle)
            put("link", formAction)
            put("type", formActionType)
        }
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
            revertForm()
        }
    }

     fun updateFormAction(id: String, video: Video) {
         actionTitle = getFormTitle(FormActionType.UPDATE)
         formAction = getFormAction(FormActionType.UPDATE, id)
         formActionType = FormActionType.UPDATE.name
         formAttributes.apply{
             put("name", actionTitle)
             put("link", formAction)
             put("type", formActionType)
         }
         this.video = video
    }

}