package cn.kmbeast.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookOrderHistory {
    /**
     * 订单历史ID
     */
    private Integer id;

    /**
     * 书籍ID
     */
    private Integer bookId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 借阅期限（天）
     */
    private Integer deadlineNum;

    /**
     * 是否归还 (0:未归还, 1:已归还)
     */
    private Boolean isReturn;

    /**
     * 归还时间
     */
    private LocalDate returnTime;

    /**
     * 创建时间/借阅时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
