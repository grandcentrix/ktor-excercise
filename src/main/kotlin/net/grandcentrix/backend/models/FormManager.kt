package net.grandcentrix.backend.models

import io.ktor.http.*
import io.ktor.server.util.*
import net.grandcentrix.backend.models.StorageManagerTypesFile.Companion.StorageManagerTypesFileInstance

class FormManager() {

    companion object {
        val FormManagerInstance: FormManager = FormManager()
    }

    private var actionTitle = getFormTitle(FormActionType.ADD)
    private var formAction = getFormAction(FormActionType.ADD)
    private var formActionType = FormActionType.ADD.name
    var formAttributes = mutableMapOf("name" to actionTitle, "link" to formAction, "type" to formActionType)

    var video = Video("","","",VideoType.CUSTOM, "")
    val videoTypes = StorageManagerTypesFileInstance.getContent().toMutableList()
    var status = String()
    var updatedVideoValues = mutableMapOf<String,Any>()
    private var youtubeUrls = listOf(
        "https://www.youtube.com/watch?v=",
        "https://youtube.com/watch?v=",
        "youtube.com/watch?v=",
        "www.youtube.com/watch?v="
    )

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

    private fun getFormAction(actionName: FormActionType, id: String = String()): String {
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

    private fun videoParametersAreValid(id: String, title: String, link: String): Boolean {
        if (id.isBlank() || title.isBlank()) {
            status = "Video link and title cannot be blank!"
            return false
        } else if (!
            (link.startsWith(youtubeUrls[0]) ||
            link.startsWith(youtubeUrls[1]) ||
            link.startsWith(youtubeUrls[2]) ||
            link.startsWith(youtubeUrls[3]))
        ) {
            status = "Video link is not supported!"
            return false
        }
        return true
    }

    fun setVideoParameters(formParameters: Parameters) {
        val id = formParameters.getOrFail("link")
            .substringAfter("v=").substringBefore("&")
        val link = formParameters.getOrFail("link")
        val title = formParameters.getOrFail("title")
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val videoType = formParameters.getOrFail("videoTypes")
        var customTypeName = formParameters.getOrFail("customType").uppercase()
        val assignedType = assignType(videoType)

        if (!videoParametersAreValid(id, title, link))  {
            return
        } else if (customTypeName.isNotBlank() && assignedType == VideoType.CUSTOM){
            videoTypes.add(customTypeName)
            StorageManagerTypesFileInstance.setContent(videoTypes)
        } else if (assignedType == VideoType.CUSTOM) {
            customTypeName = videoType
        }

        video = Video(id, title, link, assignedType, customTypeName)
    }

    fun setUpdatedVideoParameters(id: String, formParameters: Parameters) {
        val newTitle = formParameters.getOrFail("title").replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
        val newType = formParameters.getOrFail("videoTypes")
        val customTypeName = formParameters.getOrFail("customType").uppercase()
        val assignedType = assignType(newType)

        updatedVideoValues.apply {
            put("id", id)
            put("newType", assignedType)
        }

        setUpdatedValues(newType, customTypeName, assignedType, newTitle)
        revertForm()
    }

    private fun setUpdatedValues(
        newType: String,
        customTypeName: String,
        assignedType: VideoType,
        newTitle: String
    ) {
        if (newType == VideoType.CUSTOM.name) {
            if (customTypeName.isBlank()) {
                status = "Custom type name cannot be blank!"
                return
            } else {
                updatedVideoValues.apply {
                    put("newCustomTypeName", customTypeName)
                }
                videoTypes.add(customTypeName)
                StorageManagerTypesFileInstance.setContent(videoTypes)
            }
        } // check if the user selected a custom type (not custom itself)
        else {
            if (assignedType == VideoType.CUSTOM) {
                updatedVideoValues.apply {
                    put("newCustomTypeName", newType)
                }
            }
        }

        if (newTitle.isNotBlank()) {
            updatedVideoValues.apply {
                put("newTitle", newTitle)
            }
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