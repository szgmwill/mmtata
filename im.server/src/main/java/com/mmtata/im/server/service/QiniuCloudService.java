package com.mmtata.im.server.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.qiniu.util.Auth;

/**
 * 七牛云服务
 * @author 张伟锐
 *
 */
@Service
public class QiniuCloudService implements InitializingBean {
	private static final Logger logger = Logger.getLogger(QiniuCloudService.class);
	
	private Auth qiniuAuth;
	
//	private UploadManager uploadManager;
	
	@Value(value="${qiniu.access_key}")
	private String ACCESS_KEY;
	
	@Value(value="${qiniu.secret_key}")
	private String SECRET_KEY;
	
	@Value(value="${qiniu.bucket}")
	private String BUCKET;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		//初始化七牛
		qiniuAuth = Auth.create(ACCESS_KEY, SECRET_KEY);
		
		//重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
//		uploadManager = new UploadManager();
	}
	
	/**
	 * 获取上传文件凭证
	 */
	public String getUpToken() {
		String token = qiniuAuth.uploadToken(BUCKET);
		
		logger.info("upload token from qiniu is:"+token);
		
		return token;
	}



	
}
