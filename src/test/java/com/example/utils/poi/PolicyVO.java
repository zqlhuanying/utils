package com.example.utils.poi;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhuangqianliao
 */
@Data
public class PolicyVO {

    /**
     * common fileld
     */
    private Integer venderId;
    
    private Date createdTime;

    private String createdBy;

    private Date lastModifiedTime;

    private String lastModifiedBy;

    private Integer indicator;

    private Integer version;
    
    /**
     * PolicyBasic
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @JSONField(serializeUsing =com.alibaba.fastjson.serializer.ToStringSerializer.class)
    private Long policyId;
    
    private Integer policyStatus;

    private Integer policyVersion;

    @Length(max = 50)
    @DigitsCharSequences(message = "只能输入数字和英文字符")
    private String policyCode;

    @Length(max = 50)
    @OnlyInSequence(patterns = {DIGITS_CHAR_SEQUENCES , "/"}, message = "只能输入英文字符/数字/斜杆(/)")
    private String combinableDoc;

    @NotBlank(message = "航司二字码不能为空")
    @Length(min = 2, max = 2, message = "只能输入一个航司")
    @DigitsCharSequences(message = "只能输入数字和英文字符")
    private String airlineCode;

    @NotBlank(message = "开票OFFICE号不能为空", groups = {ValidationGroups.AddPolicy.class})
    private String tktOfficeId;

    @Pattern(regexp = "^[A-Z]{3}[0-9]{3}", message = "只能输入大写英文和数字,形如:ABC456")
    private String authorizedOfficeId;

    private String serviceTime;

    @NotNull(message = "发票类型不能为空")
    @InEnum(enumClazz = InvoiceDeliveryEnum.class, key = "key")
    private Integer invoiceDelivery;

    @Range(min = 0, max = 365)
    private Integer advancedTkt;

    @NotNull(message = "预定OFFICE号限定不能为空")
    @InEnum(enumClazz = OrderOfficeEnum.class, key = "key")
    private Integer orderOffice;

    @NotNull(message = "航程类型不能为空")
    @InEnum(enumClazz = IBEJourneyTypeEnum.class, key = "key")
    private Integer journeyType;

    @NotNull(message = "是否支持缺口不能为空")
    @InEnum(enumClazz = OpenjawEnum.class, key = "key")
    private Integer openJaw;

    @NotNull(message = "是否直飞不能为空")
    @InEnum(enumClazz = DirectOnlyEnum.class, key = "key")
    private Integer directOnly;

    @NotNull(message = "是否适用联运不能为空")
    @InEnum(enumClazz = EitherOrEnum.class, key = "key")
    private Integer combine;

    @Separator(maxSize = 100, pattern = AIRLINE, message = "格式错误(不要以/结尾), 形如：MU/CZ/KE")
    private String combinedAirline;

    @NotNull(message = "是否适用代码共享不能为空")
    @InEnum(enumClazz = EitherOrEnum.class, key = "key")
    private Integer codeShare;

    private String codeShareAirline;

    @Separator(maxSize = 20, pattern = COUNTRY, message = "格式错误(不要以/结尾)，形如：CN/JP")
    private String includeNation;

    @Separator(maxSize = 20, pattern = COUNTRY, message = "格式错误(不要以/结尾)，形如：CN/JP")
    private String excludeNation;

    @NotNull(message = "商品类型不能为空")
    @InEnum(enumClazz = IssueTicketTypeEnum.class, key = "key")
    private Integer ticketType;

    @NotBlank(message = "乘客类型不能为空")
/*    @InEnum(enumClazz = PassengerTypeEnum.class, key = "key")*/
    private String passengerType;

    @Separator(maxSize = 2, separator = "-", pattern = "\\d*", message = "格式错误: 形如18-25")
    private String ageLimit;

    @Length(max = 500)
    private String inRemark;

    @Length(max = 500)
    private String outRemark;

    /**
     * PolicyFare
     */
    @NotNull(message = "跨季不能为空")
    @InEnum(enumClazz = DiscountEnum.class, key = "key")
    private Integer transSeason;

    @NotNull(message = "混舱不能为空")
    @InEnum(enumClazz = DiscountEnum.class, key = "key")
    private Integer mixCabin;

    @NotNull(message = "回程无奖励不能为空")
    @InEnum(enumClazz = ReturnBonusEnum.class, key = "key")
    private Integer returnBonus;

    @NotNull(message = "Q值不能为空")
    @InEnum(enumClazz = QValueEnum.class, key = "key")
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

    @Length(max = 50)
    @OnlyInSequence(patterns = {DIGITS, UPPER, "\\*"}, message = "只能输入大写英文字符/数字/*")
    private String includeFb;

    @Length(max = 50)
    @OnlyInSequence(patterns = {DIGITS, UPPER, "\\*"}, message = "只能输入大写英文字符/数字/*")
    private String excludeFb;

    @NotNull(message = "运价类型不能为空")
    @InEnum(enumClazz = FareTypeEnum.class, key = "key")
    private Integer fareType;

    @Range(min = -100, max = 100)
    private BigDecimal adtDiscount;

    private BigDecimal adtSingleKeep;

    private BigDecimal adtRoundKeep;

    private BigDecimal chdDiscount;

    private BigDecimal chdSingleKeep;

    private BigDecimal chdRoundKeep;

    private BigDecimal infDiscount;

    private BigDecimal infSingleKeep;

    private BigDecimal infRoundKeep;

    @Separator(maxSize = 26, pattern = CABIN, message = "格式错误(不用以/结尾), 形如：A/B/C/D")
    private String suitCabinCode;

    @NotBlank(message = "销售日期不能为空")
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

    @Separator(pattern = CITY, message = "格式错误(不要以/结尾), 形如: TYO/SEL")
    private String transfer;

    @Separator(pattern = CITY, message = "格式错误(不要以/结尾), 形如: TYO/SEL")
    private String excludeTransfer;

    @NotBlank(message = "去程旅行日期不能为空")
    private String depDate;

    @NotBlank(message = "回程旅行日期不能为空")
    private String returnDate;

    private String depDayTime;

    private String returnDayTime;

    /**
     * 错误信息, 需要输出到Excel
     */
    private transient String errorMsg;
}
