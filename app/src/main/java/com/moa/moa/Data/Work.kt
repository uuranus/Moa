package com.moa.moa.Data

import java.io.Serializable

//집안일 데이터 클래스 time 은 alarm 시간
data class Work(
    var title:String ="", // 집안일 이름
    var description:String="", //집안일 세부내용
    var period:Int=0, //집안일 주기
    var time: String="", //집안일 알람 시간
    var number: Int=0, //참여 가능 인원
    var stars: Int=0, //별 갯수
    var workId:Int=0,
    var userList: List<User>?=null // 참여중인 인원
):Serializable
