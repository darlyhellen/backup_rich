package com.ytdinfo.keephealth.app;

import android.os.Environment;

public class Constants {
	// AppID：wxe9dfaf997a35d828
	// AppSecret：e98b52d02f8112bcc93181490b980aab
	private Constants() {
	};
 
	public static  String DESCRIPTOR = "com.umeng.share";
 
	/** 是否第一次使用 */
	public static final String ISFIRSTCOME = "isfirstcome_new";
	/** 新消息提醒 */
	public static final String ALERT = "MessageObrive";
	/** viewPager的本地图片路径 */
	public static final String VIEWPAGER_IMAGEPATH = "viewpager_imagepath";
	/** viewPager的本地图片路径 */
	public static final String VIEWPAGER = "viewpager";
	/** 统计活跃用户，访问日期存储 */
	public static final String DATE = "date";
	/** 加载banner判断 */
	public static final String ISLOADED = "isloaded";
	/** 用户token标示 */
	public static final String TOKEN = "token_new";
	public static final String CHECKEDID_RADIOBT = "checkedid_radiobt";
	public static final String CHECKISUPDATE = "checkisupdate";

	public static final String NOTUPDATE = "notupdate";
	/** 用户帐号 */
	public static final String UserAccount = "UserAccount";
	/** 用户密码 */
	public static final String Password = "Password";
	/** 用户姓名 */
	public static final String UserName = "UserName";
	/** 用户性别 */
	public static final String UserSex = "UserSex";
	/** 用户身份证号 */
	public static final String UserIdcard = "UserIdcard";
	public static final String USERID = "userid";
	public static final String USERMODEL = "usermodel";
	/** 对话的 subject ID */
	public static final String SUBJECTID = "subjectId";
	public static final String CHATINFO = "chatInfo";
	/** 聊天进度条时间长度 */
	public static final int PROGRESSMAX = 15 * 60;
	// public static final int PROGRESSMAX = 30;
	/** 结束咨询命令 */
	public static final String CLOSESUBJECT = "BMY://CloseSubject";
	// 在线问诊人员信息
	public static final String ONLINE_QUES_USERMODEL = "OnlineQuestionUserModel";
	public static final String ONLINE_QUES_USERMODEL_ME = "OnlineQuestionUserModel_me";
	/** 服务器地址 */
	// 开发环境

//	public static final String ROOT_URl = "http://172.3.207.15";
 
	// 测试环境
//	 public static final String ROOT_URl = "http://172.3.207.26";
	// 正式环境
	  public static final String ROOT_URl = "http://api.bmyi.cn";
 
//	 public static final String ROOT_URl = "http://172.3.207.26";
	// public static final String ROOT_URl = "http://test.rayelink.com";
	// public static final String ROOT_URl = "http://192.168.0.148:818";
	// public static final String ROOT_URl = "http://192.168.0.155";
	// public static final String ROOT_URl = "http://api.bmyi.cn";
 
	public static final String SERVICE_URl = ROOT_URl + "/APIAccount/";
	/** 登录地址 */
	public static final String LOGIN_URl = SERVICE_URl + "GetAPIAccountInfo2";
	/** 获取验证码地址 */
	public static final String VERIFICATION_URl = SERVICE_URl
			+ "GetVerificationcode";
	/** （忘记密码）获取验证码地址 */
	public static final String FORGETPASS_VERIFICATION_URl = SERVICE_URl
			+ "Verificationcodeforpassword";
	/** （忘记密码）重置密码 */
	public static final String FORGETPASS_RESETPASS_URl = SERVICE_URl
			+ "ChangePassword";
	/** 修改密码 */
	public static final String MODIFYPASS_URl = SERVICE_URl + "UpdatePassword";
	/** 验查验证码是否正确 地址 */
	public static final String CHECK_VERIFICATION_URl = SERVICE_URl
			+ "Verificationphonecode";
	/** 设置密码 地址 */
	public static final String SETPASS_URl = SERVICE_URl + "ApiUserAdd";
	/** 用户修改信息 地址 */
	public static final String MODIFYINFO_URl = SERVICE_URl + "ApiUserModify";
	/** 意见反馈图片 地址 */
	public static final String OPINION_FEEDBACK_IMAGES_URl = SERVICE_URl
			+ "ApiUploadPicforFeedback";
	/** 意见反馈 地址 */
	public static final String OPINION_FEEDBACK_URl = ROOT_URl
			+ "/api/Feedback/AddFeedback";
	/** 在线问诊图片 地址 */
	public static final String ONLINE_QUESTION_IMAGES_URl = SERVICE_URl
			+ "ApiUploadPicforOnlineConsultation";
	/** 在线问诊提交 地址 */
	public static final String ONLINE_QUESTION_SUBMIT_URl = ROOT_URl
			+ "/api/AppClient/StartChat";
	/** 体检报告图片 地址 */
	public static final String REPORT_IMAGES_URl = SERVICE_URl
			+ "ApiUploadPicforExaminationReport";
	/** 登录关闭清空会话地址 */
	public static final String LOGIN_CLOSE_SUBJECT = ROOT_URl + "/api/subject";// GET方式
	/** 评价 地址 */
	public static final String EVALUATION_URL = ROOT_URl + "/api/Chat/Comment";
	/** 上传消息 */
	public static final String UPLOAD_MESSAGE_URL = ROOT_URl
			+ "/api/Message/SaveMessage";
	/** 判断医生是否在线的接口 */
	public static final String DOCISONLINE = ROOT_URl + "/api/Retry";
	/** 云通讯服务器地址判断接口 */
	public static final String CHANGECHANNEL = ROOT_URl + "/api/ChangeChannel";

	/** 帮忙医APP的存储位置 */
	public static final String STORAGE_ROOT_DIR = Environment
			.getExternalStorageDirectory() + "/KeepHealth/";
	/** 图片存储位置 */
	public static final String IMAGES_DIR = STORAGE_ROOT_DIR + "images/";
	/** 拍照的图片路径 */
	public static String TAKEPHOTO_PATH;
	/** 拍照的图片路径 */
	public static String TAKEPHOTO_TEMP_PATH = Constants.IMAGES_DIR
			+ System.currentTimeMillis() + ".png";
	/** Webview缓存位置 */
	public static final String WEBVIEW_CACHE_DIR = STORAGE_ROOT_DIR
			+ "webcache2/";
	/** 压缩后图片名字 */
	public static final String CROPED_IMAGE_NAME = "croped_image.png";
	/** 头像的路径(所有照片的临时存储位置和名字) */
	public static final String HEAD_PICTURE_PATH = IMAGES_DIR + "head.png";
	/** 头像的名字及格式 */
	public static final String HEAD_PICTURE = "head.png";
	/** 在线问诊照片的路径 */
	public static final String ONLINE_QUESTION_PICTURE_PATH = IMAGES_DIR
			+ "onlinequestion.png";
	/** 在线问诊照片的路径 */
	public static final String REPORT_PICTURE_PATH = IMAGES_DIR + "report.png";
	/** 用来标识请求照相功能的activity */
	public static final int CAMERA_WITH_DATA = 3021;
	/** 用来标识请求gallery的activity */
	public static final int PHOTO_PICKED_WITH_DATA = 3022;
	/** 用来标识请求裁剪图片的activity */
	public static final int CROP_PICKED_WITH_DATA = 3023;
	/** 瑞慈体检天猫商城 */
	// public static final String RUICI_TMALL_URl = "http://www.hao123.com/";
	public static final String RUICI_TMALL_URl = "https://ruicitijian.m.tmall.com/";
	/* 体检预约 */
	public static final String RESERVATION = ROOT_URl
			+ "/Go/?url=/Html/Reservation/index.html";
	/* 报告查询 */
	public static final String REPORTQUERY = ROOT_URl
			+ "/Go/?url=/Html/ReportQuery/ReportValidator.html";
	/* 约名医 */
	public static final String FAMOUSDOCTOR = ROOT_URl
			+ "/Go/?url=/Html/MultiResv/FamousDoctor/List.html";
	/* 家庭医生 */
	public static final String FAMILYDOCTOR = ROOT_URl
			+ "/Go/?url=/Html/MultiResv/FamilyDoctor/List.html";
	/* 私人医生 */
	public static final String PRIVATEDOCTOR = ROOT_URl
			+ "/Go/?url=/Html/MultiResv/PrivateDoctor/List.html";
	/* 运动康复 */
	public static final String SPORTS = ROOT_URl
			+ "/Go/?url=/Html/MultiResv/Sports/List.html";
	/* 我的预约 */
	public static final String RESERVATIONS = ROOT_URl
			+ "/Go/?url=/Html/PersonalCenter/reservations.html";
	/* 健康档案 */
	public static final String HEALTHARCHIVE = ROOT_URl
			+ "/Go/?url=/Html/PersonalCenter/healthArchive.html";

	/**
	 * 下午4:06:35 TODO 关于我们
	 */
	public static final String ABOUTUS = ROOT_URl+"/Html/about_us.html";
	/** 获取体检报告 */
	public static final String CHOICE_REPORT_URl = ROOT_URl
			+ "/api/AppClient/ReportList";
	/** 报告详情，报告列表页面 */
	public static final String REPORT_LIST = ROOT_URl
			+ "/Go/?url=/Html/ReportQuery/ReportQueryList.html";
	/** 聊天请求 */
	public static final String STARTCHAT_URl = ROOT_URl
			+ "/api/AppClient/StartChat";
	/** 结束咨询 */
	public static final String CLOSESUBJECT_URL = ROOT_URl
			+ "/api/Reception/CloseSubject";
	/** 获取医生信息 */
	public static final String GETDOC_URL = ROOT_URl + "/api/Batch/DoctorInfo";
	/** 获取群组信息 */
	public static final String GETUSERGROUPS_URL = ROOT_URl
			+ "/api/group/GetUserGroups";
	/** 获取群组聊天用户信息 */
	public static final String GETUSERFACE_URL = ROOT_URl
			+ "/api/BackendApi/GetUserFace";
	/** 再次聊天 */
	public static final String CHATAGAIN_URL = ROOT_URl
			+ "/api/AppClient/ChatAgain";
	/** 智能导检机构房间编号和科室的关系表 */
	public static final String IAINTELGETDATA = ROOT_URl
			+ "/APIQueuingSystem/GetData";
	/*** 智能导检机构实时刷新数据。 */
	public static final String IAINTELORGINFO = ROOT_URl
			+ "/APIAccount/GetOrganizationInfo";
	/*** 获取在线subjectId 在线状态 */

	public static final String SUBJECTSTATUS = ROOT_URl + "/api/Subject/Status";
	/** 获取SubjectInfo */
	public static final String SUBJECTINFO = ROOT_URl
			+ "/api/AppClient/GetSubjectInfo";

	/*** 获取banner */
	public static final String BANNER = ROOT_URl + "/api/banner";

	/*** 友盟自定义统计 */
	/* 新注册用户数,0 */
	public static final String UMENG_EVENT_1 = "event_1";
	/* ,体检预约-体检登录,0 */
	public static final String UMENG_EVENT_2 = "event_2";
	/* ,体检预约-选择套餐及内增项,0 */
	public static final String UMENG_EVENT_3 = "event_3";
	/* ,体检预约-选择机构,0 */
	public static final String UMENG_EVENT_4 = "event_4";
	/* ,体检预约-选择日期,0 */
	public static final String UMENG_EVENT_5 = "event_5";
	/* ,智能导检,0 */
	public static final String UMENG_EVENT_6 = "event_6";
	/* ,报告查询,0 */
	public static final String UMENG_EVENT_7 = "event_7";
	/* ,添加其他体检报告,0 */
	public static final String UMENG_EVENT_8 = "event_8";
	/* ,报告解读-评价,0 */
	public static final String UMENG_EVENT_9 = "event_9";
	/* ,报告解读-再次咨询,0 */
	public static final String UMENG_EVENT_10 = "event_10";
	/* ,在线问诊-结束问诊,0 */
	public static final String UMENG_EVENT_11 = "event_11";
	/* ,在线问诊-评价,0 */
	public static final String UMENG_EVENT_12 = "event_12";
	/* ,在线问诊-添加我关心的人,0 */
	public static final String UMENG_EVENT_13 = "event_13";
	/* ,在线问诊-再次咨询,0 */
	public static final String UMENG_EVENT_14 = "event_14";
	/* ,运动康复-下单,0 */
	public static final String UMENG_EVENT_15 = "event_15";
	/* ,家庭医生-下单,0 */
	public static final String UMENG_EVENT_16 = "event_16";
	/* ,私人医生-首页,0 */
	public static final String UMENG_EVENT_17 = "event_17";
	/* ,私人医生-意向单,0 */
	public static final String UMENG_EVENT_18 = "event_18";
	/* ,在线商城,0 */
	public static final String UMENG_EVENT_19 = "event_19";
	/* 个人中心-体检报告解读,0 */
	public static final String UMENG_EVENT_20 = "event_20";

	/* 报告解读首页,0 */
	public static final String UMENG_EVENT_23 = "event_23";

	/* 活跃用户数,0 */
	public static final String UMENG_EVENT_24 = "event_24";

}
