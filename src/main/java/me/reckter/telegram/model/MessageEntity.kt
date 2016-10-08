package me.reckter.telegram.model

/**
 *  @author Hannes Güdelhöfer
 */
class MessageEntity{

    lateinit var type: String

    var offset: Int = 0
    var length: Int = 0

    var url: String? = null
    var user: User? = null
}