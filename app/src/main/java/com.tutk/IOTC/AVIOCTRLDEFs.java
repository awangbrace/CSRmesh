package com.tutk.IOTC;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;


public class AVIOCTRLDEFs {

	/* AVAPIs IOCTRL Message Type */
	public static final int IOTYPE_USER_IPCAM_START = 0x01FF;
	public static final int IOTYPE_USER_IPCAM_STOP = 0x02FF;

	public static final int IOTYPE_USER_IPCAM_AUDIOSTART = 0x0300;
	public static final int IOTYPE_USER_IPCAM_AUDIOSTOP = 0x0301;

	public static final int IOTYPE_USER_IPCAM_SPEAKERSTART = 0x0350;
	public static final int IOTYPE_USER_IPCAM_SPEAKERSTOP = 0x0351;

	public static final int IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ = 0x0320;
	public static final int IOTYPE_USER_IPCAM_SETSTREAMCTRL_RESP = 0x0321;
	public static final int IOTYPE_USER_IPCAM_GETSTREAMCTRL_REQ = 0x0322;
	public static final int IOTYPE_USER_IPCAM_GETSTREAMCTRL_RESP = 0x0323;

	public static final int IOTYPE_USER_IPCAM_SETMOTIONDETECT_REQ = 0x0324;
	public static final int IOTYPE_USER_IPCAM_SETMOTIONDETECT_RESP = 0x0325;
	public static final int IOTYPE_USER_IPCAM_GETMOTIONDETECT_REQ = 0x0326;
	public static final int IOTYPE_USER_IPCAM_GETMOTIONDETECT_RESP = 0x0327;

	public static final int IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ = 0x0328;
	public static final int IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_RESP = 0x0329;

	public static final int IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ = 0x032A;
	public static final int IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_RESP = 0x032B;

	public static final int IOTYPE_USER_IPCAM_DEVINFO_REQ = 0x0330;
	public static final int IOTYPE_USER_IPCAM_DEVINFO_RESP = 0x0331;

	public static final int IOTYPE_USER_IPCAM_SETPASSWORD_REQ = 0x0332;
	public static final int IOTYPE_USER_IPCAM_SETPASSWORD_RESP = 0x0333;

	public static final int IOTYPE_USER_IPCAM_LISTWIFIAP_REQ = 0x0340;
	public static final int IOTYPE_USER_IPCAM_LISTWIFIAP_RESP = 0x0341;
	public static final int IOTYPE_USER_IPCAM_SETWIFI_REQ = 0x0342;
	public static final int IOTYPE_USER_IPCAM_SETWIFI_RESP = 0x0343;
	public static final int IOTYPE_USER_IPCAM_GETWIFI_REQ = 0x0344;
	public static final int IOTYPE_USER_IPCAM_GETWIFI_RESP = 0x0345;
	public static final int IOTYPE_USER_IPCAM_SETWIFI_REQ_2 = 0x0346;
	public static final int IOTYPE_USER_IPCAM_GETWIFI_RESP_2 = 0x0347;

	public static final int IOTYPE_USER_IPCAM_SETRECORD_REQ = 0x0310;
	public static final int IOTYPE_USER_IPCAM_SETRECORD_RESP = 0x0311;
	public static final int IOTYPE_USER_IPCAM_GETRECORD_REQ = 0x0312;
	public static final int IOTYPE_USER_IPCAM_GETRECORD_RESP = 0x0313;

	public static final int IOTYPE_USER_IPCAM_SETRCD_DURATION_REQ = 0x0314;
	public static final int IOTYPE_USER_IPCAM_SETRCD_DURATION_RESP = 0x0315;
	public static final int IOTYPE_USER_IPCAM_GETRCD_DURATION_REQ = 0x0316;
	public static final int IOTYPE_USER_IPCAM_GETRCD_DURATION_RESP = 0x0317;

	public static final int IOTYPE_USER_IPCAM_LISTEVENT_REQ = 0x0318;
	public static final int IOTYPE_USER_IPCAM_LISTEVENT_RESP = 0x0319;

	public static final int IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL = 0x031A;
	public static final int IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL_RESP = 0x031B;

	public static final int IOTYPE_USER_IPCAM_GET_EVENTCONFIG_REQ = 0x0400;
	public static final int IOTYPE_USER_IPCAM_GET_EVENTCONFIG_RESP = 0x0401;
	public static final int IOTYPE_USER_IPCAM_SET_EVENTCONFIG_REQ = 0x0402;
	public static final int IOTYPE_USER_IPCAM_SET_EVENTCONFIG_RESP = 0x0403;

	public static final int IOTYPE_USER_IPCAM_SET_ENVIRONMENT_REQ = 0x0360;
	public static final int IOTYPE_USER_IPCAM_SET_ENVIRONMENT_RESP = 0x0361;
	public static final int IOTYPE_USER_IPCAM_GET_ENVIRONMENT_REQ = 0x0362;
	public static final int IOTYPE_USER_IPCAM_GET_ENVIRONMENT_RESP = 0x0363;

	public static final int IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ = 0x0370;
	public static final int IOTYPE_USER_IPCAM_SET_VIDEOMODE_RESP = 0x0371;
	public static final int IOTYPE_USER_IPCAM_GET_VIDEOMODE_REQ = 0x0372;
	public static final int IOTYPE_USER_IPCAM_GET_VIDEOMODE_RESP = 0x0373;

	public static final int IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_REQ = 0x380;
	public static final int IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_RESP = 0x381;

	public static final int IOTYPE_USER_IPCAM_PTZ_COMMAND = 0x1001;

	public static final int IOTYPE_USER_IPCAM_EVENT_REPORT = 0x1FFF;

	public static final int IOTYPE_USER_IPCAM_RECEIVE_FIRST_IFRAME = 0x1002; // Send
																				// from
																				// client,
																				// used
																				// to
																				// talk
																				// to
																				// device
																				// that

	public static final int IOTYPE_USER_IPCAM_GET_FLOWINFO_REQ = 0x0390;
	public static final int IOTYPE_USER_IPCAM_GET_FLOWINFO_RESP = 0x0391;
	public static final int IOTYPE_USER_IPCAM_CURRENT_FLOWINFO = 0x0392;

	public static final int IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ = 0x3A0;
	public static final int IOTYPE_USER_IPCAM_GET_TIMEZONE_RESP = 0x3A1;
	public static final int IOTYPE_USER_IPCAM_SET_TIMEZONE_REQ = 0x3B0;
	public static final int IOTYPE_USER_IPCAM_SET_TIMEZONE_RESP = 0x3B1;

	// liou
	public static final int IOTYPE_USER_IPCAM_GETGUARD_REQ = 0x420;
	public static final int IOTYPE_USER_IPCAM_GETGUARD_RESP = 0x421;

	public static final int IOTYPE_USER_IPCAM_SETGUARD_REQ = 0x422;
	public static final int IOTYPE_USER_IPCAM_SETGUARD_RESP = 0x423;

	public static final int IOTYPE_USER_IPCAM_SETMOTIONDETECT2_REQ = 0x42A;
	public static final int IOTYPE_USER_IPCAM_SETMOTIONDETECT2_RESP = 0x42B;

	public static final int IOTYPE_USER_IPCAM_SETSOUNDDET_REQ = 0x424;
	public static final int IOTYPE_USER_IPCAM_SETSOUNDDET_RESP = 0x425;

	public static final int IOTYPE_USER_IPCAM_SETSENSORDEV_REQ = 0x426;
	public static final int IOTYPE_USER_IPCAM_SETSENSORDEV_RESP = 0x427;

	public static final int IOTYPE_USER_IPCAM_GETSENSORDEV_REQ = 0x428;
	public static final int IOTYPE_USER_IPCAM_GETSENSORDEV_RESP = 0x429;

	public static final int IOTYPE_USER_IPCAM_SETALARM_REQ = 0x42E;
	public static final int IOTYPE_USER_IPCAM_SETALARM_RESP = 0x42F;

	public static final int IOTYPE_USER_IPCAM_ADDNEODEV_REQ = 0x436;
	public static final int IOTYPE_USER_IPCAM_ADDNEODEV_RESP = 0x437;

	public static final int IOTYPE_USER_IPCAM_GETNEODEVSTATUS_REQ = 0x438;
	public static final int IOTYPE_USER_IPCAM_GETNEODEVSTATUS_RESP = 0x439;

	public static final int IOTYPE_USER_IPCAM_REMOVENEODEV_REQ = 0x43A;
	public static final int IOTYPE_USER_IPCAM_REMOVENEODEV_RESP = 0x43B;

	public static final int IOTYPE_USER_IPCAM_EDITNEODEV_REQ = 0x448;
	public static final int IOTYPE_USER_IPCAM_EDITNEODEV_RESP = 0x449;

	public static final int IOTYPE_USER_IPCAM_GETNEOSWITCHSTATUS_REQ = 0x43C;
	public static final int IOTYPE_USER_IPCAM_GETNEOSWITCHSTATUS_RESP = 0x43D;

	public static final int IOTYPE_USER_IPCAM_SETNEOSWITCH_REQ = 0x43E;
	public static final int IOTYPE_USER_IPCAM_SETNEOSWITCH_RESP = 0x43F;

	//public static final int IOTYPE_USER_IPCAM_QUERY2WAYNEODEV_REQ = 0x44A;
	//public static final int IOTYPE_USER_IPCAM_QUERY2WAYNEODEV_RESP = 0x44B;

	public static final int IOTYPE_USER_IPCAM_SETPRESET_REQ = 0x440;
	public static final int IOTYPE_USER_IPCAM_SETPRESET_RESP = 0x441;

	public static final int IOTYPE_USER_IPCAM_GETPRESET_REQ = 0x442;
	public static final int IOTYPE_USER_IPCAM_GETPRESET_RESP = 0x443;

	public static final int IOTYPE_USER_IPCAM_CURRTEMPERATURE_REQ = 0x44C;
	public static final int IOTYPE_USER_IPCAM_CURRTEMPERATURE_RESP = 0x44D;
	public static final int IOTYPE_USER_IPCAM_SETALARMRING_REQ = 0x44E;
	public static final int IOTYPE_USER_IPCAM_SETALARMRING_RESP = 0x44F;

	public static final int IOTYPE_USER_IPCAM_GET_NAME_REQ = 0x451;
	public static final int IOTYPE_USER_IPCAM_GET_NAME_RESP = 0x452;
	public static final int IOTYPE_USER_IPCAM_CAMERA_RENAME_REQ = 0x453;
	public static final int IOTYPE_USER_IPCAM_CAMERA_RENAME_RESP = 0x454;

	public static final int IOTYPE_USER_IPCAM_SETIRLIGHT_REQ = 0x455;
	public static final int IOTYPE_USER_IPCAM_SETIRLIGHT_RESP = 0x456;

	public static final int IOTYPE_USER_IPCAM_GETISOPTZOOM_REQ = 0x457;
	public static final int IOTYPE_USER_IPCAM_GETISOPTZOOM_RESP = 0x458;

	public static final int IOTYPE_USER_IPCAM_GETDEVUTCTIME_REQ = 0x459;
	public static final int IOTYPE_USER_IPCAM_GETDEVUTCTIME_RESP = 0x45A;

	public static final int IOTYPE_USER_IPCAM_SETDEVUTCTIME_REQ = 0x45B;
	public static final int IOTYPE_USER_IPCAM_SETDEVUTCTIME_RESP = 0x45C;

	public static final int IOTYPE_USER_IPCAM_GET_NTPCFG_REQ = 0x45D;
	public static final int IOTYPE_USER_IPCAM_GET_NTPCFG_RESP = 0x45E;
	public static final int IOTYPE_USER_IPCAM_SET_NTPCFG_REQ = 0x45F;
	public static final int IOTYPE_USER_IPCAM_SET_NTPCFG_RESP = 0x460;
	
	public static final int IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ_EXT = 0x471;
	public static final int IOTYPE_USER_IPCAM_GET_TIMEZONE_RESP_EXT = 0x472;
	public static final int IOTYPE_USER_IPCAM_SET_TIMEZONE_REQ_EXT = 0x473;
	public static final int IOTYPE_USER_IPCAM_SET_TIMEZONE_RESP_EXT = 0x474;
	
	/* 澹伴煶璇锋眰鍝嶅簲鏍囪瘑 */
	public static final int IOTYPE_USER_IPCAM_SETSOUNDDETECT_REQ = 0x03B2;
	public static final int IOTYPE_USER_IPCAM_SETSOUNDDETECT_RESP	= 0x03B3;
	public static final int IOTYPE_USER_IPCAM_GETSOUNDDETECT_REQ	= 0x03B4;
	public static final int IOTYPE_USER_IPCAM_GETSOUNDDETECT_RESP	= 0x03B5;
	public static final int IOTYPE_USER_IPCAM_GET_Temperature_Humidity_REQ = 0x479;
	public static final int IOTYPE_USER_IPCAM_GET_Temperature_Humidity_RESP = 0x480;
	public static final int IOTYPE_USER_IPCAM_SET_Temperature_Humidity_REQ = 0x481;
	public static final int IOTYPE_USER_IPCAM_SET_Temperature_Humidity_RESP = 0x482;

	/* AVAPIs IOCTRL Event Type */
	public static final int AVIOCTRL_EVENT_ALL = 0x00;
	public static final int AVIOCTRL_EVENT_MOTIONDECT = 0x01;
	public static final int AVIOCTRL_EVENT_VIDEOLOST = 0x02;
	public static final int AVIOCTRL_EVENT_IOALARM = 0x03;
	public static final int AVIOCTRL_EVENT_MOTIONPASS = 0x04;
	public static final int AVIOCTRL_EVENT_VIDEORESUME = 0x05;
	public static final int AVIOCTRL_EVENT_IOALARMPASS = 0x06;
	public static final int AVIOCTRL_EVENT_EXPT_REBOOT = 0x10;
	public static final int AVIOCTRL_EVENT_SDFAULT = 0x11;

	/* AVAPIs IOCTRL Play Record Command */
	public static final int AVIOCTRL_RECORD_PLAY_PAUSE = 0x00;
	public static final int AVIOCTRL_RECORD_PLAY_STOP = 0x01;
	public static final int AVIOCTRL_RECORD_PLAY_STEPFORWARD = 0x02;
	public static final int AVIOCTRL_RECORD_PLAY_STEPBACKWARD = 0x03;
	public static final int AVIOCTRL_RECORD_PLAY_FORWARD = 0x04;
	public static final int AVIOCTRL_RECORD_PLAY_BACKWARD = 0x05;
	public static final int AVIOCTRL_RECORD_PLAY_SEEKTIME = 0x06;
	public static final int AVIOCTRL_RECORD_PLAY_END = 0x07;
	public static final int AVIOCTRL_RECORD_PLAY_START = 0x10;

	// AVIOCTRL PTZ Command Value
	public static final int AVIOCTRL_PTZ_STOP = 0;
	public static final int AVIOCTRL_PTZ_UP = 1;
	public static final int AVIOCTRL_PTZ_DOWN = 2;
	public static final int AVIOCTRL_PTZ_LEFT = 3;
	public static final int AVIOCTRL_PTZ_LEFT_UP = 4;
	public static final int AVIOCTRL_PTZ_LEFT_DOWN = 5;
	public static final int AVIOCTRL_PTZ_RIGHT = 6;
	public static final int AVIOCTRL_PTZ_RIGHT_UP = 7;
	public static final int AVIOCTRL_PTZ_RIGHT_DOWN = 8;
	public static final int AVIOCTRL_PTZ_AUTO = 9;
	public static final int AVIOCTRL_PTZ_SET_POINT = 10;
	public static final int AVIOCTRL_PTZ_CLEAR_POINT = 11;
	public static final int AVIOCTRL_PTZ_GOTO_POINT = 12;
	public static final int AVIOCTRL_PTZ_SET_MODE_START = 13;
	public static final int AVIOCTRL_PTZ_SET_MODE_STOP = 14;
	public static final int AVIOCTRL_PTZ_MODE_RUN = 15;
	public static final int AVIOCTRL_PTZ_MENU_OPEN = 16;
	public static final int AVIOCTRL_PTZ_MENU_EXIT = 17;
	public static final int AVIOCTRL_PTZ_MENU_ENTER = 18;
	public static final int AVIOCTRL_PTZ_FLIP = 19;
	public static final int AVIOCTRL_PTZ_START = 20;

	public static final int AVIOCTRL_LENS_APERTURE_OPEN = 21;
	public static final int AVIOCTRL_LENS_APERTURE_CLOSE = 22;
	public static final int AVIOCTRL_LENS_ZOOM_IN = 23;
	public static final int AVIOCTRL_LENS_ZOOM_OUT = 24;
	public static final int AVIOCTRL_LENS_FOCAL_NEAR = 25;
	public static final int AVIOCTRL_LENS_FOCAL_FAR = 26;

	public static final int AVIOCTRL_AUTO_PAN_SPEED = 27;
	public static final int AVIOCTRL_AUTO_PAN_LIMIT = 28;
	public static final int AVIOCTRL_AUTO_PAN_START = 29;

	public static final int AVIOCTRL_PATTERN_START = 30;
	public static final int AVIOCTRL_PATTERN_STOP = 31;
	public static final int AVIOCTRL_PATTERN_RUN = 32;

	public static final int AVIOCTRL_SET_AUX = 33;
	public static final int AVIOCTRL_CLEAR_AUX = 34;
	public static final int AVIOCTRL_MOTOR_RESET_POSITION = 35;

	/* AVAPIs IOCTRL Quality Type */
	public static final int AVIOCTRL_QUALITY_UNKNOWN = 0x00;
	public static final int AVIOCTRL_QUALITY_MAX = 0x01;
	public static final int AVIOCTRL_QUALITY_HIGH = 0x02;
	public static final int AVIOCTRL_QUALITY_MIDDLE = 0x03;
	public static final int AVIOCTRL_QUALITY_LOW = 0x04;
	public static final int AVIOCTRL_QUALITY_MIN = 0x05;

	/* AVAPIs IOCTRL WiFi Mode */
	public static final int AVIOTC_WIFIAPMODE_ADHOC = 0x00;
	public static final int AVIOTC_WIFIAPMODE_MANAGED = 0x01;

	/* AVAPIs IOCTRL WiFi Enc Type */
	public static final int AVIOTC_WIFIAPENC_INVALID = 0x00;
	public static final int AVIOTC_WIFIAPENC_NONE = 0x01;
	public static final int AVIOTC_WIFIAPENC_WEP = 0x02;
	public static final int AVIOTC_WIFIAPENC_WPA_TKIP = 0x03;
	public static final int AVIOTC_WIFIAPENC_WPA_AES = 0x04;
	public static final int AVIOTC_WIFIAPENC_WPA2_TKIP = 0x05;
	public static final int AVIOTC_WIFIAPENC_WPA2_AES = 0x06;
	public static final int AVIOTC_WIFIAPENC_WPA_PSK_TKIP = 0x07;
	public static final int AVIOTC_WIFIAPENC_WPA_PSK_AES = 0x08;
	public static final int AVIOTC_WIFIAPENC_WPA2_PSK_TKIP = 0x09;
	public static final int AVIOTC_WIFIAPENC_WPA2_PSK_AES = 0x0A;

	/* AVAPIs IOCTRL Recording Type */
	public static final int AVIOTC_RECORDTYPE_OFF = 0x00;
	public static final int AVIOTC_RECORDTYPE_FULLTIME = 0x01;
	public static final int AVIOTC_RECORDTYPE_ALAM = 0x02;
	public static final int AVIOTC_RECORDTYPE_MANUAL = 0x03;

	public static final int AVIOCTRL_ENVIRONMENT_INDOOR_50HZ = 0x00;
	public static final int AVIOCTRL_ENVIRONMENT_INDOOR_60HZ = 0x01;
	public static final int AVIOCTRL_ENVIRONMENT_OUTDOOR = 0x02;
	public static final int AVIOCTRL_ENVIRONMENT_NIGHT = 0x03;

	/* AVIOCTRL VIDEO MODE */
	public static final int AVIOCTRL_VIDEOMODE_NORMAL = 0x00;
	public static final int AVIOCTRL_VIDEOMODE_FLIP = 0x01;
	public static final int AVIOCTRL_VIDEOMODE_MIRROR = 0x02;
	public static final int AVIOCTRL_VIDEOMODE_FLIP_MIRROR = 0x03;

	public static class SFrameInfo {

		short codec_id;
		byte flags;
		byte cam_index;
		byte onlineNum;
		byte[] reserved = new byte[3];
		int reserved2;
		int timestamp;

		public static byte[] parseContent(short codec_id, byte flags,
				byte cam_index, byte online_num, int timestamp) {

			byte[] result = new byte[16];

			byte[] codec = Packet.shortToByteArray_Little(codec_id);
			System.arraycopy(codec, 0, result, 0, 2);

			result[2] = flags;
			result[3] = cam_index;
			result[4] = online_num;

			byte[] time = Packet.intToByteArray_Little(timestamp);
			System.arraycopy(time, 0, result, 12, 4);

			return result;
		}
	}

	public static class SMsgAVIoctrlAVStream {
		int channel = 0; // camera index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public class SMsgAVIoctrlEventConfig {
		long channel; // Camera Index
		byte mail; // enable send email
		byte ftp; // enable ftp upload photo
		byte localIO; // enable local io output
		byte p2pPushMsg; // enable p2p push msg
	}

	public static class SMsgAVIoctrlPtzCmd {
		byte control; // ptz control command
		byte speed; // ptz control speed
		byte point;
		byte limit;
		byte aux;
		byte channel; // camera index
		byte[] reserved = new byte[2];

		public static byte[] parseContent(byte control, byte speed, byte point,
				byte limit, byte aux, byte channel) {
			byte[] result = new byte[8];

			result[0] = control;
			result[1] = speed;
			result[2] = point;
			result[3] = limit;
			result[4] = aux;
			result[5] = channel;

			return result;
		}
	}

	public static class SMsgAVIoctrlSetStreamCtrlReq {
		int channel; // Camera Index
		byte quality; // AVIOCTRL_QUALITY_XXXX
		byte[] reserved = new byte[3];

		public static byte[] parseContent(int channel, byte quality) {

			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			result[4] = quality;

			return result;
		}
	}

	public class SMsgAVIoctrlGetStreamCtrlResp {
		int channel; // Camera Index
		byte quality; // AVIOCTRL_QUALITY_XXXX
		byte[] reserved = new byte[3];
	}

	public class SMsgAVIoctrlSetStreamCtrlResp {
		int result;
		byte[] reserved = new byte[4];
	}

	public static class SMsgAVIoctrlGetStreamCtrlReq {
		int channel; // Camera Index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel) {

			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public static class SMsgAVIoctrlSetMotionDetectReq {
		int channel; // Camera Index
		int sensitivity; /* 0(disbale) ~ 100(MAX) */

		public static byte[] parseContent(int channel, int sensitivity) {

			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			byte[] sen = Packet.intToByteArray_Little(sensitivity);

			System.arraycopy(ch, 0, result, 0, 4);
			System.arraycopy(sen, 0, result, 4, 4);

			return result;
		}
	}

	public class SMsgAVIoctrlSetMotionDetectResp {
		byte result;
		byte[] reserved = new byte[3];
	}

	public static class SMsgAVIoctrlGetMotionDetectReq {
		int channel; // Camera Index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel) {

			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public class SMsgAVIoctrlGetMotionDetectResp {
		int channel; // Camera Index
		int sensitivity; /* 0(disbale) ~ 100(MAX) */
	}

	public static class SMsgAVIoctrlDeviceInfoReq {

		static byte[] reserved = new byte[4];;

		public static byte[] parseContent() {
			return reserved;
		}
	}

	public class SMsgAVIoctrlDeviceInfoResp {
		byte[] model = new byte[16];
		byte[] vendor = new byte[16];
		int version;
		int channel;
		int total; /* MByte */
		int free; /* MByte */
		byte[] reserved = new byte[8];
	}

	public static class SMsgAVIoctrlSetPasswdReq {
		byte[] oldPasswd = new byte[32];
		byte[] newPasswd = new byte[32];

		public static byte[] parseContent(String oldPwd, String newPwd) {

			byte[] oldpwd = oldPwd.getBytes();
			byte[] newpwd = newPwd.getBytes();
			byte[] result = new byte[64];

			System.arraycopy(oldpwd, 0, result, 0, oldpwd.length);
			System.arraycopy(newpwd, 0, result, 32, newpwd.length);

			return result;
		}
	}

	public class SMsgAVIoctrlSetPasswdResp {
		byte result;
		byte[] reserved = new byte[3];
	}

	public static class SMsgAVIoctrlListWifiApReq {

		static byte[] reserved = new byte[4];

		public static byte[] parseContent() {

			return reserved;
		}
	}

	public static class SWifiAp {
		public byte[] ssid = new byte[32];
		public byte mode;
		public byte enctype;
		public byte signal;
		public byte status;

		public static int getTotalSize() {
			return 36;
		}

		public SWifiAp(byte[] data) {

			System.arraycopy(data, 1, ssid, 0, data.length);
			mode = data[32];
			enctype = data[33];
			signal = data[34];
			status = data[35];
		}

		public SWifiAp(byte[] bytsSSID, byte bytMode, byte bytEnctype,
				byte bytSignal, byte bytStatus) {

			System.arraycopy(bytsSSID, 0, ssid, 0, bytsSSID.length);
			mode = bytMode;
			enctype = bytEnctype;
			signal = bytSignal;
			status = bytStatus;
		}
	}

	public class SMsgAVIoctrlListWifiApResp {
		int number; // MAX: 1024/36= 28
		SWifiAp stWifiAp;
	}

	public static class SMsgAVIoctrlSetWifiReq {
		byte[] ssid = new byte[32];
		byte[] password = new byte[32];
		byte mode;
		byte enctype;
		byte[] reserved = new byte[10];

		public static byte[] parseContent(byte[] ssid, byte[] password,
				byte mode, byte enctype) {

			byte[] result = new byte[76];

			System.arraycopy(ssid, 0, result, 0, ssid.length);
			System.arraycopy(password, 0, result, 32, password.length);
			result[64] = mode;
			result[65] = enctype;

			return result;
		}
	}

	public class SMsgAVIoctrlSetWifiResp {
		byte result;
		byte[] reserved = new byte[3];
	}

	public static class SMsgAVIoctrlGetWifiReq {

		static byte[] reserved = new byte[4];

		public static byte[] parseContent() {
			return reserved;
		}
	}

	public class SMsgAVIoctrlGetWifiResp {
		byte[] ssid = new byte[32];
		byte[] password = new byte[32];
		byte mode;
		byte enctype;
		byte signal;
		byte status;
	}

	public static class SMsgAVIoctrlSetRecordReq {
		int channel; // Camera Index
		int recordType;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel, int recordType) {

			byte[] result = new byte[12];
			byte[] ch = Packet.intToByteArray_Little(channel);
			byte[] type = Packet.intToByteArray_Little(recordType);

			System.arraycopy(ch, 0, result, 0, 4);
			System.arraycopy(type, 0, result, 4, 4);

			return result;
		}
	}

	public class SMsgAVIoctrlSetRecordResp {
		byte result;
		byte[] reserved = new byte[3];
	}

	public static class SMsgAVIoctrlGetRecordReq {
		int channel; // Camera Index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel) {

			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public class SMsgAVIoctrlGetRecordResp {
		int channel; // Camera Index
		int recordType;
	}

	public class SMsgAVIoctrlSetRcdDurationReq {
		int channel; // Camera Index
		int presecond;
		int durasecond;
	}

	public class SMsgAVIoctrlSetRcdDurationResp {
		byte result;
		byte[] reserved = new byte[3];
	}

	public class SMsgAVIoctrlGetRcdDurationReq {
		int channel; // Camera Index
		byte[] reserved = new byte[4];
	}

	public class SMsgAVIoctrlGetRcdDurationResp {
		int channel; // Camera Index
		int presecond;
		int durasecond;
	}

	public static class STimeDay {

		private byte[] mBuf;
		public short year;
		public byte month;
		public byte day;
		public byte wday;
		public byte hour;
		public byte minute;
		public byte second;

		public STimeDay(byte[] data) {

			mBuf = new byte[8];
			System.arraycopy(data, 0, mBuf, 0, 8);

			year = Packet.byteArrayToShort_Little(data, 0);
			month = data[2];
			day = data[3];
			wday = data[4];
			hour = data[5];
			minute = data[6];
			second = data[7];
		}

		public long getTimeInMillis() {

			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			cal.set(year, month - 1, day, hour, minute, second);

			return cal.getTimeInMillis();
		}

		public String getLocalTime() {

			Calendar calendar = Calendar.getInstance(TimeZone
					.getTimeZone("gmt"));
			calendar.setTimeInMillis(getTimeInMillis());
			// calendar.add(Calendar.MONTH, -1);

			SimpleDateFormat dateFormat = new SimpleDateFormat();
			dateFormat.setTimeZone(TimeZone.getDefault());

			return dateFormat.format(calendar.getTime());
		}

		public byte[] toByteArray() {
			return mBuf;
		}

		public static byte[] parseContent(int year, int month, int day,
				int wday, int hour, int minute, int second) {

			byte[] result = new byte[8];

			byte[] y = Packet.shortToByteArray_Little((short) year);
			System.arraycopy(y, 0, result, 0, 2);

			result[2] = (byte) month;
			result[3] = (byte) day;
			result[4] = (byte) wday;
			result[5] = (byte) hour;
			result[6] = (byte) minute;
			result[7] = (byte) second;

			return result;
		}
	}

	public static class SMsgAVIoctrlListEventReq {

		int channel; // Camera Index
		byte[] startutctime = new byte[8];
		byte[] endutctime = new byte[8];
		byte event;
		byte status;
		byte[] reversed = new byte[2];

		public static byte[] parseConent(int channel, long startutctime,
				long endutctime, byte event, byte status) {

			Calendar startCal = Calendar.getInstance(TimeZone
					.getTimeZone("gmt"));
			Calendar stopCal = Calendar
					.getInstance(TimeZone.getTimeZone("gmt"));
			startCal.setTimeInMillis(startutctime);
			stopCal.setTimeInMillis(endutctime);

			System.out.println("search from " + startCal.get(Calendar.YEAR)
					+ "/" + startCal.get(Calendar.MONTH) + "/"
					+ startCal.get(Calendar.DAY_OF_MONTH) + " "
					+ startCal.get(Calendar.HOUR_OF_DAY) + ":"
					+ startCal.get(Calendar.MINUTE) + ":"
					+ startCal.get(Calendar.SECOND));
			System.out.println("       to   " + stopCal.get(Calendar.YEAR)
					+ "/" + stopCal.get(Calendar.MONTH) + "/"
					+ stopCal.get(Calendar.DAY_OF_MONTH) + " "
					+ stopCal.get(Calendar.HOUR_OF_DAY) + ":"
					+ stopCal.get(Calendar.MINUTE) + ":"
					+ stopCal.get(Calendar.SECOND));

			byte[] result = new byte[24];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			byte[] start = STimeDay.parseContent(startCal.get(Calendar.YEAR),
					startCal.get(Calendar.MONTH) + 1,
					startCal.get(Calendar.DAY_OF_MONTH),
					startCal.get(Calendar.DAY_OF_WEEK),
					startCal.get(Calendar.HOUR_OF_DAY),
					startCal.get(Calendar.MINUTE), 0);
			System.arraycopy(start, 0, result, 4, 8);

			byte[] stop = STimeDay.parseContent(stopCal.get(Calendar.YEAR),
					stopCal.get(Calendar.MONTH) + 1,
					stopCal.get(Calendar.DAY_OF_MONTH),
					stopCal.get(Calendar.DAY_OF_WEEK),
					stopCal.get(Calendar.HOUR_OF_DAY),
					stopCal.get(Calendar.MINUTE), 0);
			System.arraycopy(stop, 0, result, 12, 8);

			result[20] = event;
			result[21] = status;

			return result;
		}
	}

	public static class SAvEvent {
		byte[] utctime = new byte[8];
		byte event;
		byte status;
		byte[] reserved = new byte[2];

		public static int getTotalSize() {
			return 12;
		}
	}

	public class SMsgAVIoctrlListEventResp {
		int channel; // Camera Index
		int total;
		byte index;
		byte endflag;
		byte count;
		byte reserved;
		SAvEvent stEvent;
	}

	public static class SMsgAVIoctrlPlayRecord {
		int channel; // Camera Index
		int command; // play record command
		int Param;
		byte[] stTimeDay = new byte[8];
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel, int command, int param,
				long time) {

			byte[] result = new byte[24];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			byte[] cmd = Packet.intToByteArray_Little(command);
			System.arraycopy(cmd, 0, result, 4, 4);

			byte[] p = Packet.intToByteArray_Little(param);
			System.arraycopy(p, 0, result, 8, 4);

			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			cal.setTimeInMillis(time);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			cal.add(Calendar.DATE, 1);
			byte[] timedata = STimeDay.parseContent(cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
					cal.get(Calendar.DAY_OF_WEEK),
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
					cal.get(Calendar.SECOND));
			System.arraycopy(timedata, 0, result, 12, 8);

			return result;
		}

		public static byte[] parseContent(int channel, int command, int param,
				byte[] time) {

			byte[] result = new byte[24];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			byte[] cmd = Packet.intToByteArray_Little(command);
			System.arraycopy(cmd, 0, result, 4, 4);

			byte[] p = Packet.intToByteArray_Little(param);
			System.arraycopy(p, 0, result, 8, 4);

			System.arraycopy(time, 0, result, 12, 8);

			return result;
		}
	}

	// only for play record start command
	public class SMsgAVIoctrlPlayRecordResp {
		int channel;
		int result;
		byte[] reserved = new byte[4];
	} // only for play record start command

	public class SMsgAVIoctrlEvent {
		STimeDay stTime; // 8 bytes
		int channel; // Camera Index
		int event; // Event Type
		byte[] reserved = new byte[4];
	}

	public static class SMsgAVIoctrlSetVideoModeReq {
		int channel; // Camera Index
		byte mode; // Video mode
		byte[] reserved = new byte[3];

		public static byte[] parseContent(int channel, byte mode) {
			byte[] result = new byte[8];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			result[4] = mode;

			return result;
		}
	}

	public class SMsgAVIoctrlSetVideoModeResp {
		int channel; // Camera Index
		byte result; // 1 - succeed, 0 - failed
		byte[] reserved = new byte[3];
	}

	public static class SMsgAVIoctrlGetVideoModeReq {
		int channel; // Camera Index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel) {
			byte[] result = new byte[8];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public class SMsgAVIoctrlGetVideoModeResp {
		int channel; // Camera Index
		byte mode; // Video Mode
		byte[] reserved = new byte[3];
	}

	public static class SMsgAVIoctrlSetEnvironmentReq {
		int channel; // Camera Index
		byte mode; // Environment mode
		byte[] reserved = new byte[3];

		public static byte[] parseContent(int channel, byte mode) {

			byte[] result = new byte[8];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			result[4] = mode;

			return result;
		}
	}

	public class SMsgAVIoctrlSetEnvironmentResp {

		int channel; // Camera Index
		byte result; // 1 - succeed, 0 - failed
		byte[] reserved = new byte[3];
	}

	public static class SMsgAVIoctrlGetEnvironmentReq {
		int channel; // Camera Index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel) {

			byte[] result = new byte[8];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public class SMsgAVIoctrlGetEnvironmentResp {
		int channel; // Camera Index
		byte mode; // Environment Mode
		byte[] reserved = new byte[3];
	}

	public static class SMsgAVIoctrlFormatExtStorageReq {

		int storage; // Storage index (ex. sdcard slot = 0, internal flash = 1,
						// ...)
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int storage) {

			byte[] result = new byte[8];

			byte[] ch = Packet.intToByteArray_Little(storage);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public class SMsgAVIoctrlFormatExtStorageResp {

		int storage; // Storage index
		byte result; // 0: success;
						// -1: format command is not supported.
						// otherwise: failed.

		byte[] reserved = new byte[3];
	}

	public static class SStreamDef {

		public int index; // the stream index of camera
		public int channel; // the channel index used in AVAPIs

		public SStreamDef(byte[] data) {

			index = Packet.byteArrayToShort_Little(data, 0);
			channel = Packet.byteArrayToShort_Little(data, 2);
		}

		public String toString() {
			return ("CH" + String.valueOf(index + 1));
		}
	}

	public static class SMsgAVIoctrlGetSupportStreamReq {

		public static byte[] parseContent() {

			return new byte[4];
		}

		public static int getContentSize() {
			return 4;
		}
	}

	public class SMsgAVIoctrlGetSupportStreamResp {

		public SStreamDef mStreamDef[];
		public long number;
	}

	public static class SMsgAVIoctrlGetAudioOutFormatReq {

		public static byte[] parseContent() {
			return new byte[8];
		}
	}

	public class SMsgAVIoctrlGetAudioOutFormatResp {
		public int channel;
		public int format;
	}

	// IOTYPE_USER_IPCAM_GET_FLOWINFO_REQ = 0x390
	public static class SMsgAVIoctrlGetFlowInfoReq {
		public int channel;
		public int collect_interval;

	}

	// IOTYPE_USER_IPCAM_GET_FLOWINFO_RESP = 0x391
	public static class SMsgAVIoctrlGetFlowInfoResp {
		public int channel;
		public int collect_interval;

		public static byte[] parseContent(int channel, int collect_interval) {

			byte[] result = new byte[8];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			byte[] col = Packet.intToByteArray_Little(collect_interval);
			System.arraycopy(col, 0, result, 4, 4);

			return result;
		}
	}

	// IOTYPE_USER_IPCAM_CURRENT_FLOWINFO = 0x392
	public static class SMsgAVIoctrlCurrentFlowInfo {
		public int channel;
		public int total_frame_count;
		public int lost_incomplete_frame_count;
		public int total_expected_frame_size;
		public int total_actual_frame_size;
		public int elapse_time_ms;

		public static byte[] parseContent(int channel, int total_frame_count,
				int lost_incomplete_frame_count, int total_expected_frame_size,
				int total_actual_frame_size, int elapse_time_ms) {

			byte[] result = new byte[32];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			byte[] total_frame = Packet
					.intToByteArray_Little(total_frame_count);
			System.arraycopy(total_frame, 0, result, 4, 4);

			byte[] lost_incomplete = Packet
					.intToByteArray_Little(lost_incomplete_frame_count);
			System.arraycopy(lost_incomplete, 0, result, 8, 4);

			byte[] total_expected = Packet
					.intToByteArray_Little(total_expected_frame_size);
			System.arraycopy(total_expected, 0, result, 12, 4);

			byte[] total_actual = Packet
					.intToByteArray_Little(total_actual_frame_size);
			System.arraycopy(total_actual, 0, result, 16, 4);

			byte[] elapse_time = Packet.intToByteArray_Little(elapse_time_ms);
			System.arraycopy(elapse_time, 0, result, 20, 4);

			return result;
		}
	}

	/*
	 * IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ = 0x3A0
	 * IOTYPE_USER_IPCAM_GET_TIMEZONE_RESP = 0x3A1
	 * IOTYPE_USER_IPCAM_SET_TIMEZONE_REQ = 0x3B0
	 * IOTYPE_USER_IPCAM_SET_TIMEZONE_RESP = 0x3B1
	 */
	public static class SMsgAVIoctrlTimeZone {
		public int cbSize;
		public int nIsSupportTimeZone;
		public int nGMTDiff;
		public byte[] szTimeZoneString = new byte[256];
		public long  local_utc_time;
		public int dst_on; //0 off 1 on

		public static byte[] parseContent() {

			return new byte[12 + 256 + 4 + 4];
		}

		public static byte[] parseContent(int cbSize, int nIsSupportTimeZone,
			int nGMTDiff, byte[] szTimeZoneString,long local_utc_time,int dst_on) {

			byte[] result = new byte[12 + 256 + 4 + 4 ];

			byte[] size = Packet.intToByteArray_Little(cbSize);
			System.arraycopy(size, 0, result, 0, 4);

			byte[] isSupportTimeZone = Packet
					.intToByteArray_Little(nIsSupportTimeZone);
			System.arraycopy(isSupportTimeZone, 0, result, 4, 4);

			byte[] GMTDiff = Packet.intToByteArray_Little(nGMTDiff);
			System.arraycopy(GMTDiff, 0, result, 8, 4);

			System.arraycopy(szTimeZoneString, 0, result, 12,
					szTimeZoneString.length);
			
			byte[] time = Packet.longToByteArray_Little(local_utc_time);
			System.arraycopy(time, 0, result,12+256,4);
			
			byte[] b_dst_on = Packet.intToByteArray_Little(dst_on);
			System.arraycopy(b_dst_on, 0, result, 12+256+4, 4);
			
			return result;
		}
	}

	public static class SMsgAVIoctrlReceiveFirstIFrame {
		int channel; // Camera Index
		int recordType;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel, int recordType) {

			byte[] result = new byte[12];
			byte[] ch = Packet.intToByteArray_Little(channel);
			byte[] type = Packet.intToByteArray_Little(recordType);

			System.arraycopy(ch, 0, result, 0, 4);
			System.arraycopy(type, 0, result, 4, 4);

			return result;
		}
	}

	// IOTYPE_USER_IPCAM_GETGUARD_REQ = 0x420;
	// IOTYPE_USER_IPCAM_GETGUARD_RESP = 0x421;

	public static class SMsgAVIoctrlGetGuardReq {
		int channel; // Camera Index

		public static byte[] parseContent(int channel) {
			byte[] result = new byte[4];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public static class SNeoDevStore {
		byte[] szDevID = new byte[4];
		byte[] szDevName = new byte[24];
		int nIndex;
		int nCurTemp;
		int nLowTemp;
		int nHiTemp;
		byte[] result = new byte[44];

		public SNeoDevStore(byte[] devID, byte[] devName, int index,
				int curTemp, int lowTemp, int hiTemp) {
			// System.arraycopy(devID, 0, szDevID, 0, devID.length);
			// System.arraycopy(devName, 0, szDevName, 0, devName.length);

			System.arraycopy(devID, 0, result, 0, devID.length);
			System.arraycopy(devName, 0, result, 4, devName.length);
			byte[] bIndex = Packet.intToByteArray_Little(index);
			System.arraycopy(bIndex, 0, result, 28, bIndex.length);
			byte[] bCurTemp = Packet.intToByteArray_Little(curTemp);
			System.arraycopy(bCurTemp, 0, result, 32, bCurTemp.length);
			byte[] bLowTemp = Packet.intToByteArray_Little(lowTemp);
			System.arraycopy(bLowTemp, 0, result, 36, bLowTemp.length);
			byte[] bHiTemp = Packet.intToByteArray_Little(hiTemp);
			System.arraycopy(bHiTemp, 0, result, 40, bHiTemp.length);

		}
		// public static byte[] parseContent(byte[] devID,byte[] devName) {
		// byte[] result = new byte[4+24];
		//
		// System.arraycopy(devID, 0, result, 0, 4);
		// System.arraycopy(devName, 0, result, 4, 24);
		//
		// return result;
		// }
	}

	public static class SSensorStatus {

		public int nPaired; // 閿熺瓔锟斤拷閿熸枻鎷风灍锟斤拷锟斤拷閿熺瓔锟斤拷濡わ拷閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷
		public int nCamPreset; // Camera閿熺瓔锟斤拷濡わ拷閿熺瓔锟斤拷濮ｏ拷閿熺瓔锟斤拷閿熸枻鎷烽敓鏂ゆ嫹鐬燂拷閿熸枻鎷峰锟介敓鏂ゆ嫹鐬燂拷閿熸枻鎷烽敓锟�~16(0閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷閿熸枻鎷风灍锟介敓鏂ゆ嫹锟斤拷锟介敓绛嬶拷锟藉В锟�閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷閿熻棄锟斤拷閿燂拷
		public int nLinkOutlet; // 閿熺瓔锟斤拷閿熻姤锟斤拷鐬板府鎷凤拷锟藉搫锟介潻鎷凤拷锟斤拷鐣涳拷锟斤拷锟�閿熺瓔锟斤拷閿熺晫锟斤拷鐢猴拷锟斤拷褝鎷风灍锟介敓锟�閿熺瓔锟斤拷閿熸枻鎷峰锟介敓鏂ゆ嫹鐬燂拷閿熸枻鎷烽敓锟�
								// 0~4(0閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷閿熸枻鎷风灍锟介敓鏂ゆ嫹锟斤拷锟介敓绛嬶拷锟藉В锟�閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷缃瑰府鎷风灍锟介敓鏂ゆ嫹鏉岋拷1~4)
		public int nActivated; // 閿熺瓔锟斤拷濡わ拷閿熺瓔锟斤拷閿熺晫锟斤拷鐢猴拷锟斤拷褝鎷风灍锟介敓鏂ゆ嫹鐬燂拷閿熸枻鎷凤拷锟斤拷閿熸枻鎷烽敓锟絆N=1, OFF=0
		public SNeoDevStore devID; // 閿熺瓔锟斤拷锟斤拷锟介敓绛嬶拷锟介敓浠嬶拷锟界緛锟斤拷锟斤拷T(閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷閿燂拷W閿熺瓔锟斤拷锟斤拷浠嬶拷鑳拷锟介敓鏂ゆ嫹閿燂拷

		public SSensorStatus(int paired, int camPreset, int linkOutlet,
				int activated, SNeoDevStore id) {

			nPaired = paired;
			nCamPreset = camPreset;
			nLinkOutlet = linkOutlet;
			nActivated = activated;
			devID = id;
		}
		// public SSensorStatus(byte[] data) {
		//
		// byte[] id = new byte[4];
		// nPaired = Packet.byteArrayToShort_Little(data, 0);
		// nCamPreset = Packet.byteArrayToShort_Little(data, 4);
		// nLinkOutlet = Packet.byteArrayToShort_Little(data, 8);
		// nActivated = Packet.byteArrayToShort_Little(data, 12);
		// System.arraycopy(data, 16, id, 0, 4);
		//
		//
		// }
		// public byte[] parseContent(int paired, int camPreset, int linkOutlet,
		// int activated,SNeoDevID id ) {
		//
		// byte[] result = new byte[4+4+4+4+4+24];
		//
		// byte[] nPaired = Packet.intToByteArray_Little(paired);
		// System.arraycopy(nPaired, 0, result, 0, 4);
		//
		// byte[] nCamPreset = Packet.intToByteArray_Little(camPreset);
		// System.arraycopy(nCamPreset, 0, result, 4, 4);
		//
		// byte[] nLinkOutlet = Packet.intToByteArray_Little(linkOutlet);
		// System.arraycopy(nLinkOutlet, 0, result, 8, 4);
		//
		// byte[] nActivated = Packet.intToByteArray_Little(activated);
		// System.arraycopy(nActivated, 0, result, 12, 4);
		//
		// System.arraycopy(id.szDevID, 0, result, 16, 4);
		// System.arraycopy(id.szDevName, 0, result, 20, 24);
		//
		// return result;
		// }
	}

	public static class SEMailAddress {
		public byte[] szAddr = null;

		public SEMailAddress(byte[] data) {
			szAddr = new byte[data.length];
			System.arraycopy(data, 0, szAddr, 0, data.length);

		}
	}

	public class SMsgAVIoctrlGetGuardResp {
		int channel; // AvServer Index
		int nGuard; // ON=1,OFF=0
		// byte[] reserved = new byte[3];
		// --------
		int nMotionDetect; // 0 (Disabled) ~ 75(MAX):
		// Index in App. sensitivity value
		// 0 () 0
		// 1 () 25
		// 2 () 50
		// 3 () 75
		SSensorStatus sMotionInfo; // /
		int nSoundDetect; // =0
							// =1
							// =2
							// =3
		SSensorStatus sSoundInfo; //
		// ----锟斤拷锟芥稊鈺嬫嫹閿熺晫锟斤拷锟斤拷锟介敓鐣岋拷锟界綗锟斤拷锟斤拷鐟楄儻闊╋拷锟界鎷�--
		int nAlarm; // 閿熻姤锟斤拷锟斤拷锟介敓钘夛拷闈╂嫹閿熸枻鎷稯N=1, OFF=0
		int nSendEmail; // // 閿熷�锟斤拷鏉堬綇鎷风灠宸拷锟�ON=1, OFF=0
		SEMailAddress[] arrContact = new SEMailAddress[4]; // 閿熻棄锟铰よ崋锟斤拷锟介敓锟�?		int nAlarmSDRec; // 閿熻姤锟斤拷锟斤拷锟絊D閿熻В锟藉尅鎷烽敓鏂ゆ嫹ON=1, OFF=0
		int nPushNotify; // 锟斤拷锟斤拷锟斤拷缃达拷閿熻棄锟介潻鎷烽敓鏂わ拷锟介敓锟�ON=1, OFF=0
	}

	// IOTYPE_USER_IPCAM_SETGUARD_REQ = 0x422;
	// IOTYPE_USER_IPCAM_SETGUARD_RESP = 0x423;
	public static class SMsgAVIoctrlSetGuardReq {
		int channel; // AvServer Index
		int nGuard; // 锟斤拷甯拷锝忥拷鏌ワ拷鍖★拷锟界�?锟�ON=1, OFF=0
		// ----锟斤拷锟芥稊鈺嬫嫹閿熺晫锟斤拷锟斤拷锟介敓鎴掞拷锟界綗锟斤拷锟斤拷鐟楄儻闊╋拷锟界鎷�--
		int nMotionDetect; // // 锟斤拷锟芥锟介敓鏂ゆ嫹锟斤拷锟斤拷锟斤�?0 (Disabled) ~ 75(MAX):
		// Index in App. sensitivity value
		// 0 () 0
		// 1 () 25
		// 2 () 50
		// 3 () 75
		SSensorStatus sInfo1; // /
		int nSoundDetect; // =0
							// =1
							// =2
							// =3
		SSensorStatus sInfo2; //
		// -------
		int nAlarm; // ON=1,OFF=0
		int nSendEmail; // ON=1,OFF=0
		SEMailAddress[] arrContact = new SEMailAddress[4];
		int nAlarmSDRec; // ON=1,OFF=0
		int nPushNotify; // ON=1,OFF=0
		int nPushPicture;

		public static byte[] parseContent(int channel, int guard,
				int motionDetect, SSensorStatus sensorStatus, int soundDetect,
				SSensorStatus sensorStatus2, int alarm, int sendEmail,
				SEMailAddress[] mailAddress, int alarmSDRec, int pushNotify, int pushPicture) {

			byte[] result = new byte[428];

			byte[] ch = Packet.intToByteArray_Little(channel);
			byte[] nGuard = Packet.intToByteArray_Little(guard);
			byte[] bMotionDetect = Packet.intToByteArray_Little(motionDetect);
			byte[] paired = Packet.intToByteArray_Little(sensorStatus.nPaired);
			byte[] camPreset = Packet
					.intToByteArray_Little(sensorStatus.nCamPreset);
			byte[] linkOutlet = Packet
					.intToByteArray_Little(sensorStatus.nLinkOutlet);
			byte[] activated = Packet
					.intToByteArray_Little(sensorStatus.nActivated);
			byte[] bSoundDetect = Packet.intToByteArray_Little(soundDetect);
			byte[] paired2 = Packet
					.intToByteArray_Little(sensorStatus2.nPaired);
			byte[] camPreset2 = Packet
					.intToByteArray_Little(sensorStatus2.nCamPreset);
			byte[] linkOutlet2 = Packet
					.intToByteArray_Little(sensorStatus2.nLinkOutlet);
			byte[] activated2 = Packet
					.intToByteArray_Little(sensorStatus2.nActivated);
			byte[] nAlarm = Packet.intToByteArray_Little(alarm);
			byte[] nSendEmail = Packet.intToByteArray_Little(sendEmail);
			byte[] nAlarmSDRec = Packet.intToByteArray_Little(alarmSDRec);
			byte[] nPushNotify = Packet.intToByteArray_Little(pushNotify);
			byte[] nPushPicture = Packet.intToByteArray_Little(pushPicture);

			System.arraycopy(ch, 0, result, 0, 4);
			System.arraycopy(nGuard, 0, result, 4, 4);
			System.arraycopy(bMotionDetect, 0, result, 8, 4);
			System.arraycopy(paired, 0, result, 12, 4);
			System.arraycopy(camPreset, 0, result, 16, 4);
			System.arraycopy(linkOutlet, 0, result, 20, 4);
			System.arraycopy(activated, 0, result, 24, 4);
			System.arraycopy(sensorStatus.devID.result, 0, result, 28, 44);
			System.arraycopy(bSoundDetect, 0, result, 72, 4);
			System.arraycopy(paired2, 0, result, 76, 4);
			System.arraycopy(camPreset2, 0, result, 80, 4);
			System.arraycopy(linkOutlet2, 0, result, 84, 4);
			System.arraycopy(activated2, 0, result, 88, 4);
			System.arraycopy(sensorStatus2.devID.result, 0, result, 92, 44);
			System.arraycopy(nAlarm, 0, result, 136, 4);
			System.arraycopy(nSendEmail, 0, result, 140, 4);

			int len = 144;
			for (int i = 0; i < mailAddress.length; i++) {
				System.arraycopy(mailAddress[i].szAddr, 0, result, len,
						mailAddress[i].szAddr.length);
				len = len + 68;
			}
			System.arraycopy(nAlarmSDRec, 0, result, 416, 4);
			System.arraycopy(nPushNotify, 0, result, 420, 4);
			System.arraycopy(nPushPicture, 0, result, 424, 4);
			
			return result;
		}
	}

	public class SMsgAVIoctrlSetGuardResp {
		int result; // 閿熺瓔锟斤拷锟斤拷锟介敓绛嬶拷锟介敓鏂ゆ嫹锟斤拷娲伙拷锟�?: success; otherwise: failed.
		byte[] reserved = new byte[4];
	}

	/*
	 * //IOTYPE_USER_IPCAM_SETMOTIONDETECT2_REQ = 0x42A;
	 * //IOTYPE_USER_IPCAM_SETMOTIONDETECT2_RESP = 0x42B; public static class
	 * SMsgAVIoctrlSetMotionDetect2Req{ int channel; // AvServer Index int
	 * nSensitivity; // 閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷閿熶粙锟�?揪锟斤拷閿熸枻鎷凤拷锟斤拷閿熶粙锟斤拷鐣涳拷锟斤拷锟�? (Disabled) ~ 75(MAX): // Index in App.
	 * sensitivity value // 0 (閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷閿熶粙锟斤拷鐣涳拷锟斤拷锟�?0 // 1 (閿熺瓔锟斤拷閿燂�? 25 // 2 (閿熺瓔锟斤拷閿熸枻鎷烽敓锟�50 // 3 (閿熺瓔锟斤拷閿熸枻鎷烽敓锟�
	 * 75 SSensorStatus sInfo; // 閿熺瓔锟斤拷锟斤拷锟介敓绛嬶拷锟藉Δ锟介敓绛嬶拷锟藉Δ锟介敓绛嬶拷锟介敓鏂ゆ嫹閿熸枻鎷风灍锟藉Δ锟介敓绛嬶拷锟藉В锟介敓绛嬶拷锟介敓鏂ゆ嫹閿燂拷/ 閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷閿熸枻鎷烽敓锟絧ublic static byte[]
	 * parseContent(int channel,int sensitivity,SSensorStatus info) { byte[]
	 * result = new byte[4+4+20];
	 * 
	 * byte[] ch = Packet.intToByteArray_Little(channel); byte[] nSensitivity =
	 * Packet.intToByteArray_Little(sensitivity); byte[] paired =
	 * Packet.intToByteArray_Little(info.nPaired); byte[] camPreset =
	 * Packet.intToByteArray_Little(info.nCamPreset); byte[] linkOutlet =
	 * Packet.intToByteArray_Little(info.nLinkOutlet); byte[] activated =
	 * Packet.intToByteArray_Little(info.nActivated);
	 * 
	 * System.arraycopy(ch, 0, result, 0, 4); System.arraycopy(nSensitivity, 0,
	 * result, 4, 4); System.arraycopy(paired, 0, result, 8, 4);
	 * System.arraycopy(camPreset, 0, result, 12, 4);
	 * System.arraycopy(linkOutlet, 0, result, 16, 4);
	 * System.arraycopy(activated, 0, result, 20, 4);
	 * System.arraycopy(info.id.szDevID, 0, result, 24, 4);
	 * 
	 * return result; } } public class SMsgAVIoctrlSetMotionDetect2Resp { int
	 * result; // 閿熺瓔锟斤拷锟斤拷锟介敓绛嬶拷锟介敓鏂ゆ嫹锟斤拷娲伙拷锟�?: success; otherwise: failed. byte[] reserved = new
	 * byte[4]; }
	 * 
	 * //IOTYPE_USER_IPCAM_SETSOUNDDET_REQ = 0x424;
	 * //IOTYPE_USER_IPCAM_SETSOUNDDET_RESP = 0x425; public static class
	 * SMsgAVIoctrlSetSoundDetReq{ int channel; // AvServer Index int
	 * nSoundDetect; // 閿熺瓔锟斤拷濡わ拷閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷閿熶粙锟斤拷鐣涳拷锟斤拷褝鎷风灍锟介敓鏂ゆ嫹閿熸枻鎷风灍锟介敓鏂ゆ嫹锟斤拷锟介敓浠嬶拷锟界暃锟斤拷锟斤拷=0 // 閿熺瓔锟斤拷鐫界鎷风灍锟介敓鑺ワ拷銊拷锟介敓锟�1 // 閿熺瓔锟斤拷鐫界鎷风灍锟介敓鑺ワ拷銊拷锟介敓锟�2 // 閿熺瓔锟斤拷鐫界鎷风灍锟介敓鑺ワ拷銊拷锟介敓锟�3
	 * SSensorStatus sInfo; // 閿熺瓔锟斤拷锟斤拷锟介敓绛嬶拷锟藉Δ锟介敓绛嬶拷锟藉Δ锟介敓绛嬶拷锟介敓鏂ゆ嫹閿熸枻鎷风灍锟藉Δ锟介敓绛嬶拷锟藉В锟介敓绛嬶拷锟介敓鏂ゆ嫹閿燂拷/ 閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷閿熸枻鎷烽敓锟絧ublic static byte[]
	 * parseContent(int channel,int sensitivity,SSensorStatus info) { byte[]
	 * result = new byte[4+4+20];
	 * 
	 * byte[] ch = Packet.intToByteArray_Little(channel); byte[] nSensitivity =
	 * Packet.intToByteArray_Little(sensitivity); byte[] paired =
	 * Packet.intToByteArray_Little(info.nPaired); byte[] camPreset =
	 * Packet.intToByteArray_Little(info.nCamPreset); byte[] linkOutlet =
	 * Packet.intToByteArray_Little(info.nLinkOutlet); byte[] activated =
	 * Packet.intToByteArray_Little(info.nActivated);
	 * 
	 * System.arraycopy(ch, 0, result, 0, 4); System.arraycopy(nSensitivity, 0,
	 * result, 4, 4); System.arraycopy(paired, 0, result, 8, 4);
	 * System.arraycopy(camPreset, 0, result, 12, 4);
	 * System.arraycopy(linkOutlet, 0, result, 16, 4);
	 * System.arraycopy(activated, 0, result, 20, 4);
	 * System.arraycopy(info.id.szDevID, 0, result, 24, 4);
	 * 
	 * return result; } } public class SMsgAVIoctrlSetSoundDetResp { int result;
	 * // 閿熺瓔锟斤拷锟斤拷锟介敓绛嬶拷锟介敓鏂ゆ嫹锟斤拷娲伙拷锟�?: success; otherwise: failed. byte[] reserved = new byte[4]; }
	 */
	// IOTYPE_USER_IPCAM_SETSENSORDEV_REQ = 0x426;
	// IOTYPE_USER_IPCAM_SETSENSORDEV_RESP =0x427;
	public static class SMsgAVIoctrlSetSensorDevReq {
		int channel; // AvServer Index
		int nSensorType; // 閿熺瓔锟斤拷锟斤拷锟介敓绛嬶拷锟介敓鍊燂拷锟界挬锟斤拷锟窖嶆嫹鐬燂拷閿熸枻鎷烽敓锟�: SOS
							// 1: 閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷閿熸枻鎷凤拷锟斤拷IR
							// 2: 閿熺瓔锟斤拷閿熸枻鎷烽敓锟�
							// 3: 閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷閿熶粙锟斤拷鐣涳拷锟斤拷锟�?		int nCount; // 閿熺瓔锟斤拷閿熶粙锟斤拷锟斤拷锟介敓浠嬶拷锟界暃锟斤拷锟斤拷Index 閿熺瓔锟斤拷閿熸枻鎷峰锟介敓鏂ゆ嫹鐬燂拷閿熸枻鎷烽敓锟�~4(閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷锟斤拷锟介敓绛嬶拷锟斤拷锟斤拷閿熺瓔锟斤拷閿熸枻鎷凤拷锟界鎷�閿熺瓔锟斤拷閿燂拷)
		int[] nEnabled = new int[4]; // 閿熺瓔锟斤拷锟斤拷銊╋拷鈽嗭拷锟斤拷锟斤�?ON=1, OFF=0
		SSensorStatus[] arrStatus = new SSensorStatus[4]; // 閿熺瓔锟斤拷锟斤拷锟介敓绛嬶拷锟藉Δ锟介敓绛嬶拷锟藉Δ锟介敓绛嬶拷锟介敓鏂ゆ嫹閿熸枻鎷风灍锟藉Δ锟介敓绛嬶拷锟藉В锟介敓绛嬶拷锟介敓鏂ゆ嫹閿燂拷/
															// 閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷閿熸枻鎷烽敓锟�?
		public static byte[] parseContent(int channel, int sensorType,
				int count, int[] enabled, SSensorStatus[] arrStatus) {
			byte[] result = new byte[268];

			byte[] ch = Packet.intToByteArray_Little(channel);
			byte[] baSensorType = Packet.intToByteArray_Little(sensorType);
			byte[] baCount = Packet.intToByteArray_Little(count);

			System.arraycopy(ch, 0, result, 0, 4);
			System.arraycopy(baSensorType, 0, result, 4, 4);
			System.arraycopy(baCount, 0, result, 8, 4);
			int nLen = 12;
			for (int i = 0; i < 4; i++) {
				byte[] temp_enables = Packet.intToByteArray_Little(enabled[i]);
				System.arraycopy(temp_enables, 0, result, nLen, 4);
				nLen = nLen + 4;
			}

			for (int j = 0; j < 4; j++) {

				byte[] paired = Packet
						.intToByteArray_Little(arrStatus[j].nPaired);
				byte[] camPreset = Packet
						.intToByteArray_Little(arrStatus[j].nCamPreset);
				byte[] linkOutlet = Packet
						.intToByteArray_Little(arrStatus[j].nLinkOutlet);
				byte[] activated = Packet
						.intToByteArray_Little(arrStatus[j].nActivated);

				System.arraycopy(paired, 0, result, nLen, paired.length);
				nLen += 4;
				System.arraycopy(camPreset, 0, result, nLen, camPreset.length);
				nLen += 4;
				System.arraycopy(linkOutlet, 0, result, nLen, linkOutlet.length);
				nLen += 4;
				System.arraycopy(activated, 0, result, nLen, activated.length);
				nLen += 4;
				System.arraycopy(arrStatus[j].devID.result, 0, result, nLen,
						arrStatus[j].devID.result.length);
				nLen = nLen + 44;
			}

			return result;
		}
	}

	public class SMsgAVIoctrlSetSensorDevResp {
		int result; // 閿熺瓔锟斤拷锟斤拷锟介敓绛嬶拷锟介敓鏂ゆ嫹锟斤拷娲伙拷锟�?: success; otherwise: failed.
		byte[] reserved = new byte[4];
	}

	// IOTYPE_USER_IPCAM_GETSENSORDEV_REQ = 0x428;
	// IOTYPE_USER_IPCAM_GETSENSORDEV_RESP= 0x429;
	public static class SMsgAVIoctrlGetSensorDevReq {
		int channel; // AvServer Index
		int nSensorType; // 閿熺瓔锟斤拷锟斤拷锟介敓绛嬶拷锟介敓鍊燂拷锟界挬锟斤拷锟窖嶆嫹鐬燂拷閿熸枻鎷烽敓锟�: SOS
							// 1: 閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷閿熸枻鎷凤拷锟斤拷IR
							// 2: 閿熺瓔锟斤拷閿熸枻鎷烽敓锟�
							// 3: 閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷閿熶粙锟斤拷鐣涳拷锟斤拷锟�?
		public static byte[] parseContent(int channel, int sensorType) {
			byte[] result = new byte[4 + 4];

			byte[] ch = Packet.intToByteArray_Little(channel);
			byte[] baSensorType = Packet.intToByteArray_Little(sensorType);

			System.arraycopy(ch, 0, result, 0, 4);
			System.arraycopy(baSensorType, 0, result, 4, 4);

			return result;
		}
	}

	// public class SMsgAVIoctrlGetSensorDevResp {
	// int nCount; // 閿熺瓔锟斤拷锟斤拷锟介敓绛嬶拷锟介敓鏂ゆ嫹鐟锋拝鎷烽敓鏂ゆ嫹閿熺瓔锟斤拷閿熸枻鎷峰锟介敓鏂ゆ嫹鐬燂拷閿熸枻鎷烽敓锟�~4(閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷锟斤拷锟介敓绛嬶拷锟斤拷锟斤拷閿熺瓔锟斤拷閿熸枻鎷凤拷锟界鎷�閿熺瓔锟斤拷閿燂拷)
	// byte[] nEnabled = new byte[4]; // 閿熺瓔锟斤拷锟斤拷銊╋拷鈽嗭拷锟斤拷锟斤�?ON=1,OFF=0
	// SSensorStatus[] arrStatus = new SSensorStatus[4];
	// }
	// IOTYPE_USER_IPCAM_SETALARM_REQ = 0x42E;
	// IOTYPE_USER_IPCAM_SETALARM_RESP = 0x42F;
	// public static class SMsgAVIoctrlSetAlarmReq{
	// int channel; // AvServer Index
	// int nAlarm; // 閿熺瓔锟斤拷閿熸枻鎷凤拷锟斤拷锟斤拷锟介敓绛嬶拷锟介敓鏂ゆ嫹锟斤拷锟介敓鍊燂拷鍝勶拷闈╂嫹锟斤拷锟界暃锟斤拷锟斤�?ON=1,OFF=0
	//
	// public static byte[] parseContent(int channel,int alarm) {
	//
	// byte[] result = new byte[4+4];
	// byte[] ch = Packet.intToByteArray_Little(channel);
	// byte[] bAlarm = Packet.intToByteArray_Little(alarm);
	//
	// System.arraycopy(ch, 0, result, 0, 4);
	// System.arraycopy(bAlarm, 0, result, 4, 4);
	//
	// return result;
	// }
	// }
	// public class SMsgAVIoctrlSetAlarmResp {
	// int channel; // 閿熺瓔锟斤拷锟斤拷锟介敓绛嬶拷锟介敓鏂ゆ嫹锟斤拷娲伙拷锟�?: success; otherwise: failed
	// byte[] reserved = new byte[4]; // 閿熺瓔锟斤拷锟斤拷銊╋拷鈽嗭拷锟斤拷锟斤�?ON=1,OFF=0
	// }

	// 436
	public static class SMsgAVIoctrlAddNeoDevReq {
		int channel;
		SNeoDevStore sID;
		int nDevType;
		int nIndex;
		int nCurTemp;
		int nLowTemp;
		int nHiTemp;
		int nIRPower;

		public static byte[] parseContent(int channel,
				SNeoDevStore neoDevStore, int devType, int index, int curTemp,
				int lowTemp, int hiTemp, int IRPower) {

			byte[] result = new byte[4 + 32 + 4 + 4 + 4 + 4 + 4 + 4];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			System.arraycopy(neoDevStore.szDevID, 0, result, 4,
					neoDevStore.szDevID.length);
			System.arraycopy(neoDevStore.szDevName, 4, result, 8,
					neoDevStore.szDevName.length);
			byte[] bDevStoreIndex = Packet
					.intToByteArray_Little(neoDevStore.nIndex);
			System.arraycopy(bDevStoreIndex, 0, result, 32, 4);
			byte[] bDevType = Packet.intToByteArray_Little(devType);
			System.arraycopy(bDevType, 0, result, 36, 4);
			byte[] bIndex = Packet.intToByteArray_Little(index);
			System.arraycopy(bIndex, 0, result, 40, 4);
			byte[] bCurTemp = Packet.intToByteArray_Little(curTemp);
			System.arraycopy(bCurTemp, 0, result, 44, 4);
			byte[] bLowTemp = Packet.intToByteArray_Little(lowTemp);
			System.arraycopy(bLowTemp, 0, result, 48, 4);
			byte[] bHiTemp = Packet.intToByteArray_Little(hiTemp);
			System.arraycopy(bHiTemp, 0, result, 52, 4);
			byte[] bIRPower = Packet.intToByteArray_Little(IRPower);
			System.arraycopy(bIRPower, 0, result, 56, 4);

			return result;
		}

		public static byte[] parseContent(int channel, String szDevID,
				String szDevName, int sz_index, int curTemp, int lowTemp,
				int hiTemp, int devType, int index, int IRPower) {

			byte[] result = new byte[4 + 32 + 4 + 16];// 56

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			byte[] tempDevID = new byte[4];
			for (int i = 0; i < 4; i++) {
				tempDevID[i] = (byte) ((byte) Integer.parseInt(
						szDevID.substring(i * 2, i * 2 + 2), 16) & 0x0FF);
			}
			System.arraycopy(tempDevID, 0, result, 4, tempDevID.length);
			byte[] tempDevName = new byte[szDevName.getBytes().length];
			tempDevName = szDevName.getBytes();
			System.arraycopy(tempDevName, 0, result, 8, tempDevName.length);
			byte[] bDevStoreIndex = Packet.intToByteArray_Little(sz_index);
			System.arraycopy(bDevStoreIndex, 0, result, 32, 4);

			byte[] bCurTemp = Packet.intToByteArray_Little(curTemp);
			System.arraycopy(bCurTemp, 0, result, 36, 4);

			byte[] bLowTemp = Packet.intToByteArray_Little(lowTemp);
			System.arraycopy(bLowTemp, 0, result, 40, 4);

			byte[] bHiTemp = Packet.intToByteArray_Little(hiTemp);
			System.arraycopy(bHiTemp, 0, result, 44, 4);

			byte[] bDevType = Packet.intToByteArray_Little(devType);
			System.arraycopy(bDevType, 0, result, 48, 4);

			// byte[] bIndex = Packet.intToByteArray_Little(index);
			// System.arraycopy(bIndex, 0, result, 48, 4);

			byte[] bIRPower = Packet.intToByteArray_Little(IRPower);
			System.arraycopy(bIRPower, 0, result, 52, 4);

			return result;
		}
	}

	// IOTYPE_USER_IPCAM_GET_NAME_REQ = 0x451;
	public static class SMsgAVIoctrlCamGetNameReq {

		public static byte[] parseContent(int channel) {

			byte[] result = new byte[4];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	// IOTYPE_USER_IPCAM_CAMERA_RENAME_REQ = 0x453;
	public static class SMsgAVIoctrlCamRenameReq {

		public static byte[] parseContent(String cameraName) {

			byte[] result = new byte[24];
			byte[] tempCamName = new byte[cameraName.getBytes().length];
			tempCamName = cameraName.getBytes();
			System.arraycopy(tempCamName, 0, result, 0, tempCamName.length);

			return result;
		}
	}

	// 438
	public static class SMsgAVIoctrlGetNeoDevStatusReq {
		int channel; // Camera Index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel) {

			byte[] result = new byte[4];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			// result[4] = mode;

			return result;
		}
	}

	// public static final int IOTYPE_USER_IPCAM_REMOVENEODEV_REQ = 0x43A;
	// public static final int IOTYPE_USER_IPCAM_REMOVENEODEV_RESP = 0x43B;

	public static class SMsgAVIoctrlRemoveNeoDevReq {
		int channel; // Camera Index
		String sDevID;
		int nDevType;
		int nIRPower;
		byte[] reserved = new byte[16];

		public static byte[] parseContent(int channel, String devID,
				byte[] devType, int dev_index) {

			byte[] result = new byte[16];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			byte[] tempDevID = new byte[4];
			for (int i = 0; i < 4; i++) {
				tempDevID[i] = (byte) ((byte) Integer.parseInt(
						devID.substring(i * 2, i * 2 + 2), 16) & 0x0FF);
			}
			System.arraycopy(tempDevID, 0, result, 4, tempDevID.length);

			System.arraycopy(devType, 0, result, 8, 4);
			byte[] bIndex = Packet.intToByteArray_Little(dev_index);
			System.arraycopy(bIndex, 0, result, 12, 4);

			return result;
		}
	}

	// 448
	public static class SMsgAVIoctrlEditNeoDevReq {
		int channel;
		SNeoDevStore devStore;
		int nDevType;
		int nIndex;

		public static byte[] parseContent(int channel,
				SNeoDevStore neoDevStore, int devType, int index, int IRPower) {

			byte[] result = new byte[4 + 44 + 4 + 4 + 4];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			System.arraycopy(neoDevStore.szDevID, 0, result, 4, 4);
			System.arraycopy(neoDevStore.szDevName, 4, result, 8, 24);
			byte[] bDevStoreIndex = Packet
					.intToByteArray_Little(neoDevStore.nIndex);
			System.arraycopy(bDevStoreIndex, 0, result, 32, 4);

			byte[] bCurTemp = Packet
					.intToByteArray_Little(neoDevStore.nCurTemp);
			System.arraycopy(bCurTemp, 0, result, 36, 4);

			byte[] bLowTemp = Packet
					.intToByteArray_Little(neoDevStore.nLowTemp);
			System.arraycopy(bLowTemp, 0, result, 40, 4);

			byte[] bHiTemp = Packet.intToByteArray_Little(neoDevStore.nHiTemp);
			System.arraycopy(bHiTemp, 0, result, 44, 4);

			byte[] bDevType = Packet.intToByteArray_Little(devType);
			System.arraycopy(bDevType, 0, result, 48, 4);

			byte[] bIndex = Packet.intToByteArray_Little(index);
			System.arraycopy(bIndex, 0, result, 52, 4);

			byte[] bIRPower = Packet.intToByteArray_Little(IRPower);
			System.arraycopy(bIRPower, 0, result, 56, 4);

			return result;
		}

		public static byte[] parseContent(int channel, String szDevID,
				String szDevName, int sz_index, int curTemp, int lowTemp,
				int hiTemp, int devType, int index, int IRPower) {

			byte[] result = new byte[4 + 44 + 4 + 4 + 4];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			byte[] tempDevID = new byte[4];
			for (int i = 0; i < 4; i++) {
				tempDevID[i] = (byte) ((byte) Integer.parseInt(
						szDevID.substring(i * 2, i * 2 + 2), 16) & 0x0FF);
			}
			System.arraycopy(tempDevID, 0, result, 4, tempDevID.length);
			byte[] tempDevName = new byte[szDevName.getBytes().length];
			tempDevName = szDevName.getBytes();
			System.arraycopy(tempDevName, 0, result, 8, tempDevName.length);
			byte[] bDevStoreIndex = Packet.intToByteArray_Little(sz_index);
			System.arraycopy(bDevStoreIndex, 0, result, 32, 4);

			byte[] bCurTemp = Packet.intToByteArray_Little(curTemp);
			System.arraycopy(bCurTemp, 0, result, 36, 4);

			byte[] bLowTemp = Packet.intToByteArray_Little(lowTemp);
			System.arraycopy(bLowTemp, 0, result, 40, 4);

			byte[] bHiTemp = Packet.intToByteArray_Little(hiTemp);
			System.arraycopy(bHiTemp, 0, result, 44, 4);

			byte[] bDevType = Packet.intToByteArray_Little(devType);
			System.arraycopy(bDevType, 0, result, 48, 4);
			byte[] bIndex = Packet.intToByteArray_Little(index);
			System.arraycopy(bIndex, 0, result, 52, 4);
			byte[] bIRPower = Packet.intToByteArray_Little(IRPower);
			System.arraycopy(bIRPower, 0, result, 56, 4);
			return result;
		}
	}

	// 43C
	public static class SMsgAVIoctrlGetNeoSwitchStatusReq {
		int channel; // Camera Index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel) {

			byte[] result = new byte[4];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			// result[4] = mode;

			return result;
		}
	}

	// IOTYPE_USER_IPCAM_SETNEOSWITCH_REQ = 0x43E;
	// IOTYPE_USER_IPCAM_SETNEOSWITCH_RESP = 0x43F;

	public static class SMsgAVIoctrlSetNeoSwitchReq {
		int channel; // Camera Index
		int nSwitchIdx;
		int nOn;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel, int switchIdx, int nOn) {

			byte[] result = new byte[12];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			byte[] bSwitchIdx = Packet.intToByteArray_Little(switchIdx);
			System.arraycopy(bSwitchIdx, 0, result, 4, 4);

			byte[] bOn = Packet.intToByteArray_Little(nOn);
			System.arraycopy(bOn, 0, result, 8, 4);

			return result;
		}
	}

	// IOTYPE_USER_IPCAM_QUERY2WAYNEODEV_REQ = 0x44A;
	// IOTYPE_USER_IPCAM_QUERY2WAYNEODEV_RESP = 0x44B;

	public static class SMsgAVIoctrlQuery2WayNeoDevReq {
		int channel; // Camera Index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel) {

			byte[] result = new byte[4];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	// IOTYPE_USER_IPCAM_SETPRESET_REQ = 0x440;
	// IOTYPE_USER_IPCAM_SETPRESET_RESP = 0x441;

	public static class SMsgAVIoctrlSetPresetReq {
		int channel; // Camera Index
		int nPresetIdx;
		byte[] reserved = new byte[8];

		public static byte[] parseContent(int channel, int presetIdx) {

			byte[] result = new byte[8];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			byte[] bPresetIdx = Packet.intToByteArray_Little(presetIdx);
			System.arraycopy(bPresetIdx, 0, result, 4, 4);

			return result;
		}
	}

	// IOTYPE_USER_IPCAM_GETPRESET_REQ = 0x442;
	// IOTYPE_USER_IPCAM_GETPRESET_RESP = 0x443;

	public static class SMsgAVIoctrlGetPresetReq {
		int channel; // Camera Index
		int nPresetIdx;
		byte[] reserved = new byte[8];

		public static byte[] parseContent(int channel, int presetIdx) {

			byte[] result = new byte[8];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			byte[] bPresetIdx = Packet.intToByteArray_Little(presetIdx);
			System.arraycopy(bPresetIdx, 0, result, 4, 4);

			return result;
		}
	}

	// IOTYPE_USER_IPCAM_CURRTEMPERATURE_REQ = 0x44C
	// IOTYPE_USER_IPCAM_CURRTEMPERATURE_RESP = 0x44D
	// IOTYPE_USER_IPCAM_SETALARMRING_REQ = 0x44E
	// IOTYPE_USER_IPCAM_SETALARMRING_RESP = 0x44F

	public static class SMsgAVIoctrlCurrTemperatureReq {
		int channel; // Camera Index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel, int on) {

			byte[] result = new byte[8];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			byte[] bOn = Packet.intToByteArray_Little(on);
			System.arraycopy(bOn, 0, result, 4, 4);

			return result;
		}
	}

	public static class SMsgAVIoctrlGetAlarmRingResp {
		int result; // 閿熺瓔涓圭焦锟介敓鏂ゆ嫹0: success; otherwise: failed.
		int nStatus;// 閿熸枻锟斤拷閿燂�?閿熺瓔鐖遍敓浠嬶拷锟界悰锟界綗锟�閿熷�锟藉嚖鎷凤拷锟界尨鎷稯N=1
					// OFF=0
					// Offiine=2
	}

	public static class SMsgAVIoctrlSetAlarmRingReq {
		int channel; // Camera Index
		int nOn;
		byte[] reserved = new byte[8];

		public static byte[] parseContent(int channel, int isOn) {

			byte[] result = new byte[8];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			byte[] bOn = Packet.intToByteArray_Little(isOn);
			System.arraycopy(bOn, 0, result, 4, 4);

			return result;
		}
	}

	public static class SMsgAVIoctrlSetAlarmRingResp {
		int result; // 閿熺瓔涓圭焦锟介敓鏂ゆ嫹0: success; otherwise: failed.
		int nStatus;// 閿熸枻锟斤拷閿燂�?閿熺瓔鐖遍敓浠嬶拷锟界悰锟界綗锟�閿熷�锟藉嚖鎷凤拷锟界尨鎷稯N=1
					// OFF=0
					// Offiine=2
	}

	public static class SMsgAVIoctrlSetIRLightReq {
		int channel; // Camera Index
		int nOn;
		byte[] reserved = new byte[8];

		public static byte[] parseContent(int channel, int isOn) {

			byte[] result = new byte[8];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			byte[] bOn = Packet.intToByteArray_Little(isOn);
			System.arraycopy(bOn, 0, result, 4, 4);

			return result;
		}
	}

	public static class SMsgAVIoctrlSetIRLightResp {
		int result; // 閿熺瓔涓圭焦锟介敓鏂ゆ嫹0: success; otherwise: failed.
		int nStatus;// 閿熸枻锟斤拷閿燂�?閿熺瓔鐖遍敓浠嬶拷锟界悰锟界綗锟�閿熷�锟藉嚖鎷凤拷锟界尨鎷稯N=1
					// OFF=0
					// Offiine=2
	}

	public static class SMsgAVIoctrlGetIsOptZoomReq {
		int channel; // Camera Index

		public static byte[] parseContent(int channel) {
			byte[] result = new byte[4];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
		}
	}

	public static class OTYPE_USER_IPCAM_GETISOPTZOOM_RESP {
		int result; // 閿熺瓔涓圭焦锟介敓鏂ゆ嫹0:閿熷�锟芥拝鎷凤拷锟斤拷锟斤拷锟斤拷锟斤�?1:閿熸枻锟斤綁锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�?		byte[] reserved = new byte[4];
	}
	
	public static class SMsgAVIoctrlGetDevUTCTimeReq {
		int channel; // Camera Index
		public static byte[] parseContent(int channel) {
			byte[] result = new byte[4];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
		}
	}
	
	
	public static class SMsgAVIoctrlGetNTPCfgReq {
		int channel; // Camera Index
		public static byte[] parseContent(int channel) {
			byte[] result = new byte[4];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
		}
	}
	
	public static class SMsgAVIoctrlSetNTPCfgReq {
		int channel; // Camera Index
		int bNTPAdj; //1:NTP ON  0:NTP OFF
		int NTPSVR;// NTP Server 1:time.nist.gov 2:time.kriss.re.kr 3:time.windows.com 4:time.nuri.net

		byte[] reserved = new byte[12];

		public static byte[] parseContent(int channel, int bNTPAdj,int NTPSVR) {

			byte[] result = new byte[12];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			byte[] adj = Packet.intToByteArray_Little(bNTPAdj);
			System.arraycopy(adj, 0, result, 4, 4);
			byte[] svr = Packet.intToByteArray_Little(NTPSVR);
			System.arraycopy(svr, 0, result, 8, 4);

			return result;
		}
	}
	public static class SMsgAVIoctrlSetDevUTCTimeReq {
		int channel;	// AvServer Index
		long utc_time;	// the number of milliseconds passed since the UNIX epoch (January 1, 1970 UTC)


		byte[] reserved = new byte[8];

		public static byte[] parseContent(int channel, long utc_time) {

			byte[] result = new byte[12];

			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			byte[] time = Packet.longToByteArray_Little(utc_time);
			System.arraycopy(time, 0, result, 4, 8);

			return result;
		}
	}
	public static class SMsgAVIoctrlGetTemperatureHumidityReq{
		int channel; // Camera Index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
		
		}
	}
	
	public static class SMsgAVIoctrlGetTemperatureHumidityResp{
		float CurrentTemperature; 
		float CurrentHumidity;
		float LowTemperature;
		float HighTemperature;
		float LowHumidity;
		float HighHumidity;
		public static int TempOn;
		public static int HumOn;
	}
	
	
	public static byte[] float2byte(float f) {  
	      
	    // 鎶奻loat杞崲涓篵yte[]  
	    int fbit = Float.floatToIntBits(f);  
	      
	    byte[] b = new byte[4];    
	    for (int i = 0; i < 4; i++) {    
	        b[i] = (byte) (fbit >> (24 - i * 8));    
	    }   
	      
	    // 缈昏浆鏁扮粍  
	    int len = b.length;  
	    // 寤虹珛涓�釜涓庢簮鏁扮粍鍏冪礌绫诲�?鐩稿悓鐨勬暟缁� 
	    byte[] dest = new byte[len];  
	    // 涓轰簡闃叉淇敼婧愭暟缁勶紝灏嗘簮鏁扮粍鎷疯礉涓�唤鍓湰  
	    System.arraycopy(b, 0, dest, 0, len);  
	    byte temp;  
	    // 灏嗛『浣嶇i涓笌鍊掓暟绗琲涓氦鎹�?
	    for (int i = 0; i < len / 2; ++i) {  
	        temp = dest[i];  
	        dest[i] = dest[len - i - 1];  
	        dest[len - i - 1] = temp;  
	    }  
	      
	    return dest;  
	      
	} 
	
	public static class SMsgAVIoctrlSetTemperatureHumidityReq{
		
		int TempOn;
		float LowTemperature;
		float HighTemperature;
		int HumOn;
		float LowHumidity;
		float HighHumidity;

		public static byte[] parseContent(int TempOn,float LowTemperature,float HighTemperature,int HumOn,float LowHumidity,float HighHumidity) {
			byte[] result = new byte[24];
			
//			avSendIOCtrl(0, 0x481, 01 00 00 00 00 00 C0 41 00 00 08 42 01 00 00 00 00 00 98 41 00 00 AC 42 )
			byte[] TOn=Packet.intToByteArray_Little(TempOn); ;/*娓╁害鎶ヨ寮��?1:on 0:off*/
			System.arraycopy(TOn, 0, result, 0, 4);
			byte[] lt=float2byte(LowTemperature); // -30.0 ~ 100.0鈩�
			System.arraycopy(lt, 0, result, 4, 4);
			byte[] ht=float2byte(HighTemperature); // -30.0 ~ 100.0鈩�
			System.arraycopy(ht, 0, result, 8, 4);    
			byte[] hon=Packet.intToByteArray_Little(HumOn); ;/* �?��害鎶ヨ寮��?1:on 0:off*/
			System.arraycopy(hon, 0, result, 12, 4);
			byte[] lh=float2byte(LowHumidity); // 0% ~100%
			System.arraycopy(lh, 0, result, 16, 4);
			byte[] hh=float2byte(HighHumidity); // 0% ~100%
			System.arraycopy(hh, 0, result, 20, 4);
			
		
			return result;
		
		}
	}
	
	public class SMsgAVIoctrlSetTemperatureHumidityResp {
		
		int result; // 0 - succeed, 1 - failed
		
	}
	
	//IOTYPE_USER_IPCAM_SETSOUNDDETECT_REQ		= 0x03B2,
		//IOTYPE_USER_IPCAM_SETSOUNDDETECT_RESP		= 0x03B3,
		//IOTYPE_USER_IPCAM_GETSOUNDDETECT_REQ		= 0x03B4,
		//IOTYPE_USER_IPCAM_GETSOUNDDETECT_RESP		= 0x03B5,
	
	public static class SMsgAVIoctrlGetSoundDetectReq{
		int channel; // Camera Index
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int channel) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
		
		}
	}
	
	public static class SMsgAVIoctrlGetSoundDetectResp{
		int channel; // Camera Index
	    int sensitivity; 	// 0(Disabled) ~ 100(MAX)
		
		}
	
	public static class SMsgAVIoctrlSetSoundDetectReq{

		public static byte[] parseContent(int channel,int sensitivity) {
			byte[] result = new byte[8];
			
			
			byte[] ch=Packet.intToByteArray_Little(channel); // Camera Index
			System.arraycopy(ch, 0, result, 0, 4);
			byte[] st=Packet.intToByteArray_Little(sensitivity); // 0(Disabled) ~ 100(MAX)
			System.arraycopy(st, 0, result, 4, 4);
			
			
			return result;
		
		}
	}

	public static class SMsgAVIoctrlSetSoundDetectResp{
		int result;	// 0: success; otherwise: failed.
		byte[] reserved = new byte[4];
	}
	
	/*
	 * IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ_EXT = 0x471
	 * IOTYPE_USER_IPCAM_GET_TIMEZONE_RESP_EXT = 0x472
	 * IOTYPE_USER_IPCAM_SET_TIMEZONE_REQ_EXT = 0x473
	 * IOTYPE_USER_IPCAM_SET_TIMEZONE_RESP_EXT = 0x474
	 */
	public static class SMsgAVIoctrlTimeZoneExt {
		public int cbSize;// the following package size in bytes, should be sizeof(SMsgAVIoctrlTimeZone)
		public int nIsSupportTimeZone;
		public int nGMTDiff;// the difference between GMT in hours
		public byte[] szTimeZoneString = new byte[256];// the timezone description string in multi-bytes char format
		public long  local_utc_time;// the number of seconds passed
		// since the UNIX epoch (January 1, 1970 UTC)
		public int dst_on; //0 off 1 on    //婢讹拷娴犮�?锟斤�?
		public static byte[] parseContent() {

			return new byte[12 + 256 + 4 + 4];
		}

		public static byte[] parseContent(int cbSize, int nIsSupportTimeZone,
			int nGMTDiff, byte[] szTimeZoneString,long local_utc_time,int dst_on) {

			byte[] result = new byte[12 + 256 + 4 + 4 ];

			byte[] size = Packet.intToByteArray_Little(cbSize);
			System.arraycopy(size, 0, result, 0, 4);

			byte[] isSupportTimeZone = Packet
					.intToByteArray_Little(nIsSupportTimeZone);
			System.arraycopy(isSupportTimeZone, 0, result, 4, 4);

			byte[] GMTDiff = Packet.intToByteArray_Little(nGMTDiff);
			System.arraycopy(GMTDiff, 0, result, 8, 4);

			System.arraycopy(szTimeZoneString, 0, result, 12,
					szTimeZoneString.length);

			byte[] time = Packet.longToByteArray_Little(local_utc_time);
			System.arraycopy(time, 0, result,12+256,4);
			
			byte[] b_dst_on = Packet.intToByteArray_Little(dst_on);
			System.arraycopy(b_dst_on, 0, result, 12+256+4, 4);
			
			return result;
		}
	}
	
}
