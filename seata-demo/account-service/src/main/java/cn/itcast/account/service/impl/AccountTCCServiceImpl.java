package cn.itcast.account.service.impl;

import cn.itcast.account.entity.AccountFreeze;
import cn.itcast.account.mapper.AccountFreezeMapper;
import cn.itcast.account.mapper.AccountMapper;
import cn.itcast.account.service.AccountTCCService;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountTCCServiceImpl implements AccountTCCService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private AccountFreezeMapper accountFreezeMapper;

    @Override
    public void deduct(String userId, int money) {

        AccountFreeze accountFreeze1 = accountFreezeMapper.selectById(RootContext.getXID());
        if (accountFreeze1!=null){
            return;
        }

        accountMapper.deduct(userId,money);
        AccountFreeze accountFreeze = new AccountFreeze();
        accountFreeze.setUserId(userId);
        accountFreeze.setFreezeMoney(money);
        accountFreeze.setState(AccountFreeze.State.TRY);
        accountFreeze.setXid(RootContext.getXID());
        accountFreezeMapper.insert(accountFreeze);
    }

    @Override
    public boolean confirm(BusinessActionContext context) {
        int i = accountFreezeMapper.deleteById(context.getXid());
        return i==1;
    }

    @Override
    public boolean cancel(BusinessActionContext context) {
        AccountFreeze accountFreeze = accountFreezeMapper.selectById(context.getXid());

        if (accountFreeze==null){
            accountFreeze = new AccountFreeze();
            accountFreeze.setUserId(context.getActionContext("userId").toString());
            accountFreeze.setFreezeMoney(0);
            accountFreeze.setState(AccountFreeze.State.CANCEL);
            accountFreeze.setXid(RootContext.getXID());
            return true;
        }


        if(accountFreeze.getState()==AccountFreeze.State.CANCEL){
            return true;
        }

        accountMapper.refund(accountFreeze.getUserId(),accountFreeze.getFreezeMoney());
        accountFreeze.setFreezeMoney(0);
        accountFreeze.setState(AccountFreeze.State.CANCEL);
        int i = accountFreezeMapper.updateById(accountFreeze);

        return i==1;
    }
}
