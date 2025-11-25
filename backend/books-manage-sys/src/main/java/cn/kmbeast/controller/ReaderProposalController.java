package cn.kmbeast.controller;

import cn.kmbeast.aop.Pager;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.ReaderProposalQueryDto;
import cn.kmbeast.pojo.entity.ReaderProposal;
import cn.kmbeast.pojo.vo.ReaderProposalVO;
import cn.kmbeast.service.ReaderProposalService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 留言的 Controller
 */
@RestController
@RequestMapping(value = "/readerProposal")
public class ReaderProposalController {

    @Resource
    private ReaderProposalService readerProposalService;

    /**
     * 留言新增
     *
     * @param readerProposal 新增数据
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/save")
    public Result<Void> save(@RequestBody ReaderProposal readerProposal) {
        return readerProposalService.save(readerProposal);
    }

    /**
     * 留言删除
     *
     * @param ids 要删除的留言ID列表
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/batchDelete")
    public Result<Void> batchDelete(@RequestBody List<Integer> ids) {
        return readerProposalService.batchDelete(ids);
    }

    /**
     * 留言修改
     *
     * @param readerProposal 参数
     * @return Result<Void> 响应
     */
    @PutMapping(value = "/update")
    public Result<Void> update(@RequestBody ReaderProposal readerProposal) {
        return readerProposalService.update(readerProposal);
    }

    /**
     * 留言查询
     *
     * @param readerProposalQueryDto 查询参数
     * @return Result<List < ReaderProposalVO>> 通用响应
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<ReaderProposalVO>> query(@RequestBody ReaderProposalQueryDto readerProposalQueryDto) {
        return readerProposalService.query(readerProposalQueryDto);
    }

}
