package cn.codetector.yiling.server.util

import io.vertx.core.MultiMap

/**
 * Created by codetector on 05/02/2017.
 */

fun verifyFormFields(map: MultiMap, fields: List<String>, isEmptyValid: Boolean = false): Boolean {
    for (fieldName in fields) {
        if (map.contains(fieldName) && map.get(fieldName) !== null) {
            if ((!isEmptyValid) && (!map.get(fieldName).isNotEmpty())) {
                return false
            }
        } else {
            return false
        }
    }
    return true
}