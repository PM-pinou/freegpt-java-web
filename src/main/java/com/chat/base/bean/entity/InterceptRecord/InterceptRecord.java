package com.chat.base.bean.entity.InterceptRecord;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author lixin
 * @since 2023-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("intercept_record")
public class InterceptRecord implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    private String ip;

    private Long userId;

    private LocalDateTime createTime;

    private String source;

    private String reason;

}
