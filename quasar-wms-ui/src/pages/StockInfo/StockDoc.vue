<template>
    <div class="q-pa-md">
        <div class="row justify-between">
            <div class="col-2">
                <q-input v-model="search.documentNo" label="单据编号" dense outlined @keyup.enter="freshData" />
            </div>
            <div class="col-2 q-ml-md">
                <q-select v-model="search.documentType" label="单据类型" dense outlined :options="documentTypeOptions"
                    option-label="documentTypeName" option-value="id" emit-value map-options
                    @update:model-value="freshData" clearable />
            </div>
            <div class="col-2 q-ml-md">
                <q-select v-model="search.status" label="单据状态" dense outlined :options="statusOptions"
                    option-label="statusName" option-value="id" emit-value map-options @update:model-value="freshData"
                    clearable />
            </div>
            <div class="col-2 q-ml-md">
                <q-select v-model="search.warehouseId" label="仓库名称" dense outlined :options="warehouseOptions"
                    option-label="warehouseName" option-value="id" emit-value map-options
                    @update:model-value="freshData" clearable />
            </div>
            <q-space />
            <div>
                <q-btn label="新增" icon="add" color="primary" @click="addStockDoc" />
            </div>
        </div>
        <q-table title="出入库单据管理" :rows="rows" row-key="id" :columns="columns" :pagination="pagination" class="q-mt-md">
            <template v-slot:body-cell-action="props">
                <q-td>
                    <q-btn flat round color="primary" icon="visibility" size="sm" @click="viewStockDoc(props.row)">
                        <q-tooltip>查看详情</q-tooltip>
                    </q-btn>
                    <q-btn flat round color="primary" icon="edit" size="sm" @click="editStockDoc(props.row)"
                        :disable="props.row.status !== 0">
                        <q-tooltip>编辑</q-tooltip>
                    </q-btn>
                    <q-btn flat round color="primary" icon="check" size="sm" @click="auditStockDoc(props.row)"
                        :disable="props.row.status !== 0 || props.row.operatorUser !== userStore.id">
                        <q-tooltip>审核</q-tooltip>
                    </q-btn>
                    <q-btn flat round color="primary" icon="check_circle" size="sm" @click="confirmStockDoc(props.row)"
                        :disable="props.row.status !== 1">
                        <q-tooltip>确认</q-tooltip>
                    </q-btn>
                    <q-btn flat round color="negative" icon="cancel" size="sm" @click="cancelStockDoc(props.row)"
                        :disable="props.row.status !== 0">
                        <q-tooltip>取消</q-tooltip>
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
    </div>

    <!-- 新增/编辑出入库单 -->
    <q-dialog v-model="dialogVisible" persistent>
        <q-card style="min-width: 550px">
            <q-card-section>
                <div class="text-h6">{{ addStockDocDialog ? '新增出入库单' : '编辑出入库单' }}</div>
                <q-form ref="formRef">
                    <q-select v-model="formData.documentType" label="单据类型" dense outlined :options="documentTypeOptions"
                        option-label="documentTypeName" option-value="id" emit-value map-options clearable
                        :rules="[(val) => val !== null || '单据类型不能为空']" />
                    <q-select v-model="formData.warehouseId" label="仓库名称" dense outlined :options="warehouseOptions"
                        option-label="warehouseName" option-value="id" emit-value map-options clearable @update:model-value="changeWarehouse"
                        :rules="[(val) => val !== null || '仓库名称不能为空']" />
                    <q-select v-model="formData.operatorUser" label="审批人" dense outlined :options="operatorUserOptions"
                        option-label="nickName" option-value="id" emit-value map-options clearable
                        :rules="[(val) => val !== null || '审批人不能为空']" />
                    <q-input v-model="formData.remark" label="备注" dense outlined />
                    <!-- 子表格 -->
                    <q-table :rows="formData.items" :columns="stockDocAddColumns" row-key="id" class="q-mt-md">
                        <template v-slot:body-cell-productName="props">
                            <q-td>
                                <q-select v-model="props.row.productId" label="产品名称" dense outlined
                                    :options="productOptions" option-label="productName" option-value="id" emit-value
                                    map-options clearable :rules="[(val) => val !== null || '产品名称不能为空']"
                                    @update:model-value="(val) => updateProductName(props.row, val)" />
                            </q-td>
                        </template>
                        <template v-slot:body-cell-quantity="props">
                            <q-td>
                                <q-input v-model.number="props.row.quantity" label="数量" dense outlined type="number"
                                    min="1" step="1" :rules="[
                                        (val) => val !== null || '数量不能为空',
                                        (val) => val > 0 || '数量必须大于0',
                                        (val) => Number.isInteger(val) || '数量必须为整数'
                                    ]" />
                            </q-td>
                        </template>
                        <template v-slot:body-cell-action="props">
                            <q-td>
                                <q-btn flat round color="negative" icon="delete" size="sm"
                                    @click="removeStockDocDetail(props.rowIndex)">
                                    <q-tooltip>删除</q-tooltip>
                                </q-btn>
                            </q-td>
                        </template>
                    </q-table>
                    <q-btn label="添加明细" color="primary" class="q-mt-md" @click="addStockDocDetail" />
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
                <div class="text-h6">出入库单详情</div>
                <div class="row q-col-gutter-md q-mt-md">
                    <div class="col-6">
                        <q-input v-model="viewData.documentNo" label="单据编号" dense outlined readonly />
                    </div>
                    <div class="col-6">
                        <q-select v-model="viewData.documentType" label="单据类型" dense outlined
                            :options="documentTypeOptions" option-label="documentTypeName" option-value="id" emit-value
                            map-options readonly />
                    </div>
                    <div class="col-6">
                        <q-select v-model="viewData.warehouseId" label="仓库名称" dense outlined :options="warehouseOptions"
                            option-label="warehouseName" option-value="id" emit-value map-options readonly />
                    </div>
                    <div class="col-6">
                        <q-select v-model="viewData.operatorUser" label="审批人" dense outlined
                            :options="operatorUserOptions" option-label="nickName" option-value="id" emit-value
                            map-options readonly />
                    </div>
                    <div class="col-12">
                        <q-input v-model="viewData.remark" label="备注" dense outlined readonly />
                    </div>
                </div>
                <!-- 明细表格 -->
                <q-table :rows="viewData.items" :columns="stockDocDetailsColumns" row-key="id" class="q-mt-md">
                </q-table>
            </q-card-section>
            <q-card-actions align="right">
                <q-btn label="关闭" color="primary" @click="viewDialogVisible = false" />
            </q-card-actions>
        </q-card>
    </q-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useQuasar, type QTableColumn, type QForm } from 'quasar';
import axios from '@/axios/index';
import { useUserStore } from '@/stores/userStore';

const $q = useQuasar();
const userStore = useUserStore();

const rows = ref([]);
const columns = ref<QTableColumn[]>([
    { name: 'index', label: '#', field: 'index', align: "left" },
    { name: 'documentNo', label: '单据编号', field: 'documentNo', align: "left" },
    { name: 'documentType', label: '单据类型', field: 'documentType', align: "left" },
    { name: 'warehouseName', label: '仓库名称', field: 'warehouseName', align: "left" },
    { name: 'status', label: '单据状态', field: 'status', align: "left", format: (val: number) => statusMap[val as keyof typeof statusMap] || String(val) },
    { name: 'operatorUserName', label: '审批人', field: 'operatorUserName', align: "left" },
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
    documentNo: '',
    documentType: '',
    warehouseId: '',
    status: '',
});

const statusMap = {
    0: '进行中',
    1: '已审批',
    2: '已完成',
    3: '已取消'
};

const freshData = async () => {
    const res = await axios.post('/web/stock/list', {
        documentNo: search.value.documentNo,
        documentType: search.value.documentType,
        warehouseId: search.value.warehouseId,
        status: search.value.status,
    }, {
        params: {
            current: pagination.value.page,
            size: pagination.value.rowsPerPage,
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

const documentTypeOptions = ref([
    { id: 'IN', documentTypeName: '入库' },
    { id: 'OUT', documentTypeName: '出库' }
]);

const statusOptions = ref([
    { id: 0, statusName: '进行中' },
    { id: 1, statusName: '已审批' },
    { id: 2, statusName: '已完成' },
    { id: 3, statusName: '已取消' },
]);

const operatorUserOptions = ref([]);

const stockDocAddColumns = ref<QTableColumn[]>([
    { name: 'productName', label: '产品名称', field: 'productName', align: "left" },
    { name: 'quantity', label: '数量', field: 'quantity', align: "left" },
    { name: 'action', label: '操作', field: 'action', align: "left" }
]);

const stockDocDetailsColumns = ref<QTableColumn[]>([
    { name: 'productName', label: '产品名称', field: 'productName', align: "left" },
    { name: 'quantity', label: '数量', field: 'quantity', align: "left" },
    { name: 'remainingStockQuantity', label: '库存剩余数量', field: 'remainingStockQuantity', align: "left",
        format: (val: number) => val !== undefined ? String(val) : '-' }
]);

interface StockDocDetail {
    documentId?: number;
    id?: number;
    productId: number | null;
    quantity: number | null;
    productName?: string;
}

interface FormData {
    id?: number;
    documentNo: string;
    documentType: number | null;
    items: StockDocDetail[];
    operatorUser: number | null;
    remark: string;
    status: number;
    warehouseId: number | null;
}

const dialogVisible = ref(false);
const addStockDocDialog = ref(false);
const formData = ref<FormData>({
    documentNo: '',
    documentType: null,
    items: [],
    operatorUser: null,
    remark: '',
    status: 0,
    warehouseId: null
});

const formRef = ref<QForm | null>(null);

const changeWarehouse = () => {
    getProductOptions();
    formData.value.items.splice(0, formData.value.items.length);
}

const addStockDoc = () => {
    dialogVisible.value = true;
    addStockDocDialog.value = true;
    formData.value = {
        documentNo: '',
        documentType: null,
        items: [],
        operatorUser: null,
        remark: '',
        status: 0,
        warehouseId: null
    };
}

const editStockDoc = async (row: any) => {
    dialogVisible.value = true;
    addStockDocDialog.value = false;
    //调用接口获取单据详情
    await getStockDocDetail(row.id);
    formData.value = {
        id: row.id,
        documentNo: row.documentNo,
        documentType: row.documentType,
        items: viewData.value.items,
        operatorUser: +row.operatorUser,
        remark: row.remark,
        status: +row.status,
        warehouseId: +row.warehouseId
    };
}

const viewDialogVisible = ref(false);
const viewData = ref({
    documentNo: '',
    documentType: null,
    items: [],
    operatorUser: 0,
    remark: '',
    status: 0,
    warehouseId: null
});

const viewStockDoc = async (row: any) => {
    viewDialogVisible.value = true;
    viewData.value = {
        documentNo: row.documentNo,
        documentType: row.documentType,
        items: [],
        operatorUser: +row.operatorUser,
        remark: row.remark,
        status: row.status,
        warehouseId: row.warehouseId
    };
    await getRemainingStock(row.warehouseId);
    await getStockDocDetail(row.id);
}

const getStockDocDetail = async (id: number) => {
    // 获取单据详情
    await axios.get('/web/stock/detail', {
        params: {
            id: id
        }
    }).then(res => {
        if (res.data.code === 200) {
            viewData.value.items = res.data.data.map((item: any, index: number) => ({
                ...item,
                index: index + 1,
            }));
            
            // 如果是出库单，更新剩余库存
            if (remainingStockList.value.length > 0) {
                viewData.value.items.forEach((item: any) => {
                    const remainingStock = remainingStockList.value.find((stock: RemainingStock) => 
                        stock.productId === item.productId);
                    if (remainingStock) {
                        item.remainingStockQuantity = remainingStock.quantity;
                    }
                });
            }
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

interface RemainingStock {
    productId: number;
    quantity: number;
}

const remainingStockList = ref<RemainingStock[]>([]);
const getRemainingStock = async (warehouseId: number) => {
    const res = await axios.post('/web/realStock/list', {
        warehouseId: warehouseId
    },{
        params: {
            current: 1,
            size: 10000,
        }
    });
    if (res.data.code === 200) {
        remainingStockList.value = res.data.data.records;
    }
}

const auditStockDoc = (row: any) => {
    $q.dialog({
        title: '确认',
        message: '是否确认审核该出入库单？',
        cancel: true,
        persistent: true,
        ok: {
            label: '确认',
        }
    }).onOk(async () => {
        const res = await axios.post('/web/stock/audit', { },{
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

const confirmStockDoc = (row: any) => {
    $q.dialog({
        title: '确认',
        message: '是否确认确认该出入库单？',
        cancel: true,
        persistent: true,
        ok: {
            label: '确认',
        }
    }).onOk(async () => {
        const res = await axios.post('/web/stock/complete', { },{
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

const cancelStockDoc = (row: any) => {
    $q.dialog({
        title: '确认',
        message: '是否确认取消该出入库单？',
        cancel: true,
        persistent: true,
        ok: {
            label: '确认',
        }
    }).onOk(async () => {
        const res = await axios.post('/web/stock/cancel', { },{
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
};

const getOperatorUserOptions = async () => {
    const res = await axios.post('/web/user/list', {}, {
        params: {
            current: 1,
            size: 10000
        }
    });
    if (res.data.code === 200) {
        operatorUserOptions.value = res.data.data.records;
    }
}

const addStockDocDetail = () => {
    formData.value.items.push({
        productId: null,
        quantity: null
    });
    console.log(formData.value.items);
}

const removeStockDocDetail = (index: number) => {
    formData.value.items.splice(index, 1);
}

const submitForm = async () => {
    const valid = await formRef.value?.validate();
    if (!valid) {
        return;
    }
    if (addStockDocDialog.value) {
        const res = await axios.post('/web/stock/saveDoc', formData.value);
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
        const res = await axios.post('/web/stock/updateDoc', formData.value);
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

const updateProductName = (row: any, productId: number) => {
    const product = productOptions.value.find((p: any) => p.id === productId);
    if (product) {
        row.productName = product.productName;
    }
}

onMounted(() => {
    freshData();
    getWarehouseOptions();
    getOperatorUserOptions();
    getProductOptions();
});

</script>

<style scoped></style>
