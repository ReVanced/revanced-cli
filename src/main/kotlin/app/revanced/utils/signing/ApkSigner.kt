package app.revanced.utils.signing

import com.android.apksig.ApkSigner
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.ContentSigner
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.*
import java.security.cert.X509Certificate
import java.util.*
import java.util.logging.Logger

internal class ApkSigner(
    private val signingOptions: SigningOptions
) {
    private val logger = Logger.getLogger(ApkSigner::class.java.name)

    private val signer: ApkSigner.Builder
    private val passwordCharArray = signingOptions.password.toCharArray()

    init {
        Security.addProvider(BouncyCastleProvider())

        val keyStore = KeyStore.getInstance("BKS", "BC")
        val alias = keyStore.let { store ->
            FileInputStream(File(signingOptions.keyStoreFilePath).also {
                if (!it.exists()) {
                    logger.info("Creating keystore at ${it.absolutePath}")
                    newKeystore(it)
                } else {
                    logger.info("Using keystore at ${it.absolutePath}")
                }
            }).use { fis -> store.load(fis, null) }
            store.aliases().nextElement()
        }

        with(
            ApkSigner.SignerConfig.Builder(
                signingOptions.cn,
                keyStore.getKey(alias, passwordCharArray) as PrivateKey,
                listOf(keyStore.getCertificate(alias) as X509Certificate)
            ).build()
        ) {
            this@ApkSigner.signer = ApkSigner.Builder(listOf(this))
            signer.setCreatedBy(signingOptions.cn)
        }
    }

    private fun newKeystore(out: File) {
        val (publicKey, privateKey) = createKey()
        val privateKS = KeyStore.getInstance("BKS", "BC")
        privateKS.load(null, passwordCharArray)
        privateKS.setKeyEntry("alias", privateKey, passwordCharArray, arrayOf(publicKey))
        privateKS.store(FileOutputStream(out), passwordCharArray)
    }

    private fun createKey(): Pair<X509Certificate, PrivateKey> {
        val gen = KeyPairGenerator.getInstance("RSA")
        gen.initialize(2048)
        val pair = gen.generateKeyPair()
        var serialNumber: BigInteger
        do serialNumber = BigInteger.valueOf(SecureRandom().nextLong()) while (serialNumber < BigInteger.ZERO)
        val x500Name = X500Name("CN=${signingOptions.cn}")
        val builder = X509v3CertificateBuilder(
            x500Name,
            serialNumber,
            Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L * 30L),
            Date(System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 366L * 30L),
            Locale.ENGLISH,
            x500Name,
            SubjectPublicKeyInfo.getInstance(pair.public.encoded)
        )
        val signer: ContentSigner = JcaContentSignerBuilder("SHA256withRSA").build(pair.private)
        return JcaX509CertificateConverter().getCertificate(builder.build(signer)) to pair.private
    }

    fun signApk(input: File, output: File): File {
        signer.setInputApk(input)
        signer.setOutputApk(output)

        signer.build().sign()

        return output
    }
}