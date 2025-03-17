package com.wms.wmsend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.wmsend.model.entity.SysDept;
import com.wms.wmsend.model.vo.DeptResultVo;
import com.wms.wmsend.service.SysDeptService;
import com.wms.wmsend.mapper.SysDeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author Ayanami
* @description 针对表【sys_dept(部门信息表)】的数据库操作Service实现
* @createDate 2025-03-03 15:34:57
*/
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept>
    implements SysDeptService{

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Override
    public IPage<DeptResultVo> listByPage(Page<SysDept> page, String deptName, String unitId) {
        return sysDeptMapper.listByPage(page, deptName, unitId);
    }
}




