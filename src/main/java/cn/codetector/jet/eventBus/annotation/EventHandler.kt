package cn.codetector.jet.eventBus.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class EventHandler (val eventName: String)
