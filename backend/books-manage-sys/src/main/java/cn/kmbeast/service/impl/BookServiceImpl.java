package cn.kmbeast.service.impl;

import cn.kmbeast.mapper.BookMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.PageResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.BookQueryDto;
import cn.kmbeast.pojo.entity.Book;
import cn.kmbeast.pojo.vo.BookVO;
import cn.kmbeast.service.BookService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告业务逻辑实现
 */
@Service
public class BookServiceImpl implements BookService {

    @Resource
    private BookMapper bookMapper;

    /**
     * 公告新增
     *
     * @param book 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(Book book) {
        book.setCreateTime(LocalDateTime.now());
        bookMapper.save(book);
        return ApiResult.success();
    }

    /**
     * 公告删除
     *
     * @param ids 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        bookMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 公告修改
     *
     * @param book 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> update(Book book) {
        System.out.println(book);
        bookMapper.update(book);
        return ApiResult.success();
    }

    // /**
    //  * 公告查询
    //  *
    //  * @param bookQueryDto 查询参数
    //  * @return Result<List < Book>>
    //  */
    // @Override
    // public Result<List<BookVO>> query(BookQueryDto bookQueryDto) {
    //     List<BookVO> bookList = bookMapper.query(bookQueryDto);
    //     Integer totalCount = bookMapper.queryCount(bookQueryDto);
    //     return PageResult.success(bookList, totalCount);
    // }
    @Override
    public Result<List<BookVO>> query(BookQueryDto bookQueryDto) {
        System.out.println("=== 开始查询书籍 ===");
        System.out.println("查询参数: " + bookQueryDto);
        
        try {
            System.out.println("准备执行数据库查询...");
            List<BookVO> bookList = bookMapper.query(bookQueryDto);
            System.out.println("数据库查询完成，结果数量: " + (bookList != null ? bookList.size() : "null"));
            
            System.out.println("准备查询总数...");
            Integer totalCount = bookMapper.queryCount(bookQueryDto);
            System.out.println("总记录数查询完成: " + totalCount);
            
            if (bookList != null && !bookList.isEmpty()) {
                System.out.println("第一条记录: " + bookList.get(0));
            }
            
            System.out.println("准备返回结果...");
            Result<List<BookVO>> result = PageResult.success(bookList, totalCount);
            System.out.println("结果创建完成，返回中...");
            return result;
            
        } catch (Exception e) {
            System.err.println("查询异常: " + e.getMessage());
            System.err.println("异常类型: " + e.getClass().getName());
            e.printStackTrace();
            throw e;
        }
    }

}
