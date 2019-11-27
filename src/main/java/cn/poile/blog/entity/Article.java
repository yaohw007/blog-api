package cn.poile.blog.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文章表
 * </p>
 *
 * @author yaohw
 * @since 2019-11-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Article对象", description="文章表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文章id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @JsonIgnore
    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty(value = "作者头像")
    private String avatar;

    @ApiModelProperty(value = "分类名称-冗余字段")
    private String categoryName;

    @ApiModelProperty(value = "文章分类id")
    private Integer categoryId;

    @ApiModelProperty(value = "文章标题")
    private String title;

    @ApiModelProperty(value = "文章摘要")
    private String summary;

    @ApiModelProperty(value = "文章内容")
    private String content;

    @ApiModelProperty(value = "文章封面")
    private String cover;

    @ApiModelProperty(value = "文章状态：0为正常，1为待发布，2为回收站")
    private Integer status;

    @ApiModelProperty(value = "文章浏览次数")
    private Integer viewCount;

    @ApiModelProperty(value = "评论数-冗余字段")
    private Integer commentCount;

    @ApiModelProperty(value = "点赞数-冗余字段")
    private Integer likeCount;

    @ApiModelProperty(value = "发布时间")
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private LocalDateTime publishTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private LocalDateTime updateTime;

    @TableLogic
    @JsonIgnore
    @ApiModelProperty(value = "是否已删除,1:是，0:否")
    private Integer deleted;

    @ApiModelProperty(value = "是否转载,1:是，0:否")
    private Integer reproduce;


}
