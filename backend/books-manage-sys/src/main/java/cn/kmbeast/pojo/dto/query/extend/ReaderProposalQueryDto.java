package cn.kmbeast.pojo.dto.query.extend;

import cn.kmbeast.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReaderProposalQueryDto extends QueryDto {
    private Integer userId;
    private Boolean isPublish;
    private String userName; // 用于模糊搜索用户昵称
    private String content;  // 用于模糊搜索建议内容
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") // 创建时间
    private LocalDateTime createTime;
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") // 回复时间
    //private LocalDateTime replyTime;
}
