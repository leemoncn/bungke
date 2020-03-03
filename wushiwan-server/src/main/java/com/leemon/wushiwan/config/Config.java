package com.leemon.wushiwan.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description: 和yml对应的自定义的配置文件
 * @author: limeng
 * @create: 2019-05-09 22:40
 **/
@Component
@ConfigurationProperties(prefix = "custom")
public class Config {
	/**
	 * redis、mysql等服务器的地址
	 */
	private String service;
	/**
	 * 存储到本地的图片路径
	 */
	private String imgLocalPath;
	/**
	 * 用户上传的图片，最大的大小，单位是字节
	 */
	private int imgMaxSize;

	/**
	 * 做任务的超时时间（秒）
	 */
	private int missionTimeOutSecond;

	/**
	 * 任务不合格后有多少时间可以重新提交（秒）
	 */
	private int missionRejectRecommitSecond;

	/**
	 * 任务审核的最长时间(小时)
	 */
	private int missionReviewHour;

	/**
	 * 二维码过期时间
	 */
	private int qrcodeTimeoutDay;

	/**
	 * 当前服务器部署的域名
	 */
	private String serverDomain;

	/**
	 * 前端的登陆页面的域名
	 */
	private String clientDomain;

	/**
	 * 支付成功时回调地址
	 */
	private String payNotifyUrl;

	/**
	 * 七牛云上传图片成功后的回调地址
	 */
	private String qiniuKodoNotifyUrl;

	/**
	 * 前端的dist文件夹地址
	 */
	private String clientDistPath;

	/**
	 * 保存wgt文件的路径
	 */
	private String wgtFilePath;

	/**
	 * 支付宝转账时，对方显示的转账方名字
	 */
	private String alipayTransferName;

	/**
	 * 所有没填上级的注册用户，挂到这个人名下
	 */
	private Integer systemSuperiorId;

	/**
	 * 七牛云access key
	 */
	private String qiniuAccessKey;

	/**
	 * 七牛云secret key
	 */
	private String qiniuSecretKey;

	/**
	 * 七牛云对象存储的bucket name
	 */
	private String qiniuBucketName;

	/**
	 * jenkins管理端的地址
	 */
	private String jenkinsServerUrl;

	/**
	 * jenkins账号
	 */
	private String jenkinsAccount;

	/**
	 * jenkins密码
	 */
	private String jenkinsPassword;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getImgLocalPath() {
		return imgLocalPath;
	}

	public void setImgLocalPath(String imgLocalPath) {
		this.imgLocalPath = imgLocalPath;
	}

	public int getImgMaxSize() {
		return imgMaxSize;
	}

	public void setImgMaxSize(int imgMaxSize) {
		this.imgMaxSize = imgMaxSize;
	}

	public int getMissionTimeOutSecond() {
		return missionTimeOutSecond;
	}

	public void setMissionTimeOutSecond(int missionTimeOutSecond) {
		this.missionTimeOutSecond = missionTimeOutSecond;
	}

	public int getMissionReviewHour() {
		return missionReviewHour;
	}

	public void setMissionReviewHour(int missionReviewHour) {
		this.missionReviewHour = missionReviewHour;
	}

	public int getQrcodeTimeoutDay() {
		return qrcodeTimeoutDay;
	}

	public void setQrcodeTimeoutDay(int qrcodeTimeoutDay) {
		this.qrcodeTimeoutDay = qrcodeTimeoutDay;
	}

	public String getServerDomain() {
		return serverDomain;
	}

	public void setServerDomain(String serverDomain) {
		this.serverDomain = serverDomain;
	}

	public String getClientDomain() {
		return clientDomain;
	}

	public void setClientDomain(String clientDomain) {
		this.clientDomain = clientDomain;
	}

	public String getPayNotifyUrl() {
		return payNotifyUrl;
	}

	public void setPayNotifyUrl(String payNotifyUrl) {
		this.payNotifyUrl = payNotifyUrl;
	}

	public int getMissionRejectRecommitSecond() {
		return missionRejectRecommitSecond;
	}

	public void setMissionRejectRecommitSecond(int missionRejectRecommitSecond) {
		this.missionRejectRecommitSecond = missionRejectRecommitSecond;
	}

	public String getClientDistPath() {
		return clientDistPath;
	}

	public void setClientDistPath(String clientDistPath) {
		this.clientDistPath = clientDistPath;
	}

	public String getWgtFilePath() {
		return wgtFilePath;
	}

	public void setWgtFilePath(String wgtFilePath) {
		this.wgtFilePath = wgtFilePath;
	}

	public String getAlipayTransferName() {
		return alipayTransferName;
	}

	public void setAlipayTransferName(String alipayTransferName) {
		this.alipayTransferName = alipayTransferName;
	}

	public Integer getSystemSuperiorId() {
		return systemSuperiorId;
	}

	public void setSystemSuperiorId(Integer systemSuperiorId) {
		this.systemSuperiorId = systemSuperiorId;
	}

	public String getQiniuAccessKey() {
		return qiniuAccessKey;
	}

	public void setQiniuAccessKey(String qiniuAccessKey) {
		this.qiniuAccessKey = qiniuAccessKey;
	}

	public String getQiniuSecretKey() {
		return qiniuSecretKey;
	}

	public void setQiniuSecretKey(String qiniuSecretKey) {
		this.qiniuSecretKey = qiniuSecretKey;
	}

	public String getQiniuBucketName() {
		return qiniuBucketName;
	}

	public void setQiniuBucketName(String qiniuBucketName) {
		this.qiniuBucketName = qiniuBucketName;
	}

	public String getQiniuKodoNotifyUrl() {
		return qiniuKodoNotifyUrl;
	}

	public void setQiniuKodoNotifyUrl(String qiniuKodoNotifyUrl) {
		this.qiniuKodoNotifyUrl = qiniuKodoNotifyUrl;
	}

	public String getJenkinsServerUrl() {
		return jenkinsServerUrl;
	}

	public void setJenkinsServerUrl(String jenkinsServerUrl) {
		this.jenkinsServerUrl = jenkinsServerUrl;
	}

	public String getJenkinsAccount() {
		return jenkinsAccount;
	}

	public void setJenkinsAccount(String jenkinsAccount) {
		this.jenkinsAccount = jenkinsAccount;
	}

	public String getJenkinsPassword() {
		return jenkinsPassword;
	}

	public void setJenkinsPassword(String jenkinsPassword) {
		this.jenkinsPassword = jenkinsPassword;
	}
}
