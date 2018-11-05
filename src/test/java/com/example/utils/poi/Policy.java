package com.example.utils.poi;

import lombok.Data;

/**
 * @author zhuangqianliao
 */
@Data
public class Policy {

    /**
     * PolicyBasic
     */
    private String policyCode;

    private String combinableDoc;

    private String airlineCode;

    private String authorizedOfficeId;

    private String serviceTime;

    private Integer invoiceDelivery;

    private Integer advancedTkt;

    private Integer orderOffice;

    private Integer journeyType;

    private Integer openJaw;

    private Integer directOnly;

    private Integer combine;

    private String combinedAirline;

    private Integer codeShare;

    private String codeShareAirline;

    private String includeNation;

    private String excludeNation;

    private Integer ticketType;

    private String passengerType;

    private String ageLimit;

    private String inRemark;

    private String outRemark;

    /**
     * PolicyFare
     */
    private Integer transSeason;

    private Integer mixCabin;

    private Integer returnBonus;

    private Integer qValue;

    /**
     * lombok 会自动生成 getQValue(), 导致 OrikaMapper 读取不到对应的字段值
     */
    public Integer getqValue() {
        return qValue;
    }

    public void setqValue(Integer qValue) {
        this.qValue = qValue;
    }

    private String includeFb;

    private String excludeFb;

    private Integer fareType;

    private String adtDiscount;

    private String adtSingleKeep;

    private String adtRoundKeep;

    private String chdDiscount;

    private String chdSingleKeep;

    private String chdRoundKeep;

    private String infDiscount;

    private String infSingleKeep;

    private String infRoundKeep;

    private String suitCabinCode;

    private String sellDate;

    /**
     * PolicyJourney
     */
    private String dep;

    private String excludeDep;

    private String arr;

    private String excludeArr;

    private String depAirline;

    private String excludeDepAirline;

    private String returnAirline;

    private String excludeReturnAirline;

    private String transfer;

    private String excludeTransfer;

    private String depDate;

    private String returnDate;

    private String depDayTime;

    private String returnDayTime;

    /**
     * 错误信息, 需要输出到Excel
     */
    private transient String errorMsg;
}
