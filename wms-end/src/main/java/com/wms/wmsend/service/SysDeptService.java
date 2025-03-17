package com.wms.wmsend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.SysDept;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.wmsend.model.vo.DeptResultVo;

/**
* @author Ayanami
* @description 针对表【sys_dept(部门信息表)】的数据库操作Service
* @createDate 2025-03-03 15:34:57
*/
public interface SysDeptService extends IService<SysDept> {

    IPage<DeptResultVo> listByPage(Page<SysDept> page, String deptName, String unitId);
}
