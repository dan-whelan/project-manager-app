package com.learning.projectmanager.models

import android.os.Parcel
import android.os.Parcelable

/*
    Model for member that is assigned to card
 */
data class SelectedMemberModel(
    val id: String = "",
    val image: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SelectedMemberModel> {
        override fun createFromParcel(parcel: Parcel): SelectedMemberModel {
            return SelectedMemberModel(parcel)
        }

        override fun newArray(size: Int): Array<SelectedMemberModel?> {
            return arrayOfNulls(size)
        }
    }
}