/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package app.revanced.cli.utils.signer

import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.cert.jcajce.JcaCertStore
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cms.*
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.ContentSigner
import org.bouncycastle.operator.DigestCalculatorProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder
import org.bouncycastle.util.encoders.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigInteger
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.security.*
import java.security.cert.X509Certificate
import java.util.*
import java.util.jar.Attributes
import java.util.jar.JarFile
import java.util.jar.Manifest
import java.util.regex.Pattern


const val CN = "ReVanced"
val PASSWORD = "revanced".toCharArray() // TODO: make it secure; random password should be enough

/**
 * APK Signer.
 * @author Aliucord authors
 * @author ReVanced Team
 */
object Signer {
    private fun newKeystore(out: File) {
        val key = createKey()
        val privateKS = KeyStore.getInstance("BKS", "BC")
        privateKS.load(null, PASSWORD)
        privateKS.setKeyEntry("alias", key.privateKey, PASSWORD, arrayOf(key.publicKey))
        privateKS.store(FileOutputStream(out), PASSWORD)
    }

    private fun createKey(): KeySet {
        val gen = KeyPairGenerator.getInstance("RSA")
        gen.initialize(2048)
        val pair = gen.generateKeyPair()
        var serialNumber: BigInteger
        do serialNumber =
            BigInteger.valueOf(SecureRandom().nextLong()) while (serialNumber < BigInteger.ZERO)
        val x500Name = X500Name("CN=$CN")
        val builder = X509v3CertificateBuilder(
            x500Name,
            serialNumber,
            Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L * 30L),
            Date(System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 366L * 30L),
            Locale.ENGLISH,
            x500Name,
            SubjectPublicKeyInfo.getInstance(pair.public.encoded)
        )
        val signer: ContentSigner = JcaContentSignerBuilder("SHA1withRSA").build(pair.private)
        return KeySet(JcaX509CertificateConverter().getCertificate(builder.build(signer)), pair.private)
    }

    private val stripPattern: Pattern = Pattern.compile("^META-INF/(.*)[.](MF|SF|RSA|DSA)$")

    // based on https://gist.github.com/mmuszkow/10288441
    // and https://github.com/fornwall/apksigner/blob/master/src/main/java/net/fornwall/apksigner/ZipSigner.java
    fun signApk(apkFile: File) {
        Security.addProvider(BouncyCastleProvider())

        val ks = File(apkFile.parent, "revanced-cli.keystore")
        if (!ks.exists()) newKeystore(ks)

        val keyStore = KeyStore.getInstance("BKS", "BC")
        FileInputStream(ks).use { fis -> keyStore.load(fis, null) }
        val alias = keyStore.aliases().nextElement()
        val keySet = KeySet(
            (keyStore.getCertificate(alias) as X509Certificate),
            (keyStore.getKey(alias, PASSWORD) as PrivateKey)
        )

        val zip = FileSystems.newFileSystem(apkFile.toPath(), null)

        val dig = MessageDigest.getInstance("SHA1")
        val digests: MutableMap<String, String> = LinkedHashMap()

        for (entry in zip.allEntries) {
            val name = entry.toString()
            if (stripPattern.matcher(name).matches()) {
                Files.delete(entry)
            } else {
                digests[name] = toBase64(dig.digest(Files.readAllBytes(entry)))
            }
        }

        val sectionDigests: MutableMap<String, String> = LinkedHashMap()
        var manifest = Manifest()
        var attrs = manifest.mainAttributes
        attrs[Attributes.Name.MANIFEST_VERSION] = "1.0"
        attrs[Attributes.Name("Created-By")] = CN

        val digestAttr = Attributes.Name("SHA1-Digest")
        for ((name, value) in digests) {
            val attributes = Attributes()
            attributes[digestAttr] = value
            manifest.entries[name] = attributes
            sectionDigests[name] = hashEntrySection(name, attributes, dig)
        }
        ByteArrayOutputStream().use { baos ->
            manifest.write(baos)
            zip.writeFile(JarFile.MANIFEST_NAME, baos.toByteArray())
        }

        val manifestHash = getManifestHash(manifest, dig)
        val tmpManifest = Manifest()
        tmpManifest.mainAttributes.putAll(attrs)
        val manifestMainHash = getManifestHash(tmpManifest, dig)

        manifest = Manifest()
        attrs = manifest.mainAttributes
        attrs[Attributes.Name.SIGNATURE_VERSION] = "1.0"
        attrs[Attributes.Name("Created-By")] = CN
        attrs[Attributes.Name("SHA1-Digest-Manifest")] = manifestHash
        attrs[Attributes.Name("SHA1-Digest-Manifest-Main-Attributes")] = manifestMainHash

        for ((key, value) in sectionDigests) {
            val attributes = Attributes()
            attributes[digestAttr] = value
            manifest.entries[key] = attributes
        }
        var sigBytes: ByteArray
        ByteArrayOutputStream().use { sigStream ->
            manifest.write(sigStream)
            sigBytes = sigStream.toByteArray()
            zip.writeFile("META-INF/CERT.SF", sigBytes)
        }

        val signature = signSigFile(keySet, sigBytes)
        zip.writeFile("META-INF/CERT.RSA", signature)

        zip.close()
    }

    private fun hashEntrySection(name: String, attrs: Attributes, dig: MessageDigest): String {
        val manifest = Manifest()
        manifest.mainAttributes[Attributes.Name.MANIFEST_VERSION] = "1.0"
        ByteArrayOutputStream().use { baos ->
            manifest.write(baos)
            val emptyLen = baos.toByteArray().size
            manifest.entries[name] = attrs
            baos.reset()
            manifest.write(baos)
            var ob = baos.toByteArray()
            ob = Arrays.copyOfRange(ob, emptyLen, ob.size)
            return toBase64(dig.digest(ob))
        }
    }

    private fun getManifestHash(manifest: Manifest, dig: MessageDigest): String {
        ByteArrayOutputStream().use { baos ->
            manifest.write(baos)
            return toBase64(dig.digest(baos.toByteArray()))
        }
    }

    private fun signSigFile(keySet: KeySet, content: ByteArray): ByteArray {
        val msg: CMSTypedData = CMSProcessableByteArray(content)
        val certs = JcaCertStore(Collections.singletonList(keySet.publicKey))
        val gen = CMSSignedDataGenerator()
        val jcaContentSignerBuilder = JcaContentSignerBuilder("SHA1withRSA")
        val sha1Signer: ContentSigner = jcaContentSignerBuilder.build(keySet.privateKey)
        val jcaDigestCalculatorProviderBuilder = JcaDigestCalculatorProviderBuilder()
        val digestCalculatorProvider: DigestCalculatorProvider = jcaDigestCalculatorProviderBuilder.build()
        val jcaSignerInfoGeneratorBuilder = JcaSignerInfoGeneratorBuilder(digestCalculatorProvider)
        jcaSignerInfoGeneratorBuilder.setDirectSignature(true)
        val signerInfoGenerator: SignerInfoGenerator = jcaSignerInfoGeneratorBuilder.build(sha1Signer, keySet.publicKey)
        gen.addSignerInfoGenerator(signerInfoGenerator)
        gen.addCertificates(certs)
        val sigData: CMSSignedData = gen.generate(msg, false)
        return sigData.toASN1Structure().getEncoded("DER")
    }

    private fun toBase64(data: ByteArray): String {
        return String(Base64.encode(data))
    }
}

private val java.nio.file.FileSystem.allEntries: List<Path>
    get() = buildList {
        this@allEntries.rootDirectories.forEach { dir ->
            Files.walk(dir).filter(Files::isRegularFile).forEach { file ->
                this@buildList.add(file)
            }
        }
    }

private fun java.nio.file.FileSystem.writeFile(path: String, bytes: ByteArray) {
    Files.write(this.getPath("/$path"), bytes)
}