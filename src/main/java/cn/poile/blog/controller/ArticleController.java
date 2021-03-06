package cn.poile.blog.controller;


import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.common.validator.annotation.YearMonthFormat;
import cn.poile.blog.controller.model.request.ArticleRequest;
import cn.poile.blog.service.ArticleRecommendService;
import cn.poile.blog.service.IArticleService;
import cn.poile.blog.vo.ArticleArchivesVo;
import cn.poile.blog.vo.ArticleCategoryStatisticsVo;
import cn.poile.blog.vo.ArticleTagStatisticsVo;
import cn.poile.blog.vo.ArticleVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 文章表 前端控制器
 * </p>
 *
 * @author yaohw
 * @since 2019-11-15
 */
@RestController
@RequestMapping("/article")
@Api(tags = "文章服务", value = "/article")
public class ArticleController extends BaseController {

    @Autowired
    private IArticleService articleService;

    @Autowired
    private ArticleRecommendService articleRecommendService;


    @PostMapping("/save")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "保存文章", notes = "需要accessToken，需要管理员权限")
    public ApiResponse save(@Validated @RequestBody ArticleRequest request) {
        Integer id = articleService.saveOrUpdate(request);
        articleRecommendService.remove(id);
        return createResponse(id);
    }


    @GetMapping("/published/page")
    @ApiOperation(value = "分页获取文章(已发布)", notes = "用于前台页面展示,默认按发布时间倒序排序")
    public ApiResponse<IPage<ArticleVo>> publishedPage(@ApiParam("当前页，默认值：1") @RequestParam(value = "current", required = false, defaultValue = "1") long current,
                                                       @ApiParam("每页数量，默认值为：5") @RequestParam(value = "size", required = false, defaultValue = "5") long size,
                                                       @ApiParam("分类id，非必传") @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                       @ApiParam("标签id，非必传") @RequestParam(value = "tagId", required = false) Integer tagId,
                                                       @ApiParam("年月,非必传,格式:yyyy-mm") @YearMonthFormat @RequestParam(value = "yearMonth", required = false) String yearMonth,
                                                       @ApiParam("标题关键字，非必传") @RequestParam(value = "title", required = false) String title,
                                                       @ApiParam("排序字段，倒序，非必传，默认:publish_time;可选项：发布时间:publish_time、热度:hot")
                                                       @RequestParam(value = "orderBy", required = false, defaultValue = "publish_time") String orderBy
    ) {
        return createResponse(articleService.selectPublishedArticleVoPage(current, size, categoryId, tagId, yearMonth, title, orderBy));
    }


    @GetMapping("/page")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "后台管理分页获取文章", notes = "可以查询所有状态的文章，用于后台管理，需要accessToken，需要管理员权限")
    public ApiResponse<IPage<ArticleVo>> page(@ApiParam("当前页") @RequestParam(value = "current", required = false, defaultValue = "1") long current,
                                              @ApiParam("每页数量") @RequestParam(value = "size", required = false, defaultValue = "5") long size,
                                              @ApiParam("文章状态,非必传，不传查全部；0:已发布，1:未发布，2:回收站") @RequestParam(value = "status", required = false) Integer status,
                                              @ApiParam("分类id，非必传") @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                              @ApiParam("标签id，非必传") @RequestParam(value = "tagId", required = false) Integer tagId,
                                              @ApiParam("年月,非必传") @YearMonthFormat @RequestParam(value = "yearMonth", required = false) String yearMonth,
                                              @ApiParam("标题关键字，可空") @RequestParam(value = "title", required = false) String title) {
        return createResponse(articleService.selectArticleVoPage(current, size, status, title, categoryId, tagId, yearMonth));
    }

    @DeleteMapping("/discard/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "丢弃文章(回收站)", notes = "需要accessToken，需要管理员权限")
    public ApiResponse discard(@ApiParam("文章id") @PathVariable("id") int id) {
        articleService.discard(id);
        articleRecommendService.remove(id);
        return createResponse();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "删除文章", notes = "逻辑删除，需要accessToken，需要管理员权限")
    public ApiResponse delete(@ApiParam("文章id") @PathVariable("id") int id) {
        articleService.delete(id);
        articleRecommendService.remove(id);
        return createResponse();
    }

    @GetMapping("/detail/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "后台管理，获取文章详情信息", notes = "需要accessToken,用于后台文章管理，比列表返回的多一个文章内容，文章分类列表")
    public ApiResponse<ArticleVo> detail(@ApiParam("文章id") @PathVariable("id") int id) {
        return createResponse(articleService.selectArticleVoById(id));
    }

    @GetMapping("/view/{id}")
    @ApiOperation(value = "获取文章详情信息", notes = "比列表返回的多一个文章内容，文章分类列表")
    public ApiResponse<ArticleVo> view(@ApiParam("文章id") @PathVariable("id") int id) {
        ArticleVo articleVo = articleService.selectOne(id);
        return createResponse(articleVo);
    }

    @PutMapping("/increment_view/{id}")
    @ApiOperation(value = "新增浏览次数", notes = "20分钟内ip或用户浏览计数")
    public ApiResponse incrementView(@ApiParam("文章id") @PathVariable("id") int id) {
        boolean viewed = articleService.incrementView(id);
        if (viewed) {
            articleRecommendService.asyncRefresh(id);
        }
        return createResponse(viewed);
    }


    @GetMapping("/archives/page")
    @ApiOperation(value = "文章归档分页查询", notes = "按年月归档，月份文章计数")
    public ApiResponse<IPage<ArticleArchivesVo>> archives(
            @ApiParam("当前页,非必传，默认为:1") @RequestParam(value = "current", required = false, defaultValue = "1") long current,
            @ApiParam("每页数量,非必传，默认为:12") @RequestParam(value = "size", required = false, defaultValue = "12") long size) {
        return createResponse(articleService.selectArticleArchives(current, size));
    }

    @GetMapping("/category/statistic")
    @ApiOperation(value = "文章分类统计", notes = "按分类计数文章数")
    public ApiResponse<List<ArticleCategoryStatisticsVo>> categoryStatistic() {
        return createResponse(articleService.selectCategoryStatistic());
    }

    @GetMapping("/tag/statistic")
    @ApiOperation(value = "文章标签统计", notes = "按标签计数文章数")
    public ApiResponse<List<ArticleTagStatisticsVo>> tagStatistic() {
        return createResponse(articleService.selectTagStatistic());
    }

    @PostMapping("/recommend/save")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "添加到推荐，如果已存在则更新", notes = "需要accessToken，需要管理员权限")
    public ApiResponse recommendAdd(@ApiParam("文章id") @NotNull(message = "文章id不能为空") @RequestParam(value = "articleId") Integer articleId,
                                    @ApiParam("分数，分数越高越排前面") @RequestParam(value = "score", required = false, defaultValue = "0") Double score) {
        articleRecommendService.add(articleId, score);
        return createResponse();
    }


    @GetMapping("/recommend/list")
    @ApiOperation(value = "获取文章推荐列表", notes = "按分数排序")
    public ApiResponse<List<ArticleVo>> recommendList() {
        return createResponse(articleRecommendService.list());
    }


    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/recommend/delete/{articleId}")
    @ApiOperation(value = "从推荐列表中删除", notes = "需要accessToken，需要管理员权限")
    public ApiResponse recommendDelete(@ApiParam("文章id") @NotNull(message = "文章id不能为空") @PathVariable(value = "articleId") Integer articleId) {
        articleRecommendService.remove(articleId);
        return createResponse();
    }

    @GetMapping("/interrelated/list")
    @ApiOperation(value = "相关文章", notes = "根据分类查询，分类为空则根据标签查询")
    public ApiResponse<List<ArticleVo>> interrelated(@ApiParam("文章id") @NotNull(message = "文章id不能为空") @RequestParam(value = "articleId") Integer articleId,
                                                     @ApiParam("数量") @RequestParam(value = "limit", required = false, defaultValue = "5") Long limit
    ) {
        return createResponse(articleService.selectInterrelatedById(articleId, limit));
    }

    @GetMapping("/count")
    @ApiOperation(value = "已发布文章总数")
    public ApiResponse<Integer> count() {
        return createResponse(articleService.count());
    }

    @PostMapping("/status/update")
    @ApiOperation(value = "修改文章发布或保存状态",notes = "需要accessToken，需要管理员权限")
    public ApiResponse status(
            @ApiParam("文章id") @NotNull(message = "文章id不能为空") @RequestParam("articleId") Integer articleId,
            @ApiParam("文章状态，0为正常，1为待发布，2为回收站") @NotNull(message = "文章状态不能为空") @RequestParam("status") Integer status
    ) {
        articleService.updateStatus(articleId, status);
        articleRecommendService.asyncRefresh(articleId);
        return createResponse();
    }

}
