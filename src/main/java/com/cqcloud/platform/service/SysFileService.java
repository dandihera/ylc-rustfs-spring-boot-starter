package com.cqcloud.platform.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.cqcloud.platform.dto.SysFileSelDto;
import com.cqcloud.platform.entity.SysFile;
import com.cqcloud.platform.vo.SysFileVo;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 系统基础信息--文件管理服务类
 *
 * @author weimeilayer@gmail.com ✨
 * @date 💓💕 2023年5月20日 🐬🐇 💓💕
 */
public interface SysFileService extends IService<SysFile> {

	/**
	 * 上传文件
	 * @param file 文件
	 * @param groupId 分组id
	 * @return 文件信息
	 */
	Map<String, String> uploadFile(MultipartFile file, String groupId, Integer sort);

	/**
	 * base64数据上传
	 * @param base64Data
	 * @param groupId
	 * @param sort
	 * @return
	 */
	Map<String, String> uploadBase64File(String base64Data, String groupId, Integer sort);

	/**
	 * 删除文件
	 * @param id 主键
	 * @return 是否成功
	 */
	Boolean deleteFile(String id);

	/**
	 * 分页查询SysFile
	 * @param page 分页参数
	 * @param dto 查询参数
	 * @return 分页列表
	 */
	Page<SysFileVo> getSysFileVoPage(Page<SysFileVo> page, SysFileSelDto dto);

	/**
	 * 读取文件
	 * @param fileId 文件ID
	 */
	void getFile(String fileId, HttpServletResponse response);

	/**
	 * 根据id预览文件
	 * @param fileId 文件ID
	 */
	void previewFile(String fileId, HttpServletResponse response);

	/**
	 * 根据文件名称预览文件
	 * @param fileName 文件名称
	 */
	void previewByFileName(String fileName, HttpServletResponse response);

}
