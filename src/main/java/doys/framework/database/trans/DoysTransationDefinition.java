/******************************************************************************
 * Copyright (C), 2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2021-08-20
 * TransationDefinition示例获取静态类
 *****************************************************************************/
package doys.framework.database.trans;
import org.springframework.transaction.TransactionDefinition;
public final class DoysTransationDefinition implements TransactionDefinition {
    private static TransactionDefinition tDefDefault;

    // ------------------------------------------------------------------------
    public static TransactionDefinition withDefault() {
        if (tDefDefault == null) {
            tDefDefault = new TransactionDefinitionDefault();
        }
        return tDefDefault;
    }
}