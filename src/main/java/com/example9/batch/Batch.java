package com.example9.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example9.domain.Item;

@Configuration
@EnableBatchProcessing
public class Batch {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	/**
	 * csvファイルを読み込むreaderメソッドです.
	 * 
	 * @return
	 */
	@Bean
	@StepScope
	public FlatFileItemReader<Item> reader() {
		FlatFileItemReader<Item> reader = new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource("new_items.csv"));
		reader.setLineMapper(new DefaultLineMapper<Item>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "id", "name", "description", "priceM", "priceL", "imagePath" });
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<Item>() {
					{
						setTargetType(Item.class);
					}
				});
			}
		});
		return reader;

	}

	/**
	 * Stepの処理部分を返すメソッドです.
	 * 
	 * @return stepの処理（processor）
	 */
	@Bean
	public ItemItemProcessor processor() {
		return new ItemItemProcessor();
	}

	/**
	 * writerの処理をするメソッドです.
	 * 
	 * @return
	 */
	@Bean
	public JdbcBatchItemWriter<Item> writer() {
		JdbcBatchItemWriter<Item> writer = new JdbcBatchItemWriter<>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		writer.setSql(
				"INSERT INTO items (id,name,description,price_m,price_l,image_path) VALUES (:id,:name,:description,:priceM,:priceL,:imagePath)");
		writer.setDataSource(dataSource);
		return writer;
	}

	/**
	 * ジョブの実行前、終了後に処理を挟むメソッドです.
	 * 
	 * @return
	 */
	@Bean
	public JobExecutionListener listener() {
		return new JobStartEndListener(new JdbcTemplate(dataSource));
	}

	/**
	 * ステップの実行を構築するメソッドです.
	 * 
	 * @return
	 */
	@Bean
	public Step truncateStep() {
		return stepBuilderFactory.get("truncateStep").tasklet(truncateTasklet()).build();
	}

	/**
	 *
	 * @return
	 */
	@Bean
	public MethodInvokingTaskletAdapter truncateTasklet() {
		MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();
		adapter.setTargetObject(truncateService());
		adapter.setTargetMethod("execute");
		return adapter;
	}

	/**
	 * TruncateStepの実行処理とそれが完了したことを通知する処理を返すメソッドです
	 * 
	 * @return
	 */
	@Bean
	public TruncateService truncateService() {
		return new TruncateServiceImpl();
	}

	/**
	 * ステップの実行を構築するメソッドです.
	 * 
	 * @return writerStep1
	 */
	@Bean
	public Step writerStep1() {
		return stepBuilderFactory.get("writerStep1")
				// 設定した値ごとにTransaction処理を行う.
				.<Item, Item>chunk(10).reader(reader()).processor(processor()).writer(writer()).build();
	}

	/**
	 * ジョブ（各ステップ）の実行メソッドです.
	 * 
	 * @return
	 */
	@Bean
	public Job testJob() {
		return jobBuilderFactory.get("SampleJob")
				// これがないと全く同じジョブが実行されたことになり連続で同じジョブが実行されなくなるので必須.
				.incrementer(new RunIdIncrementer())
				// startとendの処理を読み込ませる.
				.listener(listener()).flow(truncateStep()).next(writerStep1()).end().build();
	}
}
