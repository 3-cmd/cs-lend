package com.cs.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cs.core.listener.ExcelDictListener;
import com.cs.core.pojo.dto.ExcelDictDTO;
import com.cs.core.pojo.entity.Dict;
import com.cs.core.mapper.DictMapper;
import com.cs.core.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Service
@Slf4j
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    private static final String REDIS_KEY = "srb:core:dictList";

    @Autowired
    private DictMapper dictMapper;
    @Resource
    private RedisTemplate<String, Object> redis;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importData(InputStream is) {
        EasyExcel.read(is, ExcelDictDTO.class, new ExcelDictListener(dictMapper)).sheet().doRead();
        log.info("Excel导入成功");
    }

    /**
     * 获取数据库中的数据并且拷贝到excel对应的实体类中
     *
     * @param
     * @return
     */
    @Override
    public List<ExcelDictDTO> getExcelDictDTOS() {
        List<Dict> list = this.list();
        List<ExcelDictDTO> excelDictDTOS = list.stream().map(iter -> {
            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            BeanUtils.copyProperties(iter, excelDictDTO);
            return excelDictDTO;
        }).collect(Collectors.toList());
        return excelDictDTOS;
    }


    //如下方法只是一个嵌套查找的方法
    //本方法是将实体类中再添加一个该实体类型的集合,然后将每一条数据进行stream流遍历每次过滤出来一条实体的parent与所有数据的id相同的值进行设置到集合中
    @Override
    public List<Dict> listWithTree() {
        /**
         * 其中我们将关于redis的所有代码片段try catch包裹起来,如果redis出现问题,可以保证将我们的数据从数据库中查询出来然后返回到前端
         */

        //1.查询出所有分类
        List<Dict> dictList = dictMapper.selectList(null);
        List<Dict> level1Menus = null;
        try {
            //先查询redis中是否存在数据列表
            level1Menus = (List<Dict>) redis.opsForValue().get(REDIS_KEY);
            //存在从redis中取出数据
            if (level1Menus != null) {
                log.info("从redis中取值");
                return level1Menus;
            }
        } catch (Exception e) {
            log.error("redis服务器异常：" + ExceptionUtils.getStackTrace(e));//此处不抛出异常，继续执行后面的代码
        }
        //2.组装成树形的父子结构
        //2.1 找到所有一级分类
        level1Menus = dictList.stream().filter(dictEntity -> {
            //第一级分类的父id为0
            return dictEntity.getParentId() == 0;
        }).map(menu -> {
            menu.setChildren(getChildren(menu, dictList));
            return menu;
        }).collect(Collectors.toList());
        log.info("从数据库中取值");
        //将查询的数据存储到redis中
        log.info("数据存入redis");
        try {
            redis.opsForValue().set(REDIS_KEY, level1Menus, 5, TimeUnit.MINUTES);
        }catch (Exception e) {
            log.error("redis服务器异常：" + ExceptionUtils.getStackTrace(e));//此处不抛出异常，继续执行后面的代码
        }
        return level1Menus;
    }
    public List<Dict> getChildren(Dict root, List<Dict> all) {
        List<Dict> children = all.stream()
                .filter(DictEntity -> DictEntity.getParentId().equals(root.getId()))
                .map(dictEntity -> {
                    dictEntity.setChildren(getChildren(dictEntity, all));
                    return dictEntity;
                }).collect(Collectors.toList());
        return children;
    }
    @Override
    public List<Dict> getListByDictCode(String dictCode) {
        LambdaQueryWrapper<Dict> dictQueryWrapper = new LambdaQueryWrapper<>();
        dictQueryWrapper.eq(Dict::getDictCode, dictCode);
        Dict dict = baseMapper.selectOne(dictQueryWrapper);
        return getChildren(dict,baseMapper.selectList(null));
    }

    @Override
    public String getNameByParentDictCodeAndValue(String dictCode, Integer value) {
        //根据dictCode查询出子类数据,然后根据value值查询出数据
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dict::getDictCode,dictCode);
        Dict dict = baseMapper.selectOne(wrapper);
        List<Dict> children = getChildren(dict, baseMapper.selectList(null));
        //根据dictCode与value值只能查出一条数据
        List<Dict> collect = children.stream().filter(item -> item.getValue() .equals(value) ).collect(Collectors.toList());
        Dict one = collect.get(0);
        return one.getName();
    }


}

