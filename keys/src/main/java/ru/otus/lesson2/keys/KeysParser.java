package ru.otus.lesson2.keys;

import lombok.SneakyThrows;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeysParser {
	@SneakyThrows
	public static RSAPublicKey getPublicKeyFromString(String key) {
		String publicKeyPEM = key;

		publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
		publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
		publicKeyPEM = publicKeyPEM.replaceAll("\\s+","");

		byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
		KeyFactory kf = KeyFactory.getInstance("RSA");

		return (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
	}

	@SneakyThrows
	public static RSAPrivateKey getPrivateKeyFromString(String key) {
		String privateKeyPEM = key;
		privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
		privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
		privateKeyPEM = privateKeyPEM.replaceAll("\\s+","");

		byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
		return (RSAPrivateKey) kf.generatePrivate(keySpec);
	}
}
