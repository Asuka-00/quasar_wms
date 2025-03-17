<template>
    <div class="q-pa-md">
        <div class="row justify-between">
            <div class="col-2">
                <q-input v-model="search.inventoryNo" label="盘点单号" dense outlined @keyup.enter="freshData" />
            </div>
            <div class="col-2 q-ml-md">
                <q-select v-model="search.warehouseId" label="仓库名称" dense outlined :options="warehouseOptions"
                    option-label="warehouseName" option-value="id" emit-value map-options
                    @update:model-value="freshData" clearable />
            </div>
            <q-space />
            <div>
                <q-btn label="新增" icon="add" color="primary" @click="addInventory" />
            </div>
        </div>
        <q-table title="库存盘点管理" :rows="rows" row-key="id" :columns="columns" :pagination="pagination" class="q-mt-md">
            <template v-slot:body-cell-action="props">
                <q-td>
                    <q-btn flat round color="primary" icon="visibility" size="sm" @click="viewInventory(props.row)">
                        <q-tooltip>查看详情</q-tooltip>
                    </q-btn>
                    <q-btn flat round color="primary" icon="edit" size="sm" @click="editInventory(props.row)"
                        :disable="props.row.status !== 0">
                        <q-tooltip>编辑</q-tooltip>
                    </q-btn>
                    <q-btn flat round color="primary" icon="check" size="sm" @click="auditInventory(props.row)"
                        :disable="props.row.status !== 0">
                        <q-tooltip>盘点</q-tooltip>
                    </q-btn>
                    <q-btn flat round color="primary" icon="check_circle" size="sm" @click="confirmInventory(props.row)"
                        :disable="props.row.status !== 1">
                        <q-tooltip>确认</q-tooltip>
                    </q-btn>
                    <q-btn flat round color="negative" icon="cancel" size="sm" @click="cancelInventory(props.row)"
                        :disable="props.row.status !== 0">
                        <q-tooltip>取消</q-tooltip>
                    </q-btn>
                    <q-btn flat round color="primary" icon="print" size="sm" @click="printInventory(props.row)">
                        <q-tooltip>打印</q-tooltip>
                    </q-btn>
                </q-td>
            </template>
            <template v-slot:bottom>
                <div class="row col-12 justify-center q-mt-md">
                    <q-pagination v-model="pagination.page"
                        :max="Math.ceil(pagination.rowsNumber / pagination.rowsPerPage)" @update:model-value="freshData"
                        input />
                </div>
            </template>
        </q-table>

        <!-- 新增/编辑盘点单 -->
        <q-dialog v-model="dialogVisible" persistent>
            <q-card style="min-width: 550px">
                <q-card-section>
                    <div class="text-h6">{{ addInventoryDialog ? '新增盘点单' : '编辑盘点单' }}</div>
                    <q-form ref="formRef">
                        <q-select v-model="formData.warehouseId" label="仓库名称" dense outlined :options="warehouseOptions"
                            option-label="warehouseName" option-value="id" emit-value map-options clearable
                            @update:model-value="changeWarehouse" :rules="[(val) => val !== null || '仓库名称不能为空']" />
                        <q-input v-model="formData.remark" label="备注" dense outlined />
                        <!-- 子表格 -->
                        <q-table :rows="formData.items" :columns="inventoryDetailsColumns" row-key="id" class="q-mt-md"
                            hide-bottom>
                            <template v-slot:body-cell-productName="props">
                                <q-td>
                                    <q-select v-model="props.row.productId" label="产品名称" dense outlined
                                        :options="productOptions" option-label="productName" option-value="id"
                                        emit-value map-options clearable :rules="[(val) => val !== null || '产品名称不能为空']"
                                        @update:model-value="(val) => updateProductName(props.row, val)" />
                                </q-td>
                            </template>
                            <template v-slot:body-cell-action="props">
                                <q-td>
                                    <q-btn flat round color="negative" icon="delete" size="sm"
                                        @click="removeInventoryDetail(props.rowIndex)">
                                        <q-tooltip>删除</q-tooltip>
                                    </q-btn>
                                </q-td>
                            </template>
                        </q-table>
                        <q-btn label="添加明细" color="primary" class="q-mt-md" @click="addInventoryDetail" />
                    </q-form>
                </q-card-section>
                <q-card-actions align="right">
                    <q-btn label="取消" color="negative" @click="dialogVisible = false" />
                    <q-btn label="提交" color="primary" @click="submitForm" />
                </q-card-actions>
            </q-card>
        </q-dialog>

        <!-- 查看详情弹窗 -->
        <q-dialog v-model="viewDialogVisible" persistent>
            <q-card style="min-width: 550px">
                <q-card-section>
                    <div class="text-h6">盘点单详情</div>
                    <div class="row q-col-gutter-md q-mt-md">
                        <div class="col-6">
                            <q-input v-model="viewData.inventoryNo" label="盘点单号" dense outlined readonly />
                        </div>
                        <div class="col-6">
                            <q-select v-model="viewData.warehouseId" label="仓库名称" dense outlined
                                :options="warehouseOptions" option-label="warehouseName" option-value="id" emit-value
                                map-options readonly />
                        </div>
                        <div class="col-12">
                            <q-input v-model="viewData.remark" label="备注" dense outlined readonly />
                        </div>
                    </div>
                    <!-- 明细表格 -->
                    <q-table :rows="viewData.items" :columns="viewDetailsColumns" row-key="id" class="q-mt-md"
                        hide-bottom>
                    </q-table>
                </q-card-section>
                <q-card-actions align="right">
                    <q-btn label="关闭" color="primary" @click="viewDialogVisible = false" />
                </q-card-actions>
            </q-card>
        </q-dialog>

        <!-- 盘点弹窗 -->
        <q-dialog v-model="inventoryDialogVisible" persistent>
            <q-card style="min-width: 550px">
                <q-card-section>
                    <div class="text-h6">盘点</div>
                    <div class="row q-col-gutter-md q-mt-md">
                        <div class="col-6">
                            <q-input v-model="inventoryData.inventoryNo" label="盘点单号" dense outlined readonly />
                        </div>
                        <div class="col-6">
                            <q-select v-model="inventoryData.warehouseId" label="仓库名称" dense outlined
                                :options="warehouseOptions" option-label="warehouseName" option-value="id" emit-value
                                map-options readonly />
                        </div>
                        <div class="col-12">
                            <q-input v-model="inventoryData.remark" label="备注" dense outlined readonly />
                        </div>
                    </div>
                    <!-- 明细表格 -->
                    <q-table :rows="inventoryData.items" :columns="inventoryDetailsColumns" row-key="id" class="q-mt-md"
                        hide-bottom>
                        <template v-slot:body-cell-actualQuantity="props">
                            <q-td>
                                <q-input v-model.number="props.row.actualQuantity" label="实际数量" dense outlined
                                    type="number" min="0" step="1" :rules="[
                                        (val) => val !== null || '实际数量不能为空',
                                        (val) => val >= 0 || '实际数量必须大于等于0',
                                        (val) => Number.isInteger(val) || '实际数量必须为整数'
                                    ]" />
                            </q-td>
                            <q-td>
                                <q-btn flat round color="negative" icon="delete" size="sm"
                                    @click="removeInventoryDetail(props.rowIndex)">
                                    <q-tooltip>删除</q-tooltip>
                                </q-btn>
                            </q-td>
                        </template>
                    </q-table>
                </q-card-section>
                <q-card-actions align="right">
                    <q-btn label="取消" color="negative" @click="inventoryDialogVisible = false" />
                    <q-btn label="提交" color="primary" @click="submitInventory" />
                </q-card-actions>
            </q-card>
        </q-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useQuasar, type QTableColumn, type QForm } from 'quasar';
import axios from '@/axios/index';

const $q = useQuasar();

const rows = ref([]);
const columns = ref<QTableColumn[]>([
    { name: 'index', label: '#', field: 'index', align: "left" },
    { name: 'inventoryNo', label: '盘点单号', field: 'inventoryNo', align: "left" },
    { name: 'warehouseName', label: '仓库名称', field: 'warehouseName', align: "left" },
    { name: 'status', label: '单据状态', field: 'status', align: "left", format: (val: number) => statusMap[val as keyof typeof statusMap] || String(val) },
    { name: 'remark', label: '备注', field: 'remark', align: "left" },
    { name: 'createdBy', label: '创建人', field: 'createdBy', align: "left" },
    { name: 'createdTime', label: '创建时间', field: 'createdTime', align: "left" },
    { name: 'updatedBy', label: '更新人', field: 'updatedBy', align: "left" },
    { name: 'updatedTime', label: '更新时间', field: 'updatedTime', align: "left" },
    { name: 'action', label: '操作', field: 'action', align: "left" },
]);

const pagination = ref({
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0,
});

const search = ref({
    inventoryNo: '',
    warehouseId: '',
});

const statusMap = {
    0: '进行中',
    1: '已盘点',
    2: '已完成',
    3: '已取消'
};

const freshData = async () => {
    const res = await axios.get('/web/inventory/list', {
        params: {
            current: pagination.value.page,
            size: pagination.value.rowsPerPage,
            inventoryNo: search.value.inventoryNo,
            warehouseId: search.value.warehouseId,
        }
    });
    if (res.data.code === 200) {
        rows.value = res.data.data.records.map((item: any, index: number) => ({
            ...item,
            index: (pagination.value.page - 1) * pagination.value.rowsPerPage + index + 1,
        }));
        pagination.value.rowsNumber = res.data.data.total;
    } else {
        $q.notify({
            message: res.data.message,
            color: 'negative',
            position: 'top',
            timeout: 2000,
        });
    }
};

const warehouseOptions = ref([]);

const getWarehouseOptions = async () => {
    const res = await axios.get('/web/warehouse/list', {
        params: {
            current: 1,
            size: 10000,
        }
    });
    if (res.data.code === 200) {
        warehouseOptions.value = res.data.data.records;
    }
};

interface InventoryDetail {
    id?: number;
    inventoryId?: number;
    productId: number | null;
    productName?: string;
    systemQuantity: number | null;
    actualQuantity: number | null;
}

interface FormData {
    id?: number;
    inventoryNo?: string;
    warehouseId: number | null;
    items: InventoryDetail[];
    remark: string;
    status: number;
}

const dialogVisible = ref(false);
const addInventoryDialog = ref(false);
const formData = ref<FormData>({
    warehouseId: null,
    items: [],
    remark: '',
    status: 0
});

const formRef = ref<QForm | null>(null);

const inventoryDetailsColumns = ref<QTableColumn[]>([
    { name: 'productName', label: '产品名称', field: 'productName', align: "left" },
    { name: 'systemQuantity', label: '系统数量', field: 'systemQuantity', align: "left" },
    { name: 'actualQuantity', label: '实际数量', field: 'actualQuantity', align: "left" },
    { name: 'action', label: '操作', field: 'action', align: "left" }
]);

interface Product {
    id: number;
    productName: string;
}

const productOptions = ref<Product[]>([]);

const getProductOptions = async () => {
    const res = await axios.get('/web/product/list', {
        params: {
            current: 1,
            size: 10000,
            warehouseId: formData.value.warehouseId
        }
    });
    if (res.data.code === 200) {
        productOptions.value = res.data.data.records;
    }
}

const changeWarehouse = () => {
    getProductOptions();
    formData.value.items.splice(0, formData.value.items.length);
}

const addInventory = () => {
    dialogVisible.value = true;
    addInventoryDialog.value = true;
    formData.value = {
        warehouseId: null,
        items: [],
        remark: '',
        status: 0
    };
}

const addInventoryDetail = () => {
    formData.value.items.push({
        productId: null,
        systemQuantity: null,
        actualQuantity: null
    });
}

const removeInventoryDetail = (index: number) => {
    formData.value.items.splice(index, 1);
}

const updateProductName = (row: any, productId: number) => {
    const product = productOptions.value.find((p: any) => p.id === productId);
    if (product) {
        row.productName = product.productName;
        row.systemQuantity = null;
        row.actualQuantity = null;
    }
}

const submitForm = async () => {
    const valid = await formRef.value?.validate();
    if (!valid) {
        return;
    }
    if (addInventoryDialog.value) {
        const res = await axios.post('/web/inventory/save', formData.value);
        if (res.data.code === 200) {
            $q.notify({
                message: res.data.message,
                color: 'positive',
                position: 'top',
                timeout: 2000,
            });
            dialogVisible.value = false;
            freshData();
        } else {
            $q.notify({
                message: res.data.message,
                color: 'negative',
                position: 'top',
                timeout: 2000,
            });
        }
    } else {
        const res = await axios.post('/web/inventory/update', formData.value);
        if (res.data.code === 200) {
            $q.notify({
                message: res.data.message,
                color: 'positive',
                position: 'top',
                timeout: 2000,
            });
            dialogVisible.value = false;
            freshData();
        } else {
            $q.notify({
                message: res.data.message,
                color: 'negative',
                position: 'top',
                timeout: 2000,
            });
        }
    }
}

interface ViewData {
    id?: number;
    inventoryNo: string;
    warehouseId: number | null;
    items: InventoryDetail[];
    remark: string;
    status: number;
}

const viewDialogVisible = ref(false);
const viewData = ref<ViewData>({
    inventoryNo: '',
    warehouseId: null,
    items: [],
    remark: '',
    status: 0
});

const viewDetailsColumns = ref<QTableColumn[]>([
    { name: 'productName', label: '产品名称', field: 'productName', align: "left" },
    { name: 'systemQuantity', label: '系统数量', field: 'systemQuantity', align: "left" },
    { name: 'actualQuantity', label: '实际数量', field: 'actualQuantity', align: "left" },
    { name: 'status', label: '盘点状态', field: 'status', align: "left" }
]);

const viewInventory = async (row: any) => {
    viewDialogVisible.value = true;
    viewData.value = {
        inventoryNo: row.inventoryNo,
        warehouseId: row.warehouseId,
        items: [],
        remark: row.remark,
        status: row.status
    };
    await getInventoryDetail(row.id);
}

const getInventoryDetail = async (id: number) => {
    const res = await axios.get('/web/inventory/detail', {
        params: {
            id: id
        }
    });
    if (res.data.code === 200) {
        viewData.value.items = res.data.data.map((item: any) => {
            if (item.diff > 0) {
                item.status = '盘盈';
            } else if (item.diff < 0) {
                item.status = '盘亏';
            } else {
                item.status = '正常';
            }
            return item;
        });
    } else {
        $q.notify({
            message: res.data.message,
            color: 'negative',
            position: 'top',
            timeout: 2000,
        });
    }
}

const editInventory = async (row: any) => {
    dialogVisible.value = true;
    addInventoryDialog.value = false;
    // 获取盘点单详情
    await getInventoryDetail(row.id);
    formData.value = {
        id: row.id,
        inventoryNo: row.inventoryNo,
        warehouseId: row.warehouseId,
        items: viewData.value.items,
        remark: row.remark,
        status: row.status
    };
}

const inventoryDialogVisible = ref(false);
const inventoryData = ref<ViewData>({
    id: 0,
    inventoryNo: '',
    warehouseId: null,
    items: [],
    remark: '',
    status: 0
});

const auditInventory = async (row: any) => {
    inventoryDialogVisible.value = true;
    await getInventoryDetail(row.id);
    inventoryData.value = {
        id: row.id,
        inventoryNo: row.inventoryNo,
        warehouseId: row.warehouseId,
        items: viewData.value.items,
        remark: row.remark,
        status: row.status
    };
}

const submitInventory = async () => {
    const res = await axios.post('/web/inventory/inventory', inventoryData.value);
    if (res.data.code === 200) {
        $q.notify({
            message: res.data.message,
            color: 'positive',
            position: 'top',
            timeout: 2000,
        });
        inventoryDialogVisible.value = false;
        freshData();
    } else {
        $q.notify({
            message: res.data.message,
            color: 'negative',
            position: 'top',
            timeout: 2000,
        });
    }
}

const confirmInventory = async (row: any) => {
    $q.dialog({
        title: '确认盘点',
        message: '确认盘点后，盘点单将无法修改',
        cancel: true,
        ok: true,
    }).onOk(async () => {
        const res = await axios.post('/web/inventory/complete', {}, {
            params: {
                id: row.id
            }
        });
        if (res.data.code === 200) {
            $q.notify({
                message: res.data.message,
                color: 'positive',
                position: 'top',
                timeout: 2000,
            });
            freshData();
        } else {
            $q.notify({
                message: res.data.message,
                color: 'negative',
                position: 'top',
                timeout: 2000,
            });
        }
    });
}

const cancelInventory = (row: any) => {
    $q.dialog({
        title: '取消盘点',
        message: '取消盘点后，盘点单将无法修改',
        cancel: true,
        ok: true,
    }).onOk(async () => {
        const res = await axios.post('/web/inventory/cancel', {}, {
            params: {
                id: row.id
            }
        });
        if (res.data.code === 200) {
            $q.notify({
                message: res.data.message,
                color: 'positive',
                position: 'top',
                timeout: 2000,
            });
            freshData();
        } else {
            $q.notify({
                message: res.data.message,
                color: 'negative',
                position: 'top',
                timeout: 2000,
            });
        }
    });
}

const printInventory = async (row: any) => {
    const res = await axios.post('/web/inventory/print', {}, {
        params: {
            id: row.id
        }
    });
    if (res.data.code === 200) {
        window.open(res.data.data, '_blank');
    } else {
        $q.notify({
            message: res.data.message,
            color: 'negative',
            position: 'top',
            timeout: 2000,
        });
    }
}

onMounted(() => {
    freshData();
    getWarehouseOptions();
    getProductOptions();
});
</script>

<style scoped></style>
