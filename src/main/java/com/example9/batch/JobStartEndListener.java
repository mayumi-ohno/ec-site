package com.example9.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 処理の開始と終了の合図に使うクラス.
 * 
 * @author mayumiono
 *
 */
public class JobStartEndListener extends JobExecutionListenerSupport {

	@SuppressWarnings("unused")
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobStartEndListener(JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated constructor stub
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * ジョブの開始を合図に実行されるメソッドです.
	 * 
	 * @param jobExecution
	 */
	@Override
	public void beforeJob(JobExecution jobExecution) {
		super.beforeJob(jobExecution);
		System.out.println("処理開始");
	}

	/**
	 * ジョブの終了を合図に実行されるメソッドです.
	 * 
	 * @param jobExecution
	 */
	@Override
	public void afterJob(JobExecution jobExecution) {
		super.afterJob(jobExecution);
		System.out.println("処理終了");
	}

}
