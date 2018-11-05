package com.example.utils.poi;


import com.example.utils.excel.mapper.Mapper;
import com.example.utils.excel.mapper.Mappers;

/**
 * @author zhuangqianliao
 */
public class PolicyMapper extends Mapper<PolicyVO> {

    public static void init() {
        Mappers.registry(new PolicyMapper(0, "文件编号", "policyCode"));
        Mappers.registry(new PolicyMapper(1, "可组文件编号", "combinableDoc"));
        Mappers.registry(new PolicyMapper(2, "航司二字码", "airlineCode"));
        Mappers.registry(new PolicyMapper(3, "同时授权office号", "authorizedOfficeId"));
        Mappers.registry(new PolicyMapper(4, "发票类型", "invoiceDelivery"));
        Mappers.registry(new PolicyMapper(5, "限定farebase", "includeFb"));
        Mappers.registry(new PolicyMapper(6, "排除farebase", "excludeFb"));
        Mappers.registry(new PolicyMapper(7, "工作时间", "serviceTime"));
        Mappers.registry(new PolicyMapper(8, "预定OFFICE号限定", "orderOffice"));
        Mappers.registry(new PolicyMapper(9, "运价类型", "fareType"));
        Mappers.registry(new PolicyMapper(10, "商品类型", "ticketType"));
        Mappers.registry(new PolicyMapper(11, "跨季", "transSeason"));
        Mappers.registry(new PolicyMapper(12, "混舱", "mixCabin"));
        Mappers.registry(new PolicyMapper(13, "回程无奖励", "returnBonus"));
        Mappers.registry(new PolicyMapper(14, "Q值", "qValue"));
        Mappers.registry(new PolicyMapper(15, "乘客类型", "passengerType"));
        Mappers.registry(new PolicyMapper(16, "适用国籍", "includeNation"));
        Mappers.registry(new PolicyMapper(17, "排除国籍", "excludeNation"));
        Mappers.registry(new PolicyMapper(18, "年龄限制", "ageLimit"));
        Mappers.registry(new PolicyMapper(19, "航程类型", "journeyType"));
        Mappers.registry(new PolicyMapper(20, "是否支持缺口", "openJaw"));
        Mappers.registry(new PolicyMapper(21, "出发地", "dep"));
        Mappers.registry(new PolicyMapper(22, "到达地", "arr"));
        Mappers.registry(new PolicyMapper(23, "出发地排除", "excludeDep"));
        Mappers.registry(new PolicyMapper(24, "到达地排除", "excludeArr"));
        Mappers.registry(new PolicyMapper(25, "是否直飞", "directOnly"));
        Mappers.registry(new PolicyMapper(26, "中转点（适用）", "transfer"));
        Mappers.registry(new PolicyMapper(27, "中转点（排除）", "excludeTransfer"));
        Mappers.registry(new PolicyMapper(28, "是否适用联运", "combine"));
        Mappers.registry(new PolicyMapper(29, "可联运航司", "combinedAirline"));
        Mappers.registry(new PolicyMapper(30, "是否适用代码共享", "codeShare"));
        Mappers.registry(new PolicyMapper(31, "代码共享航班", "codeShareAirline"));
        Mappers.registry(new PolicyMapper(32, "去程航班号（适用）", "depAirline"));
        Mappers.registry(new PolicyMapper(33, "回程航班号（适用）", "returnAirline"));
        Mappers.registry(new PolicyMapper(34, "去程航班号（排除）", "excludeDepAirline"));
        Mappers.registry(new PolicyMapper(35, "回程航班号（排除）", "excludeReturnAirline"));
        Mappers.registry(new PolicyMapper(36, "销售日期", "sellDate"));
        Mappers.registry(new PolicyMapper(37, "提前开票天数", "advancedTkt"));
        Mappers.registry(new PolicyMapper(38, "去程旅行日期", "depDate"));
        Mappers.registry(new PolicyMapper(39, "回程旅行日期", "returnDate"));
        Mappers.registry(new PolicyMapper(40, "去程适用班期", "depDayTime"));
        Mappers.registry(new PolicyMapper(41, "回程适用班期", "returnDayTime"));
        Mappers.registry(new PolicyMapper(42, "成人返点", "adtDiscount"));
        Mappers.registry(new PolicyMapper(43, "成人单程留钱", "adtSingleKeep"));
        Mappers.registry(new PolicyMapper(44, "成人往返留钱", "adtRoundKeep"));
        Mappers.registry(new PolicyMapper(45, "儿童返点", "chdDiscount"));
        Mappers.registry(new PolicyMapper(46, "儿童单程留钱", "chdSingleKeep"));
        Mappers.registry(new PolicyMapper(47, "儿童往返留钱", "chdRoundKeep"));
        Mappers.registry(new PolicyMapper(48, "婴儿返点", "infDiscount"));
        Mappers.registry(new PolicyMapper(49, "婴儿单程留钱", "infSingleKeep"));
        Mappers.registry(new PolicyMapper(50, "婴儿往返留钱", "infRoundKeep"));
        Mappers.registry(new PolicyMapper(51, "适用舱位", "suitCabinCode"));
        Mappers.registry(new PolicyMapper(52, "内部备注", "inRemark"));
        Mappers.registry(new PolicyMapper(53, "外部备注", "outRemark"));
        Mappers.registry(new PolicyMapper(54, "错误信息", "errorMsg"));
    }

    public PolicyMapper(int columnIndex, String columnName, String fieldName) {
        super(columnIndex, columnName, fieldName, PolicyVO.class);
    }
}
