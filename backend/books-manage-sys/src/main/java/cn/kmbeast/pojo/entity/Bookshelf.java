package cn.kmbeast.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bookshelf {
    /*
    * 书架ID
    */
    private Integer id;
    /*
    * 书架楼层
    */
    private String floor;
    /*
     * 书架所在区域
     */
    private String area;
    /*
     * 书架名
     */
    private String frame;

}
