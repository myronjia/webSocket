package com.jkhl.entrance.job;


import com.jkhl.entrance.dao.IdentityInformationDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
@Slf4j
public class Job {

	@Autowired
	private IdentityInformationDao identityInformationDao;

	/**
	 * 每晚更新人员表的最后刷卡时间
	 */
	/*@Scheduled(cron = "01 42 10 * * ?")
	public void updateLastPassTime(){
		identityInformationDao.updateLastPassTime();
	}*/



	/**
	 * 每晚检查三十天未刷卡人员, 并将是否入住设为否
	 * @author jzw
	 * @date 2020年3月23日09:23:32
	 */
//	@Scheduled(cron = "01 30 14 * * ?")
	public void mesPushMeetJoblast() {
		Integer days = 30;
		//获取当前时间毫秒值
		Long timeMillis = System.currentTimeMillis();
		scheduledSetUpLived(timeMillis, days);
	}


	/**
	 * 将距最后一次刷卡时间超过30天的人员 的是否入住字段改为否
	 *
	 * @param days 天数
	 */
	public void scheduledSetUpLived(Long timeMillis, Integer days) {

		List<String> infoNumberList = new ArrayList<>();
		infoNumberList = identityInformationDao.queryNotSwiped(timeMillis , days);

		if (CollectionUtils.isNotEmpty(infoNumberList)) {
			identityInformationDao.updateLocked(infoNumberList);
		}

	}


}


