package cloud.glitchdev.rfu.config.migration

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.slf4j.LoggerFactory
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

object ConfigMigration {
    const val CURRENT_VERSION = 1
    const val VERSION_KEY = "rfuConfigVersion"

    private val logger = LoggerFactory.getLogger(ConfigMigration::class.java)

    fun runMigrations(configFile: Path) {
        if (!configFile.exists()) return

        val root = try {
            readJsonc(configFile).asJsonObject
        } catch (e: Exception) {
            logger.warn("[RFU] Failed to read config for migration: ${e.message}")
            return
        }

        val currentVersion = root[VERSION_KEY]?.asInt ?: 0
        if (currentVersion >= CURRENT_VERSION) return

        processVersionChain(root, currentVersion)
        root.addProperty(VERSION_KEY, CURRENT_VERSION)

        try {
            writeJson(configFile, root)
        } catch (e: Exception) {
            logger.warn("[RFU] Failed to write migrated config: ${e.message}")
        }
    }

    private fun processVersionChain(json: JsonObject, from: Int) {
        for (version in from until CURRENT_VERSION) {
            when (version) {
                0 -> migrateV0toV1(json)
            }
        }
    }

    private fun migrateV0toV1(json: JsonObject) {
        val flareTimer = deleteKey(json, "General Fishing", "flareTimerDisplay")
        val umberellaTimer = deleteKey(json, "General Fishing", "umberellaTimerDisplay")
        val flareAlert = deleteKey(json, "General Fishing", "flareAlert")
        val umberellaAlert = deleteKey(json, "General Fishing", "umberellaAlert")

        val cat = getCategory(json, "General Fishing") ?: return

        if (!cat.has("deployableTimerDisplay")) {
            val arr = JsonArray()
            if (flareTimer?.asBoolean == true) arr.add("FLARE")
            if (umberellaTimer?.asBoolean == true) arr.add("UMBERELLA")
            cat.add("deployableTimerDisplay", arr)
        }

        if (!cat.has("deployableAlertTypes")) {
            val arr = JsonArray()
            if (flareAlert?.asBoolean == true) arr.add("FLARE")
            if (umberellaAlert?.asBoolean == true) arr.add("UMBERELLA")
            cat.add("deployableAlertTypes", arr)
        }
    }

    private fun deleteKey(json: JsonObject, category: String, key: String): JsonElement? {
        val cat = json[category]?.asJsonObject ?: return null
        val value = cat[key]
        cat.remove(key)
        return value
    }

    private fun getCategory(json: JsonObject, name: String): JsonObject? {
        return json[name]?.asJsonObject
    }

    private fun stripJsoncComments(text: String): String {
        val sb = StringBuilder(text.length)
        var i = 0
        var inString = false
        while (i < text.length) {
            val c = text[i]
            if (inString) {
                sb.append(c)
                if (c == '\\' && i + 1 < text.length) {
                    sb.append(text[++i])
                } else if (c == '"') {
                    inString = false
                }
            } else {
                when {
                    c == '"' -> {
                        inString = true
                        sb.append(c)
                    }
                    c == '/' && i + 1 < text.length && text[i + 1] == '/' -> {
                        while (i < text.length && text[i] != '\n') i++
                        continue
                    }
                    c == '/' && i + 1 < text.length && text[i + 1] == '*' -> {
                        i += 2
                        while (i + 1 < text.length && !(text[i] == '*' && text[i + 1] == '/')) i++
                        i += 2
                        continue
                    }
                    else -> sb.append(c)
                }
            }
            i++
        }
        return sb.toString()
    }

    private fun readJsonc(path: Path): JsonElement {
        val stripped = stripJsoncComments(path.readText())
        return JsonParser.parseString(stripped)
    }

    private fun writeJson(path: Path, json: JsonElement) {
        path.writeText(GsonBuilder().setPrettyPrinting().create().toJson(json))
    }
}
