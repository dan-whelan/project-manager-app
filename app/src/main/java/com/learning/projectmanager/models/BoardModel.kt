package com.learning.projectmanager.models

import android.os.Parcel
import android.os.Parcelable

/*
    Model for board stored in DB
 */
data class BoardModel(
    val name: String = "",
    val image: String = "",
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    var documentId: String = "",
    var taskList: ArrayList<TaskModel> = ArrayList()
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(TaskModel.CREATOR)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(createdBy)
        parcel.writeStringList(assignedTo)
        parcel.writeString(documentId)
        parcel.writeTypedList(taskList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BoardModel> {
        override fun createFromParcel(parcel: Parcel): BoardModel {
            return BoardModel(parcel)
        }

        override fun newArray(size: Int): Array<BoardModel?> {
            return arrayOfNulls(size)
        }
    }
}
