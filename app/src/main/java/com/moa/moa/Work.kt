package com.moa.moa

data class Work(val id:String,
                val title:String,
                val description:String,
                val period:String,
                val time:String,
                val number:Int,
                val color:Int,
                val stars:Int,
                val people:List<People>?=null)
