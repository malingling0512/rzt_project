package com.mll.mll_project.visitor;

import com.wave.rztilib.DataUtil;
import com.wave.rztilib.ICardInfo;
import com.wave.rztilib.Names;

import java.util.Date;

public class DataObjAdapter {

    public static Visitor convert(ICardInfo cardInfo) {

        Visitor visitor = new Visitor();
        visitor.setName(cardInfo.getName());
        visitor.setCode(cardInfo.getCardId());
        visitor.setAddress(DataUtil.getString(cardInfo, Names.ADDRESS_N, ""));

        visitor.setGender(cardInfo.getGender() == 1 ? 1 : 2);
        visitor.setNation(cardInfo.getNation());
        // 类型为2，表示是身份证
        visitor.setType(2);
        visitor.setState(0);
        visitor.setDepartment(DataUtil.getString(cardInfo, Names.DEPARTMENT_N, ""));
        visitor.setBirthday(new Date(DataUtil.getLong(cardInfo, Names.BIRTHDAY_N, System.currentTimeMillis())));
        visitor.setValidityFrom(new Date(DataUtil.getLong(cardInfo, Names.EFFECTDATE_N, System.currentTimeMillis())));
        visitor.setValidityTo(new Date(DataUtil.getLong(cardInfo, Names.EXPIREDATE_N, System.currentTimeMillis())));

        return visitor;
    }

}
