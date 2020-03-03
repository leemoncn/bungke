package com.leemon.wushiwan.service;

import com.leemon.wushiwan.entity.CoreMissionComplaint;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-06-14
 */
public interface ICoreMissionComplaintService extends IService<CoreMissionComplaint> {

	CoreMissionComplaint getCoreMissionComplaintByMissionAcceptId(Integer missionAcceptId);
}
