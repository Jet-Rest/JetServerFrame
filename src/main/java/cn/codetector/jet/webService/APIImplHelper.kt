package cn.codetector.jet.webService

import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

fun ajaxXSiteHandler(router: Router) {
    router.route().handler { ctx ->
        ctx.response().putHeader("Access-Control-Allow-Origin", "*")
        ctx.next()
    }

    router.post().handler { ctx ->
        ctx.response().putHeader("Content-Type", "text/plain; charset=utf-8")
        ctx.next()
    }

    router.get().handler { ctx ->
        ctx.response().putHeader("Content-Type", "text/plain; charset=utf-8")
        ctx.next()
    }

    router.route().failureHandler { ctx ->
        ctx.response().setStatusCode(if (ctx.statusCode() > 0) {
            ctx.statusCode()
        } else {
            500
        }).putHeader("Access-Control-Allow-Origin", "*").end()
        if (ctx.failure() != null && ctx.failure() is Throwable) {
            ctx.failure().printStackTrace()
        }
    }

}

fun preflightRouteHandler(router: Router) {
    router.options().handler { ctx ->
        ctx.response().putHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE")
        ctx.response().putHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Cache-Control, X-Requested-With")
        ctx.response().end()
    }
}

fun multipartPostHandler(router: Router) {
    router.post().handler(BodyHandler.create().setMergeFormAttributes(true).setUploadsDirectory("store/tmp"))
}

fun failNoPermission(ctx: RoutingContext) {
    ctx.response().setStatusCode(401).end(JsonObject().put("error", "Not Authorized").toString())
}

fun failThrowable(ctx: RoutingContext, t: Throwable, code: Int = 500, jsonObject: JsonObject = JsonObject()) {
    ctx.response().setStatusCode(code).end(jsonObject.put("error", t.message).encode())
}