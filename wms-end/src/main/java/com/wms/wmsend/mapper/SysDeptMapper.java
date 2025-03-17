package com.wms.wmsend.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.SysDept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.wmsend.model.vo.DeptResultVo;
import org.apache.ibatis.annotations.Param;

/**
* @author Ayanami
* @description 针对表【sys_dept(部门信息表)】的数据库操作Mapper
* @createDate 2025-03-03 15:34:57
* @Entity com.wms.wmsend.model.entity.SysDept
*/
public interface SysDeptMapper extends BaseMapper<SysDept> {

    IPage<DeptResultVo> listByPage(@Param("page") Page<SysDept> page,@Param("deptName") String deptName,@Param("unitId") String unitId);
}




