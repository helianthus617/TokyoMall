package com.atguigu.gulimall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 库存工作单
 *
 * @author ****
 * @email none
 * @date 2021-12-16 18:03:43
 */
@Data
@TableName("wms_ware_order_task_detail")
public class WareOrderTaskDetailEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;
    /**
     * 仓库id
     */
    private Long wareId;

    public WareOrderTaskDetailEntity() {
    }

    /**
     * 1-已锁定  2-已解锁  3-扣减
     */
    private Integer lockStatus;

    public WareOrderTaskDetailEntity(Long id, Long skuId, String skuName, Integer skuNum, Long taskId, Long wareId, Integer lockStatus) {
        this.id = id;
        this.skuId = skuId;
        this.skuName = skuName;
        this.skuNum = skuNum;
        this.taskId = taskId;
        this.wareId = wareId;
        this.lockStatus = lockStatus;
    }
}
