package app.revanced.cli.utils
import com.google.gson.JsonParser
import java.io.File
import app.revanced.patcher.signature.MethodSignature
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcodes

class SignatureParser {
    companion object {
        fun parse(signatureJsonPath: String): List<MethodSignature> {
            val json = File(signatureJsonPath).readText()
            val signatures = JsonParser.parseString(json).asJsonObject.get("signatures").asJsonArray.map { sig ->
                val signature = sig.asJsonObject

                val returnType = signature.get("returns").asString

                var accessFlags = 0

                signature
                    .get("accessors").asJsonArray
                    .forEach { accessFlags = accessFlags or AccessFlags.getAccessFlag(it.asString).value }

                val parameters = signature.get("parameters").asJsonArray
                    .map { it.asString }
                    .toTypedArray()

                val opcodes = signature.get("opcodes").asJsonArray
                    .map { Opcodes.getDefault().getOpcodeByName(it.asString)!! }
                    .toTypedArray()

                MethodSignature(
                    signature.get("name").asString,
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