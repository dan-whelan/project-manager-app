package com.learning.projectmanager.models

import android.os.Parcel
import android.os.Parcelable

data class CardModel(
    val title: String = "",
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    val cardColour: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(createdBy)
        parcel.writeStringList(assignedTo)
        parcel.writeString(cardColour)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CardModel> {
        override fun createFromParcel(parcel: Parcel): CardModel {
            return CardModel(parcel)
        }

        override fun newArray(size: Int): Array<CardModel?> {
            return arrayOfNulls(size)
        }
    }
}
