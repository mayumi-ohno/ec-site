package com.example9.controller;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.example9.domain.Item;

//@Configuration
//@EnableBatchProcessing
public class BatchController {

//	@Autowired
//	private JobBuilderFactory jobBuilderFactory;
//
//	@Autowired
//	private StepBuilderFactory stepBuilderFactory;
//
//	/** CSVファイルパス */
//	private static final String CSV_FILE_PATH = "C:/CsvData/sample.csv";
//
//	/**
//	 * CSVファイルを読み込む.
//	 * 
//	 * @return CSVファイル内のデータ
//	 */
//	@Bean
//	public FlatFileItemReader<Item> itemReader() {
//		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
//		lineTokenizer.setNames(new String[] { "id", "name", "description", "price_m", "price_l", "image_path" });
//
//		BeanWrapperFieldSetMapper<Item> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
//		fieldSetMapper.setTargetType(Item.class);
//
//		DefaultLineMapper<Item> lineMapper = new DefaultLineMapper<>();
//		lineMapper.setLineTokenizer(lineTokenizer);
//		lineMapper.setFieldSetMapper(fieldSetMapper);
//
//		FlatFileItemReader<Item> reader = new FlatFileItemReader<>();
//		reader.setResource(new FileSystemResource(CSV_FILE_PATH));
//		reader.setLinesToSkip(1);
//		reader.setEncoding("UTF-8");
//		reader.setLineMapper(lineMapper);
//		return reader;
//	}
}
