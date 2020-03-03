package com.leemon.wushiwan.entity;

import lombok.Data;

@Data
public class MayiMission {

	/**
	 * task_id : 1900812
	 * class_id : 15
	 * class_name : 电商
	 * unit_price : 3
	 * remainder_num : 42
	 * ongoing_num : 8
	 * class_img : /resources/images/antHelp/renwu_logo_11.png
	 * top_flag : 1
	 * push_flag : 1
	 * complete_count : 0
	 * push_s_time : 1577755869
	 * push_e_time : 1577762495
	 * top_e_time : 1577762488
	 * jd_time_limit : 0
	 * check_time_limit : 24
	 * task_title : 扫码一秒 秒结帐
	 * check_time : 0
	 * sub_check_time : 0
	 * user_passing_rate : 0
	 * guarantee_flag : 0
	 * guarantee_money : 2000
	 * equipment_type : 0
	 * waitCount : 0
	 * checkCount : 0
	 * finishCount : 0
	 */

	private int task_id;
	private int class_id;
	private String class_name;
	private int unit_price;
	private int remainder_num;
	private int ongoing_num;
	private String class_img;
	private int top_flag;
	private int push_flag;
	private int complete_count;
	private int push_s_time;
	private int push_e_time;
	private int top_e_time;
	private int jd_time_limit;
	private int check_time_limit;
	private String task_title;
	private int check_time;
	private int sub_check_time;
	private int user_passing_rate;
	private int guarantee_flag;
	private int guarantee_money;
	private int equipment_type;
	private int waitCount;
	private int checkCount;
	private int finishCount;
}
