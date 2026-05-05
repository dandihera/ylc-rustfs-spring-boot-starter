package com.cqcloud.platform.config;

import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Flex defaults used by this starter's SysFile entity.
 */
@Configuration
public class MyBatisFlexConfiguration {

	@Bean
	public MyBatisFlexCustomizer rustfsMyBatisFlexCustomizer() {
		return globalConfig -> {
			globalConfig.setNormalValueOfLogicDelete("0");
			globalConfig.setDeletedValueOfLogicDelete("1");
		};
	}

}
