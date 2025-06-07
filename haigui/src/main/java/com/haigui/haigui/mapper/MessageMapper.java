package com.haigui.haigui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haigui.haigui.model.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
} 