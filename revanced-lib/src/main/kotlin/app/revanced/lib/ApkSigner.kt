package app.revanced.lib

import com.android.apksig.ApkSigner
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.math.BigInteger
import java.security.*
import java.security.cert.X509Certificate
import java.util.*
import java.util.logging.Logger
import kotlin.time.Duration.Companion.days

/**
 * Utility class for writing or reading keystore files and entries as well as signing APK files.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
object ApkSigner {
    private val logger = Logger.getLogger(app.revanced.lib.ApkSigner::class.java.name)

    init {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
            Security.addProvider(BouncyCastleProvider())
    }

    /**
     * Create a new [PrivateKeyCertificatePair].
     *
     * @param commonName The common name of the certificate.
     * @param validUntil The date until the certificate is valid.
     * @return The created [PrivateKeyCertificatePair].
     */
    fun newPrivateKeyCertificatePair(
        commonName: String = "ReVanced",
        validUntil: Date = Date(System.currentTimeMillis() + 356.days.inWholeMilliseconds * 24)
    ): PrivateKeyCertificatePair {
        logger.fine("Creating certificate for $commonName")

        // Generate a new key pair.
        val keyPair = KeyPairGenerator.getInstance("RSA").apply {
            initialize(2048)
        }.generateKeyPair()

        var serialNumber: BigInteger
        do serialNumber = BigInteger.valueOf(SecureRandom().nextLong())
        while (serialNumber < BigInteger.ZERO)

        val name = X500Name("CN=$commonName")

        // Create a new certificate.
        val certificate = JcaX509CertificateConverter().getCertificate(
            X509v3CertificateBuilder(
                name,
                serialNumber,
                Date(System.currentTimeMillis()),
                validUntil,
                Locale.ENGLISH,
                name,
                SubjectPublicKeyInfo.getInstance(keyPair.public.encoded)
            ).build(JcaContentSignerBuilder("SHA256withRSA").build(keyPair.private))
        )

        return PrivateKeyCertificatePair(keyPair.private, certificate)
    }


    /**
     * Read a [PrivateKeyCertificatePair] from a keystore entry.
     *
     * @param keyStore The keystore to read the entry from.
     * @param keyStoreEntryAlias The alias of the key store entry to read.
     * @param keyStoreEntryPassword The password for recovering the signing key.
     * @return The read [PrivateKeyCertificatePair].
     * @throws IllegalArgumentException If the keystore does not contain the given alias or the password is invalid.
     */
    fun readKeyCertificatePair(
        keyStore: KeyStore,
        keyStoreEntryAlias: String,
        keyStoreEntryPassword: String,
    ): PrivateKeyCertificatePair {
        logger.fine("Reading key and certificate pair from keystore entry $keyStoreEntryAlias")

        if (!keyStore.containsAlias(keyStoreEntryAlias))
            throw IllegalArgumentException("Keystore does not contain alias $keyStoreEntryAlias")

        // Read the private key and certificate from the keystore.

        val privateKey = try {
            keyStore.getKey(keyStoreEntryAlias, keyStoreEntryPassword.toCharArray()) as PrivateKey
        } catch (exception: UnrecoverableKeyException) {
            throw IllegalArgumentException("Invalid password for keystore entry $keyStoreEntryAlias")
        }

        val certificate = keyStore.getCertificate(keyStoreEntryAlias) as X509Certificate

        return PrivateKeyCertificatePair(privateKey, certificate)
    }

    /**
     * Create a new keystore with a new keypair.
     *
     * @param entries The entries to add to the keystore.
     * @return The created keystore.
     * @see KeyStoreEntry
     */
    fun newKeyStore(
        entries: List<KeyStoreEntry>
    ): KeyStore {
        logger.fine("Creating keystore")

        return KeyStore.getInstance("BKS", BouncyCastleProvider.PROVIDER_NAME).apply {
            entries.forEach { entry ->
                load(null)
                // Add all entries to the keystore.
                setKeyEntry(
                    entry.alias,
                    entry.privateKeyCertificatePair.privateKey,
                    entry.password.toCharArray(),
                    arrayOf(entry.privateKeyCertificatePair.certificate)
                )
            }
        }
    }

    /**
     * Create a new keystore with a new keypair and saves it to the given [keyStoreOutputStream].
     *
     * @param keyStoreOutputStream The stream to write the keystore to.
     * @param keyStorePassword The password for the keystore.
     * @param entries The entries to add to the keystore.
     */
    fun newKeystore(
        keyStoreOutputStream: OutputStream,
        keyStorePassword: String,
        entries: List<KeyStoreEntry>
    ) = newKeyStore(entries).store(
        keyStoreOutputStream,
        keyStorePassword.toCharArray()
    ) // Save the keystore.

    /**
     * Read a keystore from the given [keyStoreInputStream].
     *
     * @param keyStoreInputStream The stream to read the keystore from.
     * @param keyStorePassword The password for the keystore.
     * @return The keystore.
     * @throws IllegalArgumentException If the keystore password is invalid.
     */
    fun readKeyStore(
        keyStoreInputStream: InputStream,
        keyStorePassword: String?
    ): KeyStore {
        logger.fine("Reading keystore")

        return KeyStore.getInstance("BKS", BouncyCastleProvider.PROVIDER_NAME).apply {
            try {
                load(keyStoreInputStream, keyStorePassword?.toCharArray())
            } catch (exception: IOException) {
                if (exception.cause is UnrecoverableKeyException)
                    throw IllegalArgumentException("Invalid keystore password")
                else
                    throw exception
            }
        }
    }

    /**
     * Create a new [ApkSigner.Builder].
     *
     * @param privateKeyCertificatePair The private key and certificate pair to use for signing.
     * @param signer The name of the signer.
     * @param createdBy The value for the `Created-By` attribute in the APK's manifest.
     * @return The created [ApkSigner.Builder] instance.
     */
    fun newApkSignerBuilder(
        privateKeyCertificatePair: PrivateKeyCertificatePair,
        signer: String,
        createdBy: String
    ): ApkSigner.Builder {
        logger.fine(
            "Creating new ApkSigner " +
                    "with $signer as signer and " +
                    "$createdBy as Created-By attribute in the APK's manifest"
        )

        // Create the signer config.
        val signerConfig = ApkSigner.SignerConfig.Builder(
            signer,
            privateKeyCertificatePair.privateKey,
            listOf(privateKeyCertificatePair.certificate)
        ).build()

        // Create the signer.
        return ApkSigner.Builder(listOf(signerConfig)).apply {
            setCreatedBy(createdBy)
        }
    }

    /**
     * Create a new [ApkSigner.Builder].
     *
     * @param keyStore The keystore to use for signing.
     * @param keyStoreEntryAlias The alias of the key store entry to use for signing.
     * @param keyStoreEntryPassword The password for recovering the signing key.
     * @param signer The name of the signer.
     * @param createdBy The value for the `Created-By` attribute in the APK's manifest.
     * @return The created [ApkSigner.Builder] instance.
     * @see KeyStoreEntry
     * @see PrivateKeyCertificatePair
     * @see ApkSigner.Builder.setCreatedBy
     * @see ApkSigner.Builder.signApk
     */
    fun newApkSignerBuilder(
        keyStore: KeyStore,
        keyStoreEntryAlias: String,
        keyStoreEntryPassword: String,
        signer: String,
        createdBy: String,
    ) = newApkSignerBuilder(
        readKeyCertificatePair(keyStore, keyStoreEntryAlias, keyStoreEntryPassword),
        signer,
        createdBy
    )

    fun ApkSigner.Builder.signApk(input: File, output: File) {
        logger.info("Signing ${input.name}")

        setInputApk(input)
        setOutputApk(output)

        build().sign()
    }

    /**
     * An entry in a keystore.
     *
     * @param alias The alias of the entry.
     * @param password The password for recovering the signing key.
     * @param privateKeyCertificatePair The private key and certificate pair.
     * @see PrivateKeyCertificatePair
     */
    class KeyStoreEntry(
        val alias: String,
        val password: String,
        val privateKeyCertificatePair: PrivateKeyCertificatePair = newPrivateKeyCertificatePair()
    )

    /**
     * A private key and certificate pair.
     *
     * @param privateKey The private key.
     * @param certificate The certificate.
     */
    class PrivateKeyCertificatePair(
        val privateKey: PrivateKey,
        val certificate: X509Certificate,
    )
}