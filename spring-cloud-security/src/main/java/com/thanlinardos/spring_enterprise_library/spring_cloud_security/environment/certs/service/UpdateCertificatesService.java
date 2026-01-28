package com.thanlinardos.spring_enterprise_library.spring_cloud_security.environment.certs.service;

import com.thanlinardos.spring_enterprise_library.error.errorcodes.ErrorCode;
import com.thanlinardos.spring_enterprise_library.error.exceptions.CoreException;
import com.thanlinardos.spring_enterprise_library.objects.utils.StringUtils;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.environment.certs.ClientCertificateType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

@Service
@Slf4j
public class UpdateCertificatesService {

    @Value("${spring.ssl.bundle.jks.server.keystore.location:null}")
    private String serverKeyStorePath;
    @Value("${thanlinardos.springenterpriselibrary.oauth2.client-certificate-path-format}")
    private String clientCertificatePathFormat;

    public void saveClientCertificate(MultipartFile pem) throws IOException {
        String name = ClientCertificateType.valueOf(pem.getName()).name();
        saveCertificate(pem, name, getClientCertificatePath(name));
    }

    public void saveServerCertificate(MultipartFile pem) throws IOException {
        if (serverKeyStorePath == null) {
            throw new CoreException(ErrorCode.CONFIG_PROPERTY_NOT_FOUND, "Server keystore path is not configured");
        }
        saveCertificate(pem, "server", serverKeyStorePath);
    }

    private String getClientCertificatePath(String name) {
        return StringUtils.formatMessageWithArgs(clientCertificatePathFormat, name);
    }

    private void saveCertificate(MultipartFile pem, String name, String path) throws IOException {
        log.info("Updating {} certificate from file {}", name, path);
        File certFile = new File(path);
        try (FileOutputStream fos = new FileOutputStream(certFile)) {
            fos.write(pem.getBytes());
        }
    }

    // TODO: test and use
    private void validateCertificateAndCheckDuplicate(MultipartFile pem, String targetPath) throws IOException, NoSuchAlgorithmException, CertificateException {
        byte[] pemBytes = pem.getBytes();
        X509Certificate cert;
        cert = parseCertificate(pemBytes);
        cert.checkValidity();

        String fingerprint;
        try {
            fingerprint = computeFingerprintBase64(cert);
        } catch (NoSuchAlgorithmException | CertificateException e) {
            throw new IOException(e);
        }

        Path target = Paths.get(targetPath);
        Path parent = target.getParent();

        // Check the exact target file first (if exists)
        if (Files.exists(target) && Files.isRegularFile(target)) {
            X509Certificate existing = parseCertificate(Files.readAllBytes(target));
            if (existing != null && fingerprint.equals(computeFingerprintBase64(existing))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate certificate");
            }
        }

        // If parent directory exists, scan siblings for duplicates
        if (parent != null && Files.isDirectory(parent)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(parent)) {
                for (Path p : stream) {
                    if (!Files.isRegularFile(p)) continue;
                    X509Certificate existing = parseCertificate(Files.readAllBytes(p));
                    if (existing != null && fingerprint.equals(computeFingerprintBase64(existing))) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate certificate");
                    }
                }
            }
        }
    }

    private X509Certificate parseCertificate(byte[] pemBytes) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try (ByteArrayInputStream in = new ByteArrayInputStream(pemBytes)) {
            return (X509Certificate) cf.generateCertificate(in);
        } catch (IOException e) {
            // ByteArrayInputStream won't actually throw here, wrap as CertificateException
            throw new CertificateException(e);
        }
    }

    private String computeFingerprintBase64(X509Certificate cert) throws NoSuchAlgorithmException, CertificateException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(cert.getEncoded());
        return Base64.getEncoder().encodeToString(digest);
    }
}
