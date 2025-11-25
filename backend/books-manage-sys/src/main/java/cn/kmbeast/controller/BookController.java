package cn.kmbeast.controller;

import cn.kmbeast.aop.Pager;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.BookQueryDto;
import cn.kmbeast.pojo.entity.Book;
import cn.kmbeast.pojo.vo.BookVO;
import cn.kmbeast.service.BookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公告的 Controller
 */
@RestController
@RequestMapping(value = "/book")
public class BookController {

    @Resource
    private BookService bookService;

    /**
     * 公告新增
     *
     * @param book 新增数据
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/save")
    public Result<Void> save(@RequestBody Book book) {
        return bookService.save(book);
    }

    /**
     * 公告删除
     *
     * @param ids 要删除的公告ID列表
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/batchDelete")
    public Result<Void> batchDelete(@RequestBody List<Integer> ids) {
        return bookService.batchDelete(ids);
    }

    /**
     * 公告修改
     *
     * @param book 参数
     * @return Result<Void> 响应
     */
    @PutMapping(value = "/update")
    public Result<Void> update(@RequestBody Book book) {
        return bookService.update(book);
    }

    /**
     * 公告查询
     *
     * @param bookQueryDto 查询参数
     * @return Result<List < BookVO>> 通用响应
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<BookVO>> query(@RequestBody BookQueryDto bookQueryDto) {
        return bookService.query(bookQueryDto);
    }

}
