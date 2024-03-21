package net.grandcentrix.backend.models

import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.util.*
import net.grandcentrix.backend.models.StorageManagerTypesFile.Companion.StorageManagerTypesFileInstance

class FormManager() {

    companion object {
        val FormManagerInstance: FormManager = FormManager()
    }

    private var actionTitle = getFormTitle(FormActionType.ADD)
    private var formAction = getFormAction(FormActionType.ADD)
    private var formActionType = FormActionType.ADD.name
    val formAttributes = mutableMapOf("name" to actionTitle, "link" to formAction, "type" to formActionType)

    var video = Video("","","",VideoType.CUSTOM, "")
    val videoTypes = StorageManagerTypesFileInstance.getContent().toMutableList()
    var status = String()
    val updatedVideoValues = mutableMapOf<String,Any>()
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

     private fun videoParametersAreValid(
         id: String,
         title: String,
         link: String,
         videoType: String,
         customTypeName: String
     ) {
        if (id.isBlank() || title.isBlank()) {
            status = "Video link and title cannot be blank!"
            throw MissingRequestParameterException("Blank video link and title")
        } else if (!
            (link.startsWith(youtubeUrls[0]) ||
            link.startsWith(youtubeUrls[1]) ||
            link.startsWith(youtubeUrls[2]) ||
            link.startsWith(youtubeUrls[3]))
        ) {
            status = "Video link is not supported!"
            throw MissingRequestParameterException("Video link is not supported!")
        } else if (videoType == VideoType.CUSTOM.name && customTypeName.isBlank()) {
            status = "Custom type name cannot be blank!"
            throw MissingRequestParameterException("Blank custom type")
        }
    }

    fun setVideoParameters(formParameters: Parameters) {
        val id = formParameters
            .getOrFail("link")
            .substringAfter("v=")
            .substringBefore("&")

        val link = formParameters
            .getOrFail("link")
            .substringBefore("&")

        val title = formParameters
            .getOrFail("title")
            .replaceFirstChar {
                if (it.isLowerCase()) it.titlecase()
                else it.toString()
            }

        val videoType = formParameters
            .getOrFail("videoTypes")

        var customTypeName = formParameters
            .getOrFail("customType")
            .uppercase()

        var assignedType = assignType(videoType)
        val typeNames = videoTypes.find { it == customTypeName }
        val isDuplicated = VideoType.entries.find { it.name == customTypeName }

        videoParametersAreValid(id, title, link, videoType, customTypeName)

        if (videoType == VideoType.CUSTOM.name) {
            if (typeNames == null) {
                videoTypes.add(customTypeName)
                StorageManagerTypesFileInstance.setContent(videoTypes)

            } else if (isDuplicated != null) {
                    assignedType = assignType(customTypeName)
                    customTypeName = ""
            }
        } else {
            if (assignedType == VideoType.CUSTOM) {
                customTypeName = videoType
            }
        }

        video = Video(id, title, link, assignedType, customTypeName)
    }

    fun setUpdatedVideoParameters(id: String, formParameters: Parameters) {
        val newTitle = formParameters
            .getOrFail("title")
            .replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }

        val newType = formParameters
            .getOrFail("videoTypes")

        val customTypeName = formParameters
            .getOrFail("customType")
            .uppercase()

        val assignedType = assignType(newType)

        setCustomTypeName(newType, customTypeName, assignedType)

        updatedVideoValues.apply {
            put("id", id)
            put("newTitle", newTitle)
        }

        revertForm()
    }

    private fun setCustomTypeName (
        newType: String,
        customTypeName: String,
        assignedType: VideoType,
    ) {
        val typeNames = videoTypes.find { it == customTypeName }
        val isDuplicated = VideoType.entries.find { it.name == customTypeName }

        if (newType == VideoType.CUSTOM.name) {
            if (customTypeName.isBlank()) {
                status = "Custom type name cannot be blank!"
                throw MissingRequestParameterException("Blank custom type")
            } else {
                if (isDuplicated != null) {
                    updatedVideoValues.apply {
                        put("newType", assignType(customTypeName))
                        put("newCustomTypeName", "")
                    }
                } else {
                    updatedVideoValues.apply {
                        put("newType", assignedType)
                        put("newCustomTypeName", customTypeName)
                    }
                    if (typeNames == null) {
                        videoTypes.add(customTypeName)
                        StorageManagerTypesFileInstance.setContent(videoTypes)
                    }
                }
            }

        } // check if the user selected a custom type (not custom itself)
        else {
            updatedVideoValues.apply {
                put("newType", assignedType)

                if (assignedType == VideoType.CUSTOM) {
                    put("newCustomTypeName", newType)
                } else {
                    put("newCustomTypeName", "")
                }
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