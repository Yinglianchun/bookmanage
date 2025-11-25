package cn.kmbeast.mapper;

import cn.kmbeast.pojo.dto.query.extend.BookQueryDto;
import cn.kmbeast.pojo.entity.Book;
import cn.kmbeast.pojo.vo.BookVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告持久化接口
 */
@Mapper
public interface BookMapper {

    void save(Book book);

    void update(Book book);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    List<BookVO> query(BookQueryDto bookQueryDto);

    Integer queryCount(BookQueryDto bookQueryDto);

}
