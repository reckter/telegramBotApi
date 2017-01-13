package me.reckter.telegram.model

/**
 * Created by Hannes on 13/01/2017.
 *
 */
class ChatMember {

    lateinit var user: User

    lateinit var status: ChatStatus
}

enum class ChatStatus {
    creator,
    administrator,
    member,
    left,
    kicked
}
