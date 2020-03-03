package com.leemon.wushiwan.service;

import com.alibaba.fastjson.JSONObject;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.notify.Notify;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.base.payload.MultiMedia;
import com.gexin.rp.sdk.base.payload.VoIPPayload;
import com.gexin.rp.sdk.dto.GtReq;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.*;
import com.gexin.rp.sdk.template.style.AbstractNotifyStyle;
import com.gexin.rp.sdk.template.style.Style0;
import com.gexin.rp.sdk.template.style.Style6;
import com.google.common.base.Strings;
import com.leemon.wushiwan.dto.PushModel;
import com.leemon.wushiwan.exception.LogicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * @description: unipush推送（个推）
 * @author: limeng
 * @create: 2019-12-11 21:21
 **/
@Slf4j
@Component
public class PushService {
	// STEP1：获取应用基本信息
	private static final String APPID = "6BkPLqKLKp8I9MjdG5hEt3";
	private static final String APPKEY = "E6mkXS8nM29oMyjHleWFv7";
	private static final String MASTERSECRET = "A4hUTjabir8SY9Kza5tpO";
	private static final String URL = "http://sdk.open.api.igexin.com/apiex.htm";

	/**
	 * 如果pushId是空，则向全体成员发送
	 *
	 * @param model
	 * @param pushId
	 */
	public String pushMsg(PushModel model, String pushId) {
		IGtPush push = new IGtPush(URL, APPKEY, MASTERSECRET);
		TransmissionTemplate template = getTransmissionTemplate(model);

		List<String> appIds = new ArrayList<>();
		appIds.add(APPID);

		IPushResult ret;
		if (Strings.isNullOrEmpty(pushId)) {
			AppMessage message = new AppMessage();
			message.setData(template);
			message.setAppIdList(appIds);
			message.setOffline(true);//是否保持离线消息
			message.setOfflineExpireTime(3600000);//过多久该消息离线失效（单位毫秒） 支持1-72小时*3600000毫秒

			ret = push.pushMessageToApp(message);
			log.info("全局发送推送，model = {}，result = {}", model, ret.getResponse().toString());
		} else {
			Target t = new Target();
			t.setAppId(APPID);
			t.setClientId(pushId);
			SingleMessage message = new SingleMessage();
			message.setData(template);
			message.setOffline(true);//是否保持离线消息
			message.setOfflineExpireTime(3600000);//过多久该消息离线失效（单位毫秒） 支持1-72小时*3600000毫秒

			ret = push.pushMessageToSingle(message, t);
			log.info("指定用户发送推送，model = {}，pushId = {},result = {}", model, pushId, ret.getResponse().toString());
		}
		String result = (String) ret.getResponse().get("result");
		if (!"ok".equals(result)) {
			return result;
		}
		return null;
	}

	/**
	 * 点击通知打开应用模板, 在通知栏显示一条含图标、标题等的通知，用户点击后激活您的应用。
	 * 通知模板(点击后续行为: 支持打开应用、发送透传内容、打开应用同时接收到透传 这三种行为)
	 *
	 * @return
	 */
	private NotificationTemplate getNotificationTemplate() {
		NotificationTemplate template = new NotificationTemplate();
		template.setAppId(APPID);
		template.setAppkey(APPKEY);
		template.setStyle(getStyle0());
//		1：立即启动APP（不推荐使用，影响客户体验）
//		2：客户端收到消息后需要自行处理
		template.setTransmissionType(2);
		template.setTransmissionContent("推送内容1111");
		/**
		 * APP 关闭情况下，普通推送内容
		 *
		 * 为1时,提示通知，点击后打开APP，收到
		 * receive =
		 * Object {__UUID__: "androidPushMsg86259820",...
		 * __UUID__: "androidPushMsg86259820"
		 * content: "请输入您要透传的内容"
		 * payload: "请输入您要透传的内容"
		 * title: "五十万"
		 * __proto__: Object
		 *
		 * 为2时，不提示通知，打开APP提示通知，点击后同时收到click和receive
		 * click =
		 * Object {__UUID__: "androidPushMsg39755507",...
		 * __UUID__: "androidPushMsg39755507"
		 * appid: "H54790929"
		 * content: "当前时间为 2019-12-12T21:00:54.906"
		 * payload: "{"title":"五十万通知测试","content":"当前时间为 2019-12-12T21:00:54.906"}"
		 * title: "五十万通知测试"
		 * __proto__: Object
		 * receive =
		 * Object {__UUID__: "androidPushMsg223083950"...
		 * __UUID__: "androidPushMsg223083950"
		 * content: "请输入您要透传的内容222"
		 * payload: "请输入您要透传的内容222"
		 * title: "五十万"
		 * __proto__: Object
		 *
		 * APP 开启时，普通推送内容
		 * 为1和2时，收到通知，点击后，同时收到click和receive
		 */
		return template;
	}

	/**
	 * 点击通知打开(第三方)网页模板, 在通知栏显示一条含图标、标题等的通知，用户点击可打开您指定的网页。
	 *
	 * @return
	 */
	private LinkTemplate getLinkTemplate() {
		LinkTemplate template = new LinkTemplate();
		template.setAppId(APPID);
		template.setAppkey(APPKEY);

		//设置展示样式
		template.setStyle(getStyle0());
		template.setUrl("http://www.baidu.com");  // 设置打开的网址地址
		return template;
	}

	/**
	 * 透传消息模版,透传消息是指消息传递到客户端只有消息内容，展现形式由客户端自行定义。客户端可自定义通知的展现形式，也可自定义通知到达之后的动作，或者不做任何展现。
	 *
	 * @return
	 */
	private TransmissionTemplate getTransmissionTemplate(PushModel model) {
		TransmissionTemplate template = new TransmissionTemplate();
		template.setAppId(APPID);
		template.setAppkey(APPKEY);

		JSONObject obj = new JSONObject();
		obj.put("data", model);
		String modelString = JSONObject.toJSONString(obj);

		/**
		 * APP 关闭时，通知栏提示，点击后会收到click的notify里面的内容
		 * APP 开启时，通知栏不提示，会收到receive的透传消息的内容
		 */
//		1：立即启动APP（不推荐使用，影响客户体验）
//		2：客户端收到消息后需要自行处理
		template.setTransmissionType(2);
		template.setTransmissionContent(modelString); //透传内容
//		template.setAPNInfo(getAPNPayload()); //ios消息推送

		Notify notify = new Notify();
		notify.setTitle(model.getTitle());
		notify.setContent(model.getContent());
		notify.setPayload(JSONObject.toJSONString(model.getPayload()));
		String intentStr = String.format("intent:#Intent;launchFlags=0x14000000;component=%s/io.dcloud.PandoraEntry;S.UP-OL-SU=true;S.title=%s;S.content=%s;S.payload=%s;end", "com.bungke.android", notify.getTitle(), notify.getContent(), notify.getPayload());
		notify.setIntent(intentStr);
		notify.setType(GtReq.NotifyInfo.Type._intent);
		// notify.setUrl("https://dev.getui.com/");
		//notify.setType(Type._url);
		template.set3rdNotifyInfo(notify);
		return template;
	}

	//点击通知, 打开（自身）应用内任意页面
	private StartActivityTemplate getStartActivityTemplate() {
		StartActivityTemplate template = new StartActivityTemplate();
		// 设置APPID与APPKEY
		template.setAppId(APPID);
		template.setAppkey(APPKEY);
		//设置展示样式
		template.setStyle(getStyle0());

		String intent = "intent:#Intent;component=com.yourpackage/.NewsActivity;end";
		template.setIntent(intent); //最大长度限制为1000
		return template;
	}

	/**
	 * 消息撤回模版
	 *
	 * @param taskId
	 * @return
	 */
	private RevokeTemplate getRevokeTemplate(String taskId) {
		RevokeTemplate template = new RevokeTemplate();
		// 设置APPID与APPKEY
		template.setAppId(APPID);
		template.setAppkey(APPKEY);
		template.setOldTaskId(taskId); //指定需要撤回消息对应的taskId
		template.setForce(false); // 客户端没有找到对应的taskid，是否把对应appid下所有的通知都撤回

		return template;
	}

	private APNPayload getAPNPayload() {
		APNPayload payload = new APNPayload();
		//在已有数字基础上加1显示，设置为-1时，在已有数字上减1显示，设置为数字时，显示指定数字
		payload.setAutoBadge("+1");
		payload.setContentAvailable(1);
		//ios 12.0 以上可以使用 Dictionary 类型的 sound
		payload.setSound("default");
		payload.setCategory("$由客户端定义");
		payload.addCustomMsg("由客户自定义消息key", "由客户自定义消息value");

		//简单模式APNPayload.SimpleMsg
		payload.setAlertMsg(new APNPayload.SimpleAlertMsg("hello"));
//        payload.setAlertMsg(getDictionaryAlertMsg());  //字典模式使用APNPayload.DictionaryAlertMsg

		//设置语音播报类型，int类型，0.不可用 1.播放body 2.播放自定义文本
		payload.setVoicePlayType(2);
		//设置语音播报内容，String类型，非必须参数，用户自定义播放内容，仅在voicePlayMessage=2时生效
		//注：当"定义类型"=2, "定义内容"为空时则忽略不播放
		payload.setVoicePlayMessage("定义内容");

		// 添加多媒体资源
		payload.addMultiMedia(new MultiMedia().setResType(MultiMedia.MediaType.pic)
				.setResUrl("资源文件地址")
				.setOnlyWifi(true));

		return payload;
	}

	private APNPayload.DictionaryAlertMsg getDictionaryAlertMsg() {
		APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
		alertMsg.setBody("body1");
		alertMsg.setActionLocKey("显示关闭和查看两个按钮的消息");
		alertMsg.setLocKey("loc-key1");
		alertMsg.addLocArg("loc-ary1");
		alertMsg.setLaunchImage("调用已经在应用程序中绑定的图形文件名");
		// iOS8.2以上版本支持
		alertMsg.setTitle("通知标题");
		alertMsg.setTitleLocKey("自定义通知标题");
		alertMsg.addTitleLocArg("自定义通知标题组");
		return alertMsg;
	}

	/**
	 * 需要使用iOS语音传输，请使用VoIPPayload代替APNPayload
	 *
	 * @return
	 */
	private VoIPPayload getVoIPPayload() {
		VoIPPayload payload = new VoIPPayload();
		JSONObject jo = new JSONObject();
		jo.put("key1", "value1");
		payload.setVoIPPayload(jo.toString());
		return payload;
	}

	/**
	 * Style0 系统样式
	 *
	 * @return
	 * @link http://docs.getui.com/getui/server/java/template/ 查看效果
	 */
	private AbstractNotifyStyle getStyle0() {
		Style0 style = new Style0();
		// 设置通知栏标题与内容
		style.setTitle("五十万通知测试");
		style.setText("当前时间为 " + LocalDateTime.now().toString());
		// 配置通知栏图标
		style.setLogo("icon.png"); //配置通知栏图标，需要在客户端开发时嵌入，默认为push.png
		// 配置通知栏网络图标
		style.setLogoUrl("");

		// 设置通知是否响铃，震动，或者可清除
		style.setRing(true);
		style.setVibrate(true);
		style.setClearable(true);
		style.setChannel("通知渠道id");
		style.setChannelName("通知渠道名称");
//		0：无声音，无震动，不显示。(不推荐)
//		1：无声音，无震动，锁屏不显示，通知栏中被折叠显示，导航栏无logo。
//		2：无声音，无震动，锁屏和通知栏中都显示，通知不唤醒屏幕。
//		3：有声音，有震动，锁屏和通知栏中都显示，通知唤醒屏幕。（推荐）
//		4：有声音，有震动，亮屏下通知悬浮展示，锁屏通知以默认形式展示且唤醒屏幕。（推荐）
		style.setChannelLevel(4); //设置通知渠道重要性
		return style;
	}

	/**
	 * Style6 展开式通知样式
	 * Style6-1：大图+文本样式
	 * Style6-2：长文本样式
	 *
	 * @return
	 * @link http://docs.getui.com/getui/server/java/template/ 查看效果
	 */
	private AbstractNotifyStyle getStyle6() {
		Style6 style = new Style6();
		// 设置通知栏标题与内容
		style.setTitle("请输入通知栏标题");
		style.setText("请输入通知栏内容");
		// 配置通知栏图标
		style.setLogo("icon.png"); //配置通知栏图标，需要在客户端开发时嵌入
		// 配置通知栏网络图标
		style.setLogoUrl("");
		// 三种方式选一种
		style.setBigStyle1("bigImageUrl"); //设置大图+文本样式
//        style.setBigStyle2("bigText"); //设置长文本+文本样式

		// 设置通知是否响铃，震动，或者可清除
		style.setRing(true);
		style.setVibrate(true);
		style.setClearable(true);
		style.setChannel("通知渠道id");
		style.setChannelName("通知渠道名称");
		style.setChannelLevel(4); //设置通知渠道重要性
		return style;
	}
}
