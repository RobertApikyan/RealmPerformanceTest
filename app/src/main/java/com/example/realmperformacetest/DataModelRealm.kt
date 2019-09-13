package com.example.realmperformacetest

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class DataModelRealm : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var altId: String = ""
    var epcis: String = ""
    var dateTime: String = ""
    var vuid: String = ""
    var parentId: String = ""
    var topParentId:String = ""

    fun toDataModel() = DataModel(
        id, altId, epcis, dateTime, vuid, parentId,topParentId
    )

    fun toSequentString():String{
        return "$id,$altId,$epcis,$dateTime,$vuid,$parentId,$topParentId"
    }

    companion object {
        fun fromSequentString(string:String):DataModelRealm{
            val data = string.split(",")
            return DataModelRealm().apply {
                id = data[0]
                altId = data[1]
                epcis = data[2]
                dateTime = data[3]
                vuid = data[4]
                parentId = data[5]
                topParentId = data[6]
            }
        }
    }
}

class DataModel(
    val id: String,
    val altId: String,
    val epcis: String,
    val dateTime: String,
    val vuid: String,
    val parentId: String,
    val topParentId:String
) {
    fun toRealmObject() = DataModelRealm().apply {
        id = this@DataModel.id
        altId = this@DataModel.altId
        epcis = this@DataModel.epcis
        dateTime = this@DataModel.dateTime
        vuid = this@DataModel.vuid
        parentId = this@DataModel.parentId
        topParentId =this@DataModel.topParentId
    }
}