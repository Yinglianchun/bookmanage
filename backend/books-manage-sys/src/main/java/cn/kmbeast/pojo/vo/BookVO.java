package cn.kmbeast.pojo.vo;

import cn.kmbeast.pojo.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data


public class BookVO  extends Book {
    // //书架名
    // private String bookshelfName;
    // 书架区域
    private String area;
    // 书架楼层
    private String floor;
    // 书架名称
    private String frame;
    // 书架类别
    private String categoryName;
}
