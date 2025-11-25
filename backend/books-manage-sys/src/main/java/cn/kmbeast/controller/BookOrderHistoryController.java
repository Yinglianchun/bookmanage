package cn.kmbeast.controller;

import cn.kmbeast.aop.Pager;
import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.BookOrderHistoryQueryDto;
import cn.kmbeast.pojo.entity.BookOrderHistory;
import cn.kmbeast.pojo.vo.BookOrderHistoryVO;
import cn.kmbeast.service.BookOrderHistoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 的 Controller
 */
@RestController
@RequestMapping(value = "/bookOrderHistory")
public class BookOrderHistoryController {

    @Resource
    private BookOrderHistoryService bookOrderHistoryService;

    /**
     * 新增借阅记录
     *
     * @param bookOrderHistory 新增数据
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/save")
    public Result<Void> save(@RequestBody BookOrderHistory bookOrderHistory) {
        return bookOrderHistoryService.save(bookOrderHistory);
    }

    /**
     * 删除
     *
     * @param ids 要删除的ID列表
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/batchDelete")
    public Result<Void> batchDelete(@RequestBody List<Integer> ids) {
        return bookOrderHistoryService.batchDelete(ids);
    }

    /**
     * 修改
     *
     * @param bookOrderHistory 参数
     * @return Result<Void> 响应
     */
    @PutMapping(value = "/update")
    public Result<Void> update(@RequestBody BookOrderHistory bookOrderHistory) {
        return bookOrderHistoryService.update(bookOrderHistory);
    }

    /**
     * 查询
     *
     * @param bookOrderHistoryQueryDto 查询参数
     * @return Result<List < BookOrderHistory>> 通用响应
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<BookOrderHistoryVO>> query(@RequestBody BookOrderHistoryQueryDto bookOrderHistoryQueryDto) {
        return bookOrderHistoryService.query(bookOrderHistoryQueryDto);
    }

    /**
     * 查询
     *
     * @param bookOrderHistoryQueryDto 查询参数
     * @return Result<List < BookOrderHistoryVO>> 通用响应
     */
    @Pager
    @PostMapping(value = "/queryUser")
    public Result<List<BookOrderHistoryVO>> queryUser(@RequestBody BookOrderHistoryQueryDto bookOrderHistoryQueryDto) {
        bookOrderHistoryQueryDto.setUserId(LocalThreadHolder.getUserId());
        return bookOrderHistoryService.query(bookOrderHistoryQueryDto);
    }

}
