package cn.codetector.jet.webService

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class WebAPIImpl(val prefix: String)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Route(val prefix: String, val url: String = "")