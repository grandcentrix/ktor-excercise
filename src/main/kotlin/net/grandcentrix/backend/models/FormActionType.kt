package net.grandcentrix.backend.models

enum class FormActionType {
    ADD, UPDATE;

    companion object {
        fun getFormTitle(actionName: FormActionType): String {
            when (actionName) {
                ADD -> return "Add a new video:"
                UPDATE -> return "Update video:"
                else -> {
                    print("Error")
                    return "Wrong form action!"
                }
            }
        }

        fun getFormAction(actionName: FormActionType, id: String = ""): String {
            when (actionName) {
                ADD -> return "/add-video"
                UPDATE -> return "/${id}/update"
                else -> {
                    print("Error")
                    return "Wrong form action!"
                }
            }
        }
    }
}
