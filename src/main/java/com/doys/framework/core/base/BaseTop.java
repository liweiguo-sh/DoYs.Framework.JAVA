/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-04-18
 * 框架顶级类
 *****************************************************************************/
package com.doys.framework.core.base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class BaseTop {
    @Value("${global.debug}")
    protected boolean debug;

    protected static Logger logger = LoggerFactory.getLogger("doys");

    // -- close ----------------------------------------------------------------
}