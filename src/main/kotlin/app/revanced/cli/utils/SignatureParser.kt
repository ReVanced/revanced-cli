package app.revanced.cli.utils

import app.revanced.patcher.signature.MethodSignature
import com.google.gson.JsonParser
import me.tongfei.progressbar.ProgressBar
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcodes

class SignatureParser {
    companion object {
        fun parse(json: String, bar: ProgressBar): List<MethodSignature> {
            val tmp = JsonParser.parseString(json).asJsonObject.get("signatures").asJsonArray
            bar.reset().maxHint(tmp.size().toLong())
                .extraMessage = "Parsing signatures"
            val signatures = tmp.map { sig ->
                val signature = sig.asJsonObject
                val returnType = signature.get("returns").asString

                var accessFlags = 0
                signature
                    .get("accessors").asJsonArray
                    .forEach { accessFlags = accessFlags or AccessFlags.getAccessFlag(it.asString).value }

                val parameters = signature.get("parameters").asJsonArray
                    .map { it.asString }

                val opcodes = signature.get("opcodes").asJsonArray
                    .map { Opcodes.getDefault().getOpcodeByName(it.asString)!! }

                val name = signature.get("name").asString
                bar.step()
                    .extraMessage = "Parsing $name"
                MethodSignature(
                    name,
                    returnType,
                    accessFlags,
                    parameters,
                    opcodes
                )
            }
            return signatures
        }
    }
}