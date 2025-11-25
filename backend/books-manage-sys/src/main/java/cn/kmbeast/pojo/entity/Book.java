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
public class Book {
    /*
    * ID
    */
    private Integer id;
    /*
    * 书籍名
    */
    private String name;
    /*
     * 封面
     */
    private String cover;
    /*
     * 出版社
     */
    private String publisher;
    // 作者
    private String author;
    // 索书号
    private String isbn;
    // 数量
    private Integer num;
    // 书籍简介
    private String detail;
    // 书架ID
    private  Integer bookshelfId;
    // 书籍类别ID
    private Integer categoryId;
    // 是否购买
    private Boolean isPlanBuy;
    // 计划购买时间
    private LocalDate planBuyTime;
    // 入库时间
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
