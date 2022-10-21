package com.cs;

import com.cs.common.exception.Assert;
import com.cs.common.result.ResponseEnum;
import org.junit.Test;

public class PasswordTest {
    @Test
    public void trst(){
        Assert.isPattern("\\w{6,24}","123c_s456", ResponseEnum.PASSWORD_NOT_PATTERN);
        Assert.isPattern("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$","1397368928@qq.com",ResponseEnum.EMAIL_ERROR);
    }
}
