package com.mingdotyang.dtssubscribeclient.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mingdotyang.dtssubscribeclient.model.SubscribeInstance;
import lombok.extern.slf4j.Slf4j;


@Repository
@Slf4j
public class SubscribeInstanceRepository {

	private static final String LOAD_SUBCRIBE_INSTANCE_INFO = "select * from subscribe_instance where id = ?";

	private static final String UPDATE_OFFSET = "update subscribe_instance set start_time = ?, offset=? where id = ?";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public SubscribeInstance loadSubscribeInstance(Long instanceId) {
		try {
			return this.jdbcTemplate.queryForObject(LOAD_SUBCRIBE_INSTANCE_INFO, new Object[]{instanceId},
					(rs, index) -> new SubscribeInstance()
							.setId(rs.getLong("id"))
							.setInstanceName(rs.getString("instance_name"))
							.setBrokers(rs.getString("brokers"))
							.setTopic(rs.getString("topic"))
							.setUsername(rs.getString("username"))
							.setPassword(rs.getString("password"))
							.setGroupId(rs.getString("group_id"))
							.setSessionTimeout(rs.getLong("session_timeout"))
							.setAutoCommitInterval(rs.getLong("auto_commit_interval"))
							.setPollTimeout(rs.getLong("poll_timeout"))
							.setPartition(rs.getInt("partition"))
							.setStartTime(rs.getLong("start_time"))
							.setOffset(rs.getLong("offset")));
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	public void updateInstanceOffset(Long instanceId, Long sourceTimestamp, Long offset) {
		try {
			this.jdbcTemplate.update(UPDATE_OFFSET, new Object[]{sourceTimestamp, offset, instanceId});
		} catch (DataAccessException dae) {
			log.error(dae.getMessage());
		}
	}

}
