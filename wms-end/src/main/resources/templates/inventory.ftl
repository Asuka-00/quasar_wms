<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>盘点单</title>
    <style>
        .container {
            width: 1000px;
            margin: 0 auto;
            padding: 20px;
        }
        .header {
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 20px;
        }
        .form-group {
            margin-bottom: 15px;
            display: flex;
            justify-content: space-between;
        }
        .form-group label {
            font-weight: bold;
            width: 120px;
        }
        .form-group .value {
            flex: 1;
            background-color: #f1f1f1;
            padding: 5px;
            height: 20px;
            border-radius: 4px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        table th, table td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }
        table th {
            background-color: #f2f2f2;
        }
        .bottom {
            margin-top: 20px;
            display: flex;
            justify-content: space-between;
            font-weight: bold;
            width: 120px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">盘点单</div>

    <div class="form-group">
        <label>盘点单号</label>
        <div class="value">${(inventory.inventoryNo)!''}</div>
    </div>
    <div class="form-group">
        <label>仓库名称</label>
        <div class="value">${(inventory.warehouseName)!''}</div>
    </div>
    <div class="form-group">
        <label>盘点单状态</label>
        <div class="value">${(inventory.statusDesc)!''}</div>
    </div>
    <div class="form-group">
        <label>备注</label>
        <div class="value">${(inventory.remark)!''}</div>
    </div>

    <table>
        <thead>
        <tr>
            <th>商品名称</th>
            <th>记录数量</th>
            <th>实际数量</th>
            <th>差异</th>
            <th>结果</th>
        </tr>
        </thead>
        <tbody>
        <#if details?? && details?size gt 0>
            <#list details as item>
                <tr>
                    <td>${(item.productName)!''}</td>
                    <td>${(item.systemQuantity)!''}</td>
                    <td>${(item.actualQuantity)!''}</td>
                    <td>${(item.diff)!''}</td>
                    <td>${(item.status)!''}</td>
                </tr>
            </#list>
        <#else>
            <tr>
                <td colspan="5" style="text-align: center;">暂无数据</td>
            </tr>
        </#if>
        </tbody>
    </table>
    <div class="bottom">
        <div class="form-group">
            <label>审核人:</label>
            <div class="value"></div>
        </div>

        <div class="form-group">
            <label>日期:</label>
            <div class="value"></div>
        </div>
    </div>
</div>
</body>
</html>
