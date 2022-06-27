package com.moa.moa.Data

//집안일 데이터 클래스 time 은 alarm 시간
data class Work(
    var title:String , // 집안일 이름
    var description:String, //집안일 세부내용
    var period:Int, //집안일 주기
    var time: String, //집안일 알람 시간
    var number: Int, //참여 가능 인원
    var color: String, //집안일 색깔
    var star: Int, //별 갯수
    var userList: List<User> // 참여중인 인원
){


}
