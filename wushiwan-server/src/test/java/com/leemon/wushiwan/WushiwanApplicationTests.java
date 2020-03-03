package com.leemon.wushiwan;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.leemon.wushiwan.config.Config;
import com.leemon.wushiwan.controller.MayiMissionController;
import com.leemon.wushiwan.dto.PushModel;
import com.leemon.wushiwan.dto.PushPayloadToast;
import com.leemon.wushiwan.dto.UploadToken;
import com.leemon.wushiwan.entity.BaseModel;
import com.leemon.wushiwan.enums.CurrencyChangeReasonType;
import com.leemon.wushiwan.enums.PartnerTimeType;
import com.leemon.wushiwan.jwt.JwtTokenUtil;
import com.leemon.wushiwan.mapper.CoreMissionDetailMapper;
import com.leemon.wushiwan.mapper.SocialFollowMapper;
import com.leemon.wushiwan.pay.PayService;
import com.leemon.wushiwan.service.*;
import com.leemon.wushiwan.service.impl.CoreVersionServiceImpl;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.system.service.ISysUserService;
import com.leemon.wushiwan.vo.CreateMissionRequest;
import com.leemon.wushiwan.vo.UploadTokenResponse;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev-win")
@Slf4j
public class WushiwanApplicationTests {

	@Autowired
	private ISysPropertyService sysPropertyService;
	@Autowired
	private ValueOperations<String, Object> redisVO;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private ICorePartnerPurchaseService partnerPurchaseService;
	@Autowired
	private ICorePartnerService partnerService;
	@Autowired
	private ICoreDepositPurchaseService depositPurchaseService;
	@Autowired
	private PayService payService;
	@Autowired
	private ISysAdviceService adviceService;
	@Autowired
	private CoreMissionDetailMapper missionDetailMapper;
	@Autowired
	private SocialFollowMapper followMapper;
	@Autowired
	private ICoreLoginAuthService coreLoginAuthService;
	@Autowired
	private Config config;
	@Autowired
	private QiniuKodoService kodoService;
	@Autowired
	private ListOperations<String, Object> redisList;
	@Autowired
	private ICoreMissionAcceptService acceptService;
	@Autowired
	private CoreVersionServiceImpl versionService;
	@Autowired
	private PushService pushService;
	@Autowired
	private MayiMissionController mayiMissionController;

	@Test
	public void contextLoads() throws InterruptedException {
		partnerService.getNonePartner();
	}

	@Test
	public void withdrawalEarning() throws InterruptedException {
		SysUser user = sysUserService.getById(100079);
		sysUserService.updateUserEarning(user, CurrencyChangeReasonType.WITHDRAWAL_EARNING, -100);
	}

	@Test
	public void withdrawalMissionCoin() throws InterruptedException {
		SysUser user = sysUserService.getById(100079);
		sysUserService.updateUserMissionCoin(user, CurrencyChangeReasonType.WITHDRAWAL_MISSION_COIN, -100);
	}

	@Test
	public void rechargeMissionCoin() throws InterruptedException {
		SysUser user = sysUserService.getById(100079);
		sysUserService.updateUserMissionCoin(user, CurrencyChangeReasonType.RECHARGE_MISSION_COIN, 100);
	}

	@Test
	public void buyPartner() throws InterruptedException {
		SysUser user = sysUserService.getById(100079);
		partnerPurchaseService.addNewPartner(user, 4, PartnerTimeType.YEAH, null);
	}

	@Test
	public void rechargeDeposit() throws InterruptedException {
		depositPurchaseService.rechargeDeposit(100, 100079);
//		depositPurchaseService.refundDeposit(100079);
	}

	@Test
	public void submitAdvice() throws InterruptedException {
		adviceService.addNewAdvice("123456", "ffff", 100079, null);
	}

	@Test
	public void missionPage() {
//		Page<MissionDetail> page = new Page<>(1, 30);
//		List<MissionDetail> list = missionDetailMapper.searchMissionDetail(page, 28);
//		page.setRecords(list);
//		log.info("page = {}", page);
//		QueryWrapper<MissionDetail> qw = new QueryWrapper<>();
//		qw.lambda().eq(MissionDetail::getStatusPropertyId, 28).orderByAsc(MissionDetail::getTopEndTime);
//		log.info("a = {}", qw.getSqlSelect());
////		log.info("a = {}", qw.getSqlSegment());
//		log.info("a = {}", qw.getSqlSet());
//		log.info("a = {}", qw.getCustomSqlSegment());
//		log.info("a = {}", qw.getParamAlias());
//		log.info("a = {}", qw.getParamAlias());
	}

	@Test
	public void followTest() {
		Page<SysUser> page = new Page<>(1, 10);
		followMapper.selectFollowedUserList(page, 100079);
		log.info("page = {}", page);
	}

	@Test
	public void createUploadToken() {
		Auth auth = Auth.create(config.getQiniuAccessKey(), config.getQiniuSecretKey());
		String upToken = auth.uploadToken(config.getQiniuBucketName());
		log.info("token = {}", upToken);
	}

	@Test
	public void uploadFile() {
		//构造一个带指定 Region 对象的配置类
		Configuration cfg = new Configuration(Region.huadong());
//...其他参数参考类注释
		UploadManager uploadManager = new UploadManager(cfg);
//...生成上传凭证，然后准备上传
		String accessKey = config.getQiniuAccessKey();
		String secretKey = config.getQiniuSecretKey();
		String bucket = config.getQiniuBucketName();
//如果是Windows情况下，格式是 D:\\qiniu\\test.png
		String localFilePath = "C:\\Users\\limeng\\Desktop\\222.png";
//默认不指定key的情况下，以文件内容的hash值作为文件名
		String key = "aaa/bbb";
		Auth auth = Auth.create(accessKey, secretKey);
		StringMap putPolicy = new StringMap();
//		putPolicy.put("isPrefixalScope", 1);
		long expireSeconds = 3600;
		String upToken = auth.uploadToken(bucket, key, expireSeconds, putPolicy);
		log.info("111");
		try {
			Response response = uploadManager.put(localFilePath, null, upToken);
			//解析上传成功的结果
			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
			System.out.println(putRet.key);
			System.out.println(putRet.hash);
		} catch (QiniuException ex) {
			log.info("aa = {}", ex);
//			Response r = ex.response;
//			System.err.println(r.toString());
//			try {
//				System.err.println(r.bodyString());
//			} catch (QiniuException ex2) {
//				//ignore
//			}
		}
//		localFilePath = "C:\\Users\\limeng\\Desktop\\762034402451833099.png";
//		log.info("222");
//		try {
//			Response response = uploadManager.put(localFilePath, key, upToken);
//			//解析上传成功的结果
//			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
//			System.out.println(putRet.key);
//			System.out.println(putRet.hash);
//		} catch (QiniuException ex) {
//			Response r = ex.response;
//			System.err.println(r.toString());
//			try {
//				System.err.println(r.bodyString());
//			} catch (QiniuException ex2) {
//				//ignore
//			}
//		}
	}

	@Test
	public void kodoWriteTest() {
		UploadTokenResponse res = kodoService.createUploadFileToken(100098, 2);
		log.info("fff = {}", res);
	}

	@Test
	public void kodoReadTest() {
		List<UploadToken> list = (List<UploadToken>) redisVO.get("f668ebd3ad9f4e9a963db0f483804c74");
		for (UploadToken uploadToken : list) {
			log.info("date = {}", uploadToken.getExpirationTime());
		}
	}

	@Test
	public void kodoReasssdTest() {
		kodoService.deleteTimeoutUpload();
	}

	@Test
	public void kodoDeleteTest() {
		//构造一个带指定 Region 对象的配置类
		Configuration cfg = new Configuration(Region.huadong());
		String key = "222.png";
		Auth auth = Auth.create(config.getQiniuAccessKey(), config.getQiniuSecretKey());
		BucketManager bucketManager = new BucketManager(auth, cfg);
		try {
			bucketManager.delete(config.getQiniuBucketName(), key);
		} catch (QiniuException ex) {
			//如果遇到异常，说明删除失败
			System.err.println(ex.code());
			System.err.println(ex.response.toString());
		}
	}

	@Test
	public void banMissionAccept() {
		acceptService.banMissionAccept(1000088);
	}

	@Test
	public void gwtTest() {
		versionService.startJenkinsJob("dev-wushiwan-admin");
	}

	@Test
	public void pushTest() {
		PushPayloadToast toast = new PushPayloadToast();
		toast.setMsg("toast提示一下");
		PushModel model = PushModel.builder().title("我是标题5").content("1123").build();
		model.addSinglePayload(toast);
		pushService.pushMsg(model, null);
	}

	public static void main(String[] args) {
		Mono<BaseModel> resp = WebClient.create().post()
				.uri("http://localhost:9091/core-mission/create")
				.header("Authorization", "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjIxOTk4NjE1MTgsInN1YiI6IjEwMDEwNSIsImlhdCI6MTU3Nzc4MTUxOH0.8Xxjm3mKFeQ8Lu9NjtN2R4OQfApVusWnVOaHaJOKqSpMJEHgcqUgyLWqZ8Q7cbe7tVL5W56LG-zj1BnNgIocJg")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.syncBody("aaa")
				.retrieve().bodyToMono(new ParameterizedTypeReference<BaseModel>() {
				});
		BaseModel respModel = resp.block();
		log.info("model = {}", respModel);
	}

	@Test
	public void mayiCreateMission() {
//		MayiMissionController.CreateMayiMissionRequest req = new MayiMissionController.CreateMayiMissionRequest();
//		req.setMissionNo("1901114");
//		req.setSessionId("4877501CB4A556CE70E118C92633E250");
//		mayiMissionController.createMission(req);
//		String token = jwtTokenUtil.generateToken(null, String.valueOf(100105));
//		log.info("token = {}", token);
//		eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjIxOTk4NjE1MTgsInN1YiI6IjEwMDEwNSIsImlhdCI6MTU3Nzc4MTUxOH0.8Xxjm3mKFeQ8Lu9NjtN2R4OQfApVusWnVOaHaJOKqSpMJEHgcqUgyLWqZ8Q7cbe7tVL5W56LG-zj1BnNgIocJg
//		String token2 = jwtTokenUtil.generateToken(null, String.valueOf(100130));
//		log.info("token2 = {}", token2);
//		eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjIxOTk4NjE1MTgsInN1YiI6IjEwMDEzMCIsImlhdCI6MTU3Nzc4MTUxOH0.UlcG-dFoKVqH7HVffw4G2pP3lz_W-7QELt_YPdKNhaQgRxUBDCHV19_7gk3yiXuKFPGw5ZArZBGSdKhC-DYYwQ
//		Authorization
		CreateMissionRequest req = new CreateMissionRequest();
		req.setTitle("aaaaaaaa");

		Mono<String> resp = WebClient.create().post()
				.uri("http://localhost:9091/core-mission/create")
				.header("Authorization", "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjIxOTk4NjE1MTgsInN1YiI6IjEwMDEwNSIsImlhdCI6MTU3Nzc4MTUxOH0.8Xxjm3mKFeQ8Lu9NjtN2R4OQfApVusWnVOaHaJOKqSpMJEHgcqUgyLWqZ8Q7cbe7tVL5W56LG-zj1BnNgIocJg")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(Mono.just(req), CreateMissionRequest.class)
				.retrieve().bodyToMono(String.class);
		String respStr = resp.block();
		log.info("respStr = {}", respStr);
	}

	@Test
	public void aaaa() {
		sysUserService.removePushIdFromAllUser("111");
	}

}

