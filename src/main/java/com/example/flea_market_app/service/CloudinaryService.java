package com.example.flea_market_app.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {
	private final Cloudinary cloudinary;

	public CloudinaryService(@Value("${cloudinary.cloud_name}") String cloudName,
			@Value("${cloudinary.api_key}") String apiKey,
			@Value("${cloudinary.api_secret}") String apiSecret) {
		this.cloudinary = new Cloudinary(ObjectUtils.asMap(
				"cloud_name", cloudName,
				"api_key", apiKey,
				"api_secret", apiSecret));
	}

	public String uploadFile(MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			return null;
		}

		Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
				ObjectUtils.emptyMap());
		return uploadResult.get("url").toString();
	}

	public void deleteFile(String imageUrl) {
		// 1. URLが空、またはCloudinaryのドメインを含まない場合は何もしない
		if (imageUrl == null || imageUrl.isEmpty() || !imageUrl.contains("res.cloudinary.com")) {
			return;
		}

		try {
			// 2. URLから public_id を安全に抽出する
			// 例: http://res.cloudinary.com/cloudname/image/upload/v12345/sample.jpg
			// から "sample" を取り出す
			String[] parts = imageUrl.split("/");
			String fileNameWithExtension = parts[parts.length - 1]; // "sample.jpg"

			int dotIndex = fileNameWithExtension.lastIndexOf('.');

			if (dotIndex != -1) {
				// ドットが見つかった場合のみ切り取る
				String publicId = fileNameWithExtension.substring(0, dotIndex);

				// 3. Cloudinaryから削除実行
				cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
				System.out.println("Cloudinaryから画像を削除しました: " + publicId);
			}
		} catch (Exception e) {
			// 画像削除の失敗で商品削除（メイン処理）を止めないよう、ログ出力に留める
			System.err.println("Cloudinaryの画像削除に失敗しましたが、処理を続行します: " + e.getMessage());
		}
	}
}