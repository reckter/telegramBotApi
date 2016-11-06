package me.reckter.telegram.model

/**
 *  @author Hannes Güdelhöfer
 */
class InlineQuery {

    lateinit var id: String

    lateinit var from: User

    var location: Location? = null

    lateinit var query: String

    var offset: String? = null
}