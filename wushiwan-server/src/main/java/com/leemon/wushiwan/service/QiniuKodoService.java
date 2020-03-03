package com.leemon.wushiwan.service;

import com.google.common.base.Strings;
import com.leemon.wushiwan.config.Config;
import com.leemon.wushiwan.dto.UploadToken;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.vo.UploadTokenResponse;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @description: 七牛云对象存储操作类
 * @author: leemon
 * @create: 2019-11-11 10:25
 **/
@Slf4j
@Component
public class QiniuKodoService {

	private final Config config;
	private final ListOperations<String, Object> redisList;
	private final RedisTemplate<String, Object> redisTemplate;
	private static final long EXPIRE_SECONDS = 3600;
	private static final Region REGION = Region.huadong();
	private static final String UPLOAD_FILE_PREFIX = "upload_file_";


	public QiniuKodoService(Config config, ListOperations<String, Object> redisList, RedisTemplate<String, Object> redisTemplate) {
		this.config = config;
		this.redisList = redisList;
		this.redisTemplate = redisTemplate;
	}

	/**
	 * 创建上传凭证
	 * @param key 文件名
	 * @return
	 */
	private String createUploadToken(String key) {
		Auth auth = Auth.create(config.getQiniuAccessKey(), config.getQiniuSecretKey());
		StringMap putPolicy = new StringMap();
		putPolicy.put("callbackUrl", config.getQiniuKodoNotifyUrl());
		putPolicy.put("callbackBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":$(fsize),\"redisKey\":\"$(x:redisKey)\"}");
		putPolicy.put("callbackBodyType", "application/json");
		return auth.uploadToken(config.getQiniuBucketName(), key, EXPIRE_SECONDS, putPolicy);
	}

	/**
	 * 创建上传七牛云的凭证
	 *
	 * @param userId
	 * @param number 需要上传的数量
	 * @return
	 */
	public UploadTokenResponse createUploadFileToken(Integer userId, Integer number) {
		UploadTokenResponse res = new UploadTokenResponse();
		List<UploadToken> list = new ArrayList<>();

		for (Integer i = 0; i < number; i++) {
			UploadToken token = new UploadToken();

			String fileName = UUID.randomUUID().toString().replace("-", "");
			String path = userId.toString() + "/" + fileName;
			token.setFileName(path);
			token.setIsReceiveCallback(false);

			String tokenStr = createUploadToken(path);
			token.setToken(tokenStr);

			LocalDateTime ldt = LocalDateTime.now().plusSeconds(EXPIRE_SECONDS);
			token.setExpirationTime(ldt);

			list.add(token);
		}
		res.setList(list);
		String key = UUID.randomUUID().toString().replace("-", "");
		key = UPLOAD_FILE_PREFIX + key;
		res.setKey(key);

		//将这个结构存储到redis中
		redisList.leftPushAll(key, (Collection) list);
		return res;
	}

	/**
	 * 用户上传成功，七牛云回调业务服务器
	 *
	 * @param redisKey
	 * @param fileName
	 */
	public void fileUploadSuccess(String redisKey, String fileName) {
		if (!Strings.isNullOrEmpty(redisKey) && !Strings.isNullOrEmpty(fileName)) {
			List<Object> list = redisList.range(redisKey, 0, -1);
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					UploadToken uploadToken = (UploadToken) list.get(i);
					if (uploadToken.getFileName().equals(fileName)) {
						uploadToken.setIsReceiveCallback(true);
						redisList.set(redisKey, i, uploadToken);
						break;
					}
				}
			}
		}
	}

	private void deleteFileFromQiniuAndRedis(List<Object> list, String key) {
		for (Object obj : list) {
			UploadToken uploadToken = (UploadToken) obj;
			if (uploadToken.getIsReceiveCallback()) {
				try {
					deleteFileFromKodo(uploadToken.getFileName());
				} catch (Exception e) {
					log.info("七牛云文件{}删除失败，原因：{}", uploadToken.getFileName(), e.getMessage());
				}
			}
		}
		redisTemplate.delete(key);
	}

	/**
	 * 将所有的超时的upload token删除。所以一旦将图片提交上来，就应该把redis里面的key删掉
	 */
	public void deleteTimeoutUpload() {
		String prefix = UPLOAD_FILE_PREFIX + "*";
		Set<String> keys = redisTemplate.keys(prefix);
		if (keys != null) {
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				boolean needDelete = false;
				List<Object> list = redisList.range(key, 0, -1);
				if (list != null) {
					for (Object obj : list) {
						UploadToken uploadToken = (UploadToken) obj;
						if (uploadToken.getExpirationTime().isBefore(LocalDateTime.now())) {//超时
							needDelete = true;
							break;
						}
					}
					if (needDelete) {
						try {
							deleteFileFromQiniuAndRedis(list, key);
						} catch (Exception e) {
							log.error("从七牛云删除文件失败 = {}", e.getMessage());
						}
					}
				}
			}
		}
	}

	/**
	 * 从七牛云删除文件
	 */
	private void deleteFileFromKodo(String fileName) {
		Configuration cfg = new Configuration(REGION);
		Auth auth = Auth.create(config.getQiniuAccessKey(), config.getQiniuSecretKey());
		BucketManager bucketManager = new BucketManager(auth, cfg);
		try {
			bucketManager.delete(config.getQiniuBucketName(), fileName);
		} catch (QiniuException e) {
			throw new LogicException(ErrorCode.SYS_ERROR, e.getMessage());
		}
	}

	/**
	 * 判断是否这个key里面的所有文件都已经上传完成了
	 */
	private boolean isAllFileUploaded(List<Object> list) {
		if (list != null) {
			//是否所有的file都已完成
			boolean allFileUploaded = true;
			for (Object obj : list) {
				UploadToken uploadToken = (UploadToken) obj;
				if (!uploadToken.getIsReceiveCallback()) {
					allFileUploaded = false;
					break;
				}
			}
			return allFileUploaded;
		}
		return false;
	}

	/**
	 * 如果所有文件上传完成，就删除redis里面的key
	 *
	 * @param key
	 */
	public List<String> deleteAllFileUploadedRedisKey(String key) {
		if (!Strings.isNullOrEmpty(key)) {
			List<Object> list = redisList.range(key, 0, -1);
			boolean allUploaded = isAllFileUploaded(list);
			if (!allUploaded) {
				throw new LogicException("文件上传未完成，请先完成文件上传");
			}
			List<String> pathList = new ArrayList<>();
			for (Object obj : list) {
				UploadToken uploadToken = (UploadToken) obj;
				pathList.add(uploadToken.getFileName());
			}
			redisTemplate.delete(key);
			return pathList;
		}
		return null;
	}

	public void deleteAllFileUploadedRedisKey(List<String> keyList) {
		for (String s : keyList) {
			deleteAllFileUploadedRedisKey(s);
		}
	}

	/**
	 * 服务器直接上传图片
	 * @param fileList 可以是http地址，也可以是本地磁盘地址
	 * @return redis的key
	 */
	public UploadTokenResponse uploadFileFromServer(List<String> fileList,Integer userId){
		UploadTokenResponse res = this.createUploadFileToken(userId, fileList.size());

		Configuration cfg = new Configuration(Region.region0());
		UploadManager uploadManager = new UploadManager(cfg);

		for (int i = 0; i < fileList.size(); i++) {
			String s = fileList.get(i);
			String uploadToken = res.getList().get(i).getToken();
			String fileName = res.getList().get(i).getFileName();

			if (s.startsWith("http")) {//网络图片
				try {
					Response response = uploadManager.put(getImageStream(s), fileName, uploadToken, null, null);
					if (!response.isOK()) {
						throw new LogicException("上传网络图片出错,路径 = " + s);
					}
				} catch (QiniuException ex) {
					throw new LogicException("上传网络图片出错,路径 = " + s);
				}
			}else{//本地图片
				try {
					Response response = uploadManager.put(s, fileName, uploadToken);
					if (!response.isOK()) {
						throw new LogicException("上传本地图片出错,路径 = " + s);
					}
				} catch (QiniuException ex) {
					throw new LogicException("上传本地图片出错,路径 = " + s);
				}
			}
		}
		return res;
	}

	private InputStream getImageStream(String url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return connection.getInputStream();
			}
		} catch (IOException e) {
			throw new LogicException("获取网络图片出现异常，图片路径为：" + url);
		}
		return null;
	}
}
