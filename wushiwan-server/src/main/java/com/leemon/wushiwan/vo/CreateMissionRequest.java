package com.leemon.wushiwan.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.leemon.wushiwan.dto.ExplainData;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateMissionRequest {
    /**
     * 重发任务时，原来的任务ID,重发才需要填
     */
    private Integer missionId;

    /**
     * 发布任务的类型，在属性表中的id
     */
    @NotNull
    private Integer typePropertyId;

    /**
     * 属性表里面的支持设备类型
     */
    @NotNull
    private Integer mobilePropertyId;

    /**
     * 标题，12字以内
     */
    @NotBlank
    @Length(max = 12)
    private String title;

    /**
     * 截止时间
     */
    @NotNull
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadlineTime;

    /**
     * 出价，单位分
     */
    @NotNull
    @Min(1)
    private Integer price;

    /**
     * 任务数量
     */
    @NotNull
    @Min(1)
    private Integer count;

    /**
     * 链接
     */
    @Length(max = 300)
    private String url;

    /**
     * 文字验证
     */
    @Length(max = 500)
    private String textVerify;

    /**
     * 备注
     */
    @Length(max = 200)
    private String remark;

    //审核图样的图片
    @NotNull
    private List<String> sampleImgList;
    //如果是重发任务，这个key也没有
    private String sampleKey;
    //操作说明的图片
    private List<ExplainData> explainList;
    private String explainKey;
}
