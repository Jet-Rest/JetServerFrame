///*
// * Copyright (c) 2016. Codetector (Yaotian Feng)
// */
//
//package cn.codetector.jet.console
//
//import cn.codetector.util.StringUtil.StringFormatUtil
//import cn.codetector.yiling.server.Main
//import cn.codetector.jet.console.consoleManager.Command
//import cn.codetector.yiling.server.data.DataService
//import cn.codetector.yiling.server.data.services.multifactorauthentication.MFAUtil
//import cn.codetector.yiling.server.data.services.permission.PermissionManager
//import cn.codetector.yiling.server.data.services.user.UserHash
//import cn.codetector.jet.webService.WebService
//
///**
// * Created by codetector on 20/11/2016.
// */
//object CommandHandlers {
//    @Command(command = "web")
//    fun webServiceCommandHandler(args: Array<String>): Boolean {
//        if (args.size == 2) {
//            when (args[1]) {
//                "status" -> {
//                    println("Web Service is currently " + if (WebService.isServiceRunning) "Running" else "Stopped")
//                    return true
//                }
//                "stop" -> {
//                    WebService.shutdown()
//                    return true
//                }
//                "start" -> {
//                    WebService.initService(Main.sharedVertx, Main.sharedJDBCClient)
//                    return true
//                }
//            }
//        }
//        println("Available actions: (status)")
//        return false
//    }
//
//    @Command(command = "server")
//    fun serverCommandHandler(args: Array<String>): Boolean {
//        if (args.size == 2) {
//            when (args[1]) {
//                "stop" -> {
//                    Main.stopService()
//                    return true
//                }
//                "save" -> {
//                    Main.save()
//                    return true
//                }
//            }
//        }
//        println("Available actions: (status)")
//        return false
//    }
//
//    @Command(command = "db")
//    fun dbCommandHandler(args: Array<String>): Boolean {
//        if (args.size > 1) {
//            when (args[1]) {
//                "save" -> {
//                    DataService.save()
//                    return true
//                }
//                "reload" -> {
//                    DataService.reload()
//                    return true
//                }
//            }
//        }
//        return false
//    }
//
//    @Command(command = "user")
//    fun userCommandHandler(args: Array<String>): Boolean {
//        if (args.size > 1) {
//            val userHash = DataService.getService("UserHash") as UserHash
//            when (args[1]) {
//                "count" -> {
//                    val cnt = userHash.totalUserCache()
//                    println("Current Logged in: $cnt")
//                    return true
//                }
//                "save" -> {
//                    userHash.save()
//                    return true
//                }
//                "reload" -> {
//                    userHash.removeTimedOutUsers(userHash.DEFAULT_TIMEOUT)
//                    println("Finished removing timed out users")
//                    return true
//                }
//                "clear" -> {
//                    userHash.clearCache()
//                    return true
//                }
//            }
//        }
//        return false
//    }
//
//    @Command(command = "mfa")
//    fun mfaCommandHandler(args: Array<String>): Boolean {
//        if (args.size > 1) {
//            when (args[1]){
//                "gen" -> {
//                    println(MFAUtil.generateBase32Secret())
//                    return true;
//                }
//            }
//        }
//        return false
//    }
//
//    @Command(command = "mem")
//    fun memoryCommandHandler(args: Array<String>): Boolean {
//        if (args.size <= 1) {
//            val runtime = Runtime.getRuntime()
//            println("Free Memory: ${StringFormatUtil.byteSizeToString(runtime.freeMemory(), false)}, Total: ${StringFormatUtil.byteSizeToString(runtime.totalMemory(), false)}, (${StringFormatUtil.byteSizeToString(runtime.totalMemory() - runtime.freeMemory(), false)} used)")
//            return true
//        } else {
//            when (args[1]){
//                "gc" -> {
//                    Runtime.getRuntime().gc()
//                    return true;
//                }
//            }
//        }
//        return false
//    }
//
//    @Command(command = "permission")
//    fun permissionCommandHandler(args: Array<String>): Boolean {
//        if (args.size > 1) {
//            when (args[1]) {
//                "list" -> {
//                    (DataService.getService("PermissionManager") as PermissionManager).allPermissions().forEach(::println)
//                    return true
//                }
//                "add" -> {
//                    if (args.size > 2) {
//                        (DataService.getService("PermissionManager") as PermissionManager).registerPermission(args[2])
//                        println("Permission ${args[2]} added!")
//                        return true
//                    }
//                }
//            }
//        }
//        return false
//    }
//}