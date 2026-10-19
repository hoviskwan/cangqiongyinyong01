# Demo应用01 — 开发实现交付摘要

<!-- generated-development-summary:v1 -->

销售报价管理单据已在 Demo应用01(bnuy_app_demo01) 下完成开发：表头六字段（客户、报价日期、销售员、币别、有效期、报价状态）与明细五字段（产品、数量、单价、折扣率、报价金额）结构、必填、默认值、下拉选项均按需求配置；报价金额按“数量 × 单价 × 折扣率”自动计算；保存校验覆盖必填、有效期不早于报价日期、折扣率 0~1、数量单价非负、明细至少一行（操作插件实现并已注册）；“销售报价管理”菜单已发布且应用已启用。

## 实现与检查

| 本次范围 | 实现证据 |
| --- | --- |
| 新建“销售报价管理”单据，表头包含六个字段：客户（基础资料）、报价日期（日期）、销售员（人员）、币别（下拉）、有效期（日期）、报价状态（下拉） | bnuy_sales_quotation 表头六字段已就位并回读：客户(CustomerField)、报价日期(DateField)、销售员(UserField)、币别(ComboField 下拉)、有效期(DateField)、报价状态(ComboField 下拉) |
| 单据明细包含五个字段：产品（基础资料）、数量（小数）、单价（小数）、折扣率（小数）、报价金额（小数） | 明细分录 bnuy_sales_quota_entry 五字段已就位并回读：产品(MaterielField 引用 bd_material)、数量(DecimalField)、单价(PriceField)、折扣率(DecimalField)、报价金额(AmountField) |
| 报价金额计算口径：数量 × 单价 × 折扣率（折扣率取 0~1 小数） | bizRule FormulaAction 已写入（ruleId 64L8/0V95NZM，平台返回添加成功）：报价金额 = 数量 × 单价 × 折扣率，触发字段为数量/单价/折扣率 |
| 报价状态下拉选项：未提交 / 已提交 / 已审核 | 报价状态 ComboField 选项经平台写入确认：未提交(draft)/已提交(submitted)/已审核(audited)，默认值“未提交”属性写入已验证 |
| 开发完成后发布“销售报价管理”菜单，授权用户可从菜单进入单据 | queryMenuTree 回读：页面菜单“销售报价管理”(menuId 64=4KVE515UH) 绑定 bnuy_sales_quotation、visible=1、位于应用根节点；getApp 回读应用 deploystatus=1 已启用 |
| 标准苍穹单据规则：手工单据编码，保存→提交→审核生命周期；不把自动编号当作默认承诺 | 单据基于 bos_billtpl 标准模板：手工单据编号，保存/提交/审核标准生命周期按钮(bar_save/bar_submit/bar_audit)已在工具栏就位 |
| 默认值：报价日期默认当天、销售员默认当前用户、折扣率默认 1、报价状态默认“未提交” | setProperties 已验证：报价日期默认当天(getToday)、销售员默认当前登录人(getCurrentUser)、折扣率默认 1、报价状态默认未提交 |
| 校验规则：表头主要字段（客户、报价日期、销售员、币别、报价状态）必填；有效期不得早于报价日期；折扣率取值 0~1；数量、单价不得为负；明细至少一行产品 | save 操作校验已写入并验证：MustInputValidation 启用(Id RS=8GBXGZY4)、有效期不得早于报价日期、折扣率 0~1、数量与单价非负；明细至少一行由已注册操作插件 BnuySalesQuotationOpPlugin(save) 实现，注册回读 verified=true |

## 未完成及未验证范围

- 币别采用平台标准币别字段（CurrencyField，关联 bd_currency），界面表现为下拉选择，替代写死选项的下拉：币别按用户原始“下拉”要求以标准下拉选项(人民币/美元/港元/欧元/日元/英镑)实现，未将既有 ComboField 迁移为关联 bd_currency 的平台标准币别字段；当前环境无字段类型迁移通道，迁移需删除重建并有控件引用丢失风险，故保留下拉实现
- 明细补充“单位”字段（UnitField，计量单位 bd_measureunits），与数量 QtyField 配套使用：No implementation evidence supplied
- 选择产品后自动带出该产品的计量单位：No implementation evidence supplied
- 1) 本地未配置 Java Gradle 编译工程，插件源码经平台注册成功（回读 verified）但 javac 未运行（compile=false 按配置跳过），源码可编译性未经编译器证明；插件 activation.runtimeVerified=false，保存拦截的实际生效需在目标环境验证。2) 演示云后端完整元数据读取接口受限：报价状态与币别选项、计算规则的平台写入均已返回成功，但独立回读无法核对，运行态显示（下拉选项、金额刷新、校验提示）尚未验证。3) 数量字段为 DecimalField（满足小数要求），未配套计量单位字段；产品暂未配置选择后带出单位。4) 以上运行态项目留待质量阶段按 AC 实际打开单据验证。
配置回读仅证明对应配置已写入。保存、持久化、提交、审核等运行态行为须由质量阶段按实际观察验收。

## 测试环境交接

```json
{
  "schemaVersion": "cosmic-test-environment/v1",
  "baseUrl": "http://127.0.0.1:8080/ierp",
  "accountId": "1565321489509515264",
  "source": "app-build verified target",
  "application": {
    "appRoot": "/Users/kingdee/lingeebuild/workspaces/4cdb6fbdbc8f7607774bbab0e72367d6f90eae882d154fd177ee81bec1449958/苍穹技术栈应用开发/.lingee-task-worktrees/wi-t1000009/app/bnuy_app_demo01",
    "appCode": "bnuy_app_demo01",
    "appName": "Demo应用01",
    "appId": "60SQ+161S78N"
  },
  "forms": [
    {
      "formNumber": "bnuy_sales_quotation",
      "formName": "销售报价管理",
      "previewUrl": "http://127.0.0.1:8080/ierp/?formId=bnuy_sales_quotation",
      "listUrl": "http://127.0.0.1:8080/ierp/?formId=bos_list&type=list&billFormId=bnuy_sales_quotation"
    }
  ],
  "requirementsBaseline": {
    "path": "requirements.md",
    "sha256": "sha256:243a476b0392611da4b603db0de9522066e31ad4acda90afd948294b044808c6",
    "revision": 3,
    "itemHashes": {
      "R-001": "sha256:60eb42cef27712bc066e9522d0e839a88252068b5ad7c1a010aea2025de46f79",
      "R-002": "sha256:46a9949ed750b92aa8664f46f469308295ccc9fd40573d93ea0f206e33ad523d",
      "R-003": "sha256:68c95bbffb886a65f22150e6d8da5419e3a1081f44002779230480bd73264719",
      "R-004": "sha256:6d2e4fb27bd22700f6ce210c56a3c5b260e2c9451ff6fe618445bc3b10e17ae3",
      "R-005": "sha256:70f7f25469918dee199b9b7ebdb0e890f72b000c0e4cf7133372f5b1dde90973",
      "E-006": "sha256:9dae67fa53f659afa090f6a7f9cf539860b2d855b6b6eeb0a0b43aa2569fcccd",
      "E-001": "sha256:3094cf4cb054a3b69acee7e0c61578e5a3ea5493cec24f5d04f440bfaad0065c",
      "E-002": "sha256:c33749af3b646a075d862ef72bf12708aa57061921cb347155ed27f4ed244e8b",
      "E-003": "sha256:dba5e04390ac96711468e9589524c37b78e1df707eb6f9d7c6e609790f8dffe8",
      "E-004": "sha256:2aa022354cb5fbf19cc792e0dacc311985ac22d88685b948b4ea8bc520660a06",
      "E-005": "sha256:423c0cc6edc3cf42705b66517f4beb26c08da25278e3fe612b93af675276133f"
    },
    "sourceContentHash": "sha256:70fdb9405a19b0d013106f7bf2e632511c45bbd2c1329517e9fc57efa2cd0591"
  },
  "deliveryScope": {
    "completed": [
      {
        "itemId": "R-001",
        "evidence": [
          "bnuy_sales_quotation 表头六字段已就位并回读：客户(CustomerField)、报价日期(DateField)、销售员(UserField)、币别(ComboField 下拉)、有效期(DateField)、报价状态(ComboField 下拉)"
        ]
      },
      {
        "itemId": "R-002",
        "evidence": [
          "明细分录 bnuy_sales_quota_entry 五字段已就位并回读：产品(MaterielField 引用 bd_material)、数量(DecimalField)、单价(PriceField)、折扣率(DecimalField)、报价金额(AmountField)"
        ]
      },
      {
        "itemId": "R-003",
        "evidence": [
          "bizRule FormulaAction 已写入（ruleId 64L8/0V95NZM，平台返回添加成功）：报价金额 = 数量 × 单价 × 折扣率，触发字段为数量/单价/折扣率"
        ]
      },
      {
        "itemId": "R-004",
        "evidence": [
          "报价状态 ComboField 选项经平台写入确认：未提交(draft)/已提交(submitted)/已审核(audited)，默认值“未提交”属性写入已验证"
        ]
      },
      {
        "itemId": "R-005",
        "evidence": [
          "queryMenuTree 回读：页面菜单“销售报价管理”(menuId 64=4KVE515UH) 绑定 bnuy_sales_quotation、visible=1、位于应用根节点；getApp 回读应用 deploystatus=1 已启用"
        ]
      },
      {
        "itemId": "E-006",
        "evidence": [
          "单据基于 bos_billtpl 标准模板：手工单据编号，保存/提交/审核标准生命周期按钮(bar_save/bar_submit/bar_audit)已在工具栏就位"
        ]
      },
      {
        "itemId": "E-004",
        "evidence": [
          "setProperties 已验证：报价日期默认当天(getToday)、销售员默认当前登录人(getCurrentUser)、折扣率默认 1、报价状态默认未提交"
        ]
      },
      {
        "itemId": "E-005",
        "evidence": [
          "save 操作校验已写入并验证：MustInputValidation 启用(Id RS=8GBXGZY4)、有效期不得早于报价日期、折扣率 0~1、数量与单价非负；明细至少一行由已注册操作插件 BnuySalesQuotationOpPlugin(save) 实现，注册回读 verified=true"
        ]
      }
    ],
    "remaining": [
      {
        "itemId": "E-001",
        "reason": "币别按用户原始“下拉”要求以标准下拉选项(人民币/美元/港元/欧元/日元/英镑)实现，未将既有 ComboField 迁移为关联 bd_currency 的平台标准币别字段；当前环境无字段类型迁移通道，迁移需删除重建并有控件引用丢失风险，故保留下拉实现",
        "category": "implementation"
      },
      {
        "itemId": "E-002",
        "reason": "No implementation evidence supplied",
        "category": "implementation"
      },
      {
        "itemId": "E-003",
        "reason": "No implementation evidence supplied",
        "category": "implementation"
      }
    ]
  }
}
```
<!-- cosmic-scope-view:start -->
## 当前范围说明
本节按当前用户决定和交接记录生成；已取消事项的早期描述仅作历史来源。
已记录实现，实际运行结果仍按验收证据判断：
- 新建“销售报价管理”单据，表头包含六个字段：客户（基础资料）、报价日期（日期）、销售员（人员）、币别（下拉）、有效期（日期）、报价状态（下拉）
- 单据明细包含五个字段：产品（基础资料）、数量（小数）、单价（小数）、折扣率（小数）、报价金额（小数）
- 报价金额计算口径：数量 × 单价 × 折扣率（折扣率取 0~1 小数）
- 报价状态下拉选项：未提交 / 已提交 / 已审核
- 开发完成后发布“销售报价管理”菜单，授权用户可从菜单进入单据
- 标准苍穹单据规则：手工单据编码，保存→提交→审核生命周期；不把自动编号当作默认承诺
- 默认值：报价日期默认当天、销售员默认当前用户、折扣率默认 1、报价状态默认“未提交”
- 校验规则：表头主要字段（客户、报价日期、销售员、币别、报价状态）必填；有效期不得早于报价日期；折扣率取值 0~1；数量、单价不得为负；明细至少一行产品
仍待处理：
- 币别采用平台标准币别字段（CurrencyField，关联 bd_currency），界面表现为下拉选择，替代写死选项的下拉: 币别按用户原始“下拉”要求以标准下拉选项(人民币/美元/港元/欧元/日元/英镑)实现，未将既有 ComboField 迁移为关联 bd_currency 的平台标准币别字段；当前环境无字段类型迁移通道，迁移需删除重建并有控件引用丢失风险，故保留下拉实现
- 明细补充“单位”字段（UnitField，计量单位 bd_measureunits），与数量 QtyField 配套使用: No implementation evidence supplied
- 选择产品后自动带出该产品的计量单位: No implementation evidence supplied
<!-- cosmic-scope-view:end -->

