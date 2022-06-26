package com.moa.moa.Data

//집안일 데이터 클래스 time 은 alarm 시간
data class Work(var title:String , var description:String, var period:Int, var time: String, var number: Int, var color: String, var star: Int,var userList: List<User>){

}
