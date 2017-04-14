# Kotlin Telegram Bot API


This Bot API is build in Kotlin (with some parts still in Java) and builds on Annotations.

The Main Idea is, that you specify handler for commands and messages and the library calls them for you.

For Example:
``` Kotlin
class HelloWorldBot(
 val telegram: Telegram
){

    @OnCommand("hello")
    fun hello(message: Message, args: List<Strings) {
        var to = if(args.size > 2) args.sublist(1, args.length).joinToString(" ")
            else "World"
        telegram.sendMessage {
            chat(message.chat)
            text("Hello $to!")
        }
    }

}

```

This response to any message with `/hello` with 'Hello World!'

## Installation

TODO


## Usage

First you will need to create your own Telegram instance

The best way to do this is using the Telegram Builder:


```
val telegram = Telegram.Builder()
        .apiToken(<token here>)
        .adminChat(<admin chat here>)
        .build()
```

The admin chat is used, to send error messages and boot messages to.


To add a new handler to the library user

```
val helloWorldBot = HelloWorldBot(telegram)
telegram.addListener(helloWorldBot)
```

You can add as many handlers as you want, they all gonna be called.

### Handlers

There are two kind of handlers for regular messages:

#### `@OnCommand(<commands>)`
 This will get called if one of the commands is used.
All Methods need to have the message ad first argument and a list of strings as second:
`fun handler(message:Message, args: List<String>)`

The second Argument is a parsed list of arguments in the message, using unix style parsing.
so `/hello cat` will become: `listOf("hello", "cat")`  
`/hello "you two cats"`: `listOf("hello", "you two cats")`  
`/hello you two cats`: `listOf("hello", "you", "two", "cats")`  

you can escape " by using a single /.

#### `@OnMessage([optional: Message type])`

This will get called for every message that has the given type (defaults to just normal messages)

Note: Commands will not trigger this, because they are handled with the `OnCommand` annotation.

This expects a method that only takes in the message as an argument.
`fun handler(message:Message)`

There are some shorthands for the different types:  
- `@OnLocation` for locations  
- `@OnUserJoin` for user joining  
- `@OnUserLeave` for user leaving  


#### `@OnCallback`

This will get called for every inline button press.
The method will be called with the the first argument being a `CallbackQuery`.
Every `@OnCallback` handler will be called, for every button press, so for better seperation of concerns,
you should prefix your data on the buttons with a keyword, so the handlers can decide, what to do.

#### Inline Queries
Inline queries are handled differently, because there can only be one handler of inline queries in telegram.

To set up a Inline Query handler, you register one on the telegram object:
``` Kotlin
telegram.inlineQueryHandler { query ->
    val answer = InlineQueryAnswer(query.id, cacheTime = 0)
    (1..10).forEach {
        val article = InlineQueryResultArticle()
        article.title = "result $it"
        article.id = it.toString()
        answer.results.add(article)
    }
    answer
}
```

The handler returns an `InlineQueryAnswer`, which contains all the `InlineQueryResults`


If you want to get notified what the user has clicked on you need to call `onResult` on the `inlineQueryHandler`.
Like this:
```
telegram.inlineQueryHandler { query ->
    [...]
}.onResult { result ->
    print("result!")
}
```

Do not forget to configure your telegram bot with the @BotFather to send back results!


TODO other messages to send stuff.