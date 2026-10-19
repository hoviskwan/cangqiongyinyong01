package kd.bos.app.bnuy.app.demo01.operate;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.AddValidatorsEventArgs;
import kd.bos.entity.plugin.PreparePropertysEventArgs;
import kd.bos.entity.validate.AbstractValidator;

/**
 * 销售报价管理（bnuy_sales_quotation）保存操作插件。
 *
 * 业务触发：保存销售报价单时的服务端校验。
 * 核心逻辑：保存时如果明细分录中没有任何产品行，抛出阻断错误，提示至少录入一行报价明细。
 * 该插件仅绑定 save 操作，校验随 onAddValidators 在保存流程中执行。
 */
public class BnuySalesQuotationOpPlugin extends AbstractOperationServicePlugIn {

    /** 明细分录集合属性（分录实体标识）。 */
    private static final String ENTRY_ENTITY = "bnuy_sales_quota_entry";
    /** 明细产品字段（物料基础资料引用）。 */
    private static final String ENTRY_PRODUCT = "bnuy_product";
    /** 无产品行时的阻断提示。 */
    private static final String NO_ENTRY_MESSAGE = "至少录入一行报价明细";

    @Override
    public void onPreparePropertys(PreparePropertysEventArgs e) {
        super.onPreparePropertys(e);
        // 声明明细分录，保证保存数据包中加载分录及其产品字段
        e.getFieldKeys().add(ENTRY_ENTITY);
    }

    @Override
    public void onAddValidators(AddValidatorsEventArgs e) {
        super.onAddValidators(e);
        // 保存校验：明细分录中必须存在至少一行已选产品的报价明细
        e.addValidator(new AbstractValidator() {
            @Override
            public void validate() {
                for (ExtendedDataEntity billEntity : this.getDataEntities()) {
                    DynamicObject bill = billEntity.getDataEntity();
                    DynamicObjectCollection entries = bill.getDynamicObjectCollection(ENTRY_ENTITY);
                    if (!hasProductRow(entries)) {
                        this.addErrorMessage(billEntity, NO_ENTRY_MESSAGE);
                    }
                }
            }
        });
    }

    /** 明细分录中是否存在至少一行已选产品。 */
    private static boolean hasProductRow(DynamicObjectCollection entries) {
        if (entries == null) {
            return false;
        }
        for (DynamicObject entry : entries) {
            if (entry.getDynamicObject(ENTRY_PRODUCT) != null) {
                return true;
            }
        }
        return false;
    }
}
