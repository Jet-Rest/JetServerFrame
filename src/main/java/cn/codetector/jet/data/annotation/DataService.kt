package cn.codetector.jet.data.annotation

/**
 * Annotate this on to a field to inject the target service
 * @param name Name of the target service you want to inject. Case sensitive
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class DataService(val name: String)