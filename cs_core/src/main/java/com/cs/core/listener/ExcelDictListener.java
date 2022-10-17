package com.cs.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.cs.core.mapper.DictMapper;
import com.cs.core.pojo.dto.ExcelDictDTO;
import com.cs.core.pojo.entity.Dict;
import com.cs.core.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ExcelDictListener extends AnalysisEventListener<ExcelDictDTO> {
    private static final int BATCH_COUNT = 10;

    List<ExcelDictDTO> list = new ArrayList<>();

    //private DictService dictService;
    public ExcelDictListener(){}
//    public ExcelDictListener(DictService dictService){
//        this.dictService=dictService;
//    }
    private DictMapper dictMapper;
    public ExcelDictListener(DictMapper dictMapper){
        this.dictMapper=dictMapper;
    }
    @Override
    public void invoke(ExcelDictDTO excelDictDTO, AnalysisContext analysisContext) {
        //将每一个对象存储到list集合中
        list.add(excelDictDTO);
        //当集合中的对象大于这个我们指定的值时,将数据进行插入,插入到数据库中,并且清空list
        if (list!=null && list.size() > BATCH_COUNT) {
            saveData();
        }
        log.info("读取数据:{}", excelDictDTO);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //当我们的剩余数据不足100并且list的size大于0时,我们要将剩余的数据进行存储
        if (list!=null && list.size()>0){
            saveData();
        }
        log.info("数据倒入成功");
    }
    //保存数据的方法
    private void saveData(){

//        List<Dict> DictList = list.stream().map(iter -> {
//            Dict dict = new Dict();
//            BeanUtils.copyProperties(iter, dict);
//            return dict;
//        }).collect(Collectors.toList());
        //使用Mybatis-plus的批量插入方法时,不会将主键定义为type = IdType.AUTO的主键插入但是我们在新增时想让主键自增
        // ,所以我们将此方法自己定义在xml中
//        dictService.saveBatch(DictList);
        dictMapper.saveBatch(list);
        list.clear();
    }

}
